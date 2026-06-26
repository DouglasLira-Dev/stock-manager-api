package com.estoque.api.service;

import com.estoque.api.dto.ProductRequestDTO;
import com.estoque.api.dto.ProductResponseDTO;
import com.estoque.api.dto.ProductStatsDTO;
import com.estoque.api.model.MovimentationType;
import com.estoque.api.model.Product;
import com.estoque.api.model.User;
import com.estoque.api.repository.ProductRepository;
import com.estoque.api.repository.UserRepository;
import com.estoque.api.exception.InsufficientStockException;
import com.estoque.api.exception.ProductNotFoundException;
import com.estoque.api.exception.UnauthorizedProductAccessException;
import com.estoque.api.exception.UserNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MovimentationService movimentationService;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository,
                            UserRepository userRepository,
                            MovimentationService movimentationService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.movimentationService = movimentationService;
    }

    // Obtém o usuário autenticado
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    // 1. Criar produto
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        User user = getAuthenticatedUser();
        logger.info("Usuário {} criando novo produto: {}", user.getEmail(), dto.name());

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setQuantity(dto.quantity());
        product.setPrice(dto.price());
        product.setCategory(dto.category());
        product.setUser(user);
        product.setMinimumQuantity(dto.minimumQuantity() != null ? dto.minimumQuantity() : 0); // Define quantidade mínima, default 0

        Product saved = productRepository.save(product);
        logger.info("Produto criado com sucesso: {} (ID: {})", saved.getName());
        return mapToResponseDTO(saved);

    }

    // 2. Listar produtos do usuário (com paginação e filtro por categoria)
    public Page<ProductResponseDTO> listUserProducts(String category, String name, Pageable pageable) {
        User user = getAuthenticatedUser();

        Page<Product> products;

        if (category != null && !category.isEmpty() && name != null && !name.isEmpty()) {
            products = productRepository.findByUserIdAndCategoryContainingIgnoreCaseAndNameContainingIgnoreCase(
                    user.getId(), category, name, pageable);
        } else if (category != null && !category.isEmpty()) {
            products = productRepository.findByUserIdAndCategoryContainingIgnoreCase(user.getId(), category, pageable);
        } else if (name != null && !name.isEmpty()) {
            products = productRepository.findByUserIdAndNameContainingIgnoreCase(user.getId(), name, pageable);
        } else {
            products = productRepository.findByUserId(user.getId(), pageable);
        }

        return products.map(this::mapToResponseDTO);
    }

    // 3. Buscar produto por ID (validando que pertence ao usuário)
    public ProductResponseDTO getProductById(Long id) {
        User user = getAuthenticatedUser();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedProductAccessException(id, user.getId());
        }

        return mapToResponseDTO(product);
    }

    // 4. Atualizar produto
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        User user = getAuthenticatedUser();
        logger.info("Usuário {} atualizando produto ID: {}", user.getEmail(), id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado para atualização: ID {}", id);
                    return new ProductNotFoundException(id);
                });

        if (!product.getUser().getId().equals(user.getId())) {
            logger.warn("Usuário {} tentou atualizar produto ID: {} sem permissão", user.getEmail(), id);
            throw new UnauthorizedProductAccessException(id, user.getId());
        }

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(dto.category());
        // ATENÇÃO: Atualização de quantidade é feita por endpoints específicos (entrada/saída)
        // Não atualizamos a quantidade aqui para evitar inconsistências.
        // Se quiser permitir, descomente a linha abaixo:
        // product.setQuantity(dto.quantity());

        Product updated = productRepository.save(product);
        logger.info("Produto atualizado com sucesso: {} (ID: {})", updated.getName(), updated.getId());
        return mapToResponseDTO(updated);
    }

    // 5. Deletar produto
    public void deleteProduct(Long id) {
        User user = getAuthenticatedUser();
        logger.info("Usuário {} deletando produto ID: {}", user.getEmail(), id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado para deleção: ID {}", id);
                    return new ProductNotFoundException(id);
                });

        if (!product.getUser().getId().equals(user.getId())) {
            logger.warn("Usuário {} tentou deletar produto ID: {} sem permissão", user.getEmail(), id);
            throw new UnauthorizedProductAccessException(id, user.getId());
        }

        productRepository.delete(product);
        logger.info("Produto deletado com sucesso: {} (ID: {})", product.getName(), product.getId());
    }

    // 6. REGRA DE NEGÓCIO: ENTRADA DE ESTOQUE (adicionar quantidade)
    public ProductResponseDTO addStock(Long id, Integer quantityToAdd) {
    // A validação @Positive no DTO já garante que quantityToAdd > 0
    // Não precisa de validação manual aqui
    
        User user = getAuthenticatedUser();
        logger.info("Usuário {} adicionando {} unidades ao produto ID: {}", user.getEmail(), quantityToAdd, id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado para entrada de estoque: {}", id);
                    return new ProductNotFoundException(id);
                });

        if (!product.getUser().getId().equals(user.getId())) {
            logger.warn("Usuário {} tentou alterar estoque do produto ID: {} sem permissão", user.getEmail(), id);
            throw new UnauthorizedProductAccessException(id, user.getId());
        }

        Integer previousQuantity = product.getQuantity();
        product.setQuantity(product.getQuantity() + quantityToAdd);
        Product updated = productRepository.save(product);

        movimentationService.registerMovimentation(updated, MovimentationType.ENTRY, previousQuantity, updated.getQuantity());
        logger.info("Estoque atualizado: produto {} - antes: {}, depois: {}", product.getName(), previousQuantity, updated.getQuantity());
        return mapToResponseDTO(updated);
    }

    // 7. REGRA DE NEGÓCIO: SAÍDA DE ESTOQUE (remover quantidade)
        public ProductResponseDTO removeStock(Long id, Integer quantityToRemove) {

        if (quantityToRemove <= 0) {
            logger.warn("Tentativa de remover quantidade inválida: {}", quantityToRemove);
            throw new IllegalArgumentException("Quantidade a remover deve ser positiva");
        }

        User user = getAuthenticatedUser();
        logger.info("Usuário {} removendo {} unidades do produto ID: {}", user.getEmail(), quantityToRemove, id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado para saída de estoque: {}", id);
                    return new ProductNotFoundException(id);
                });

        if (!product.getUser().getId().equals(user.getId())) {
            logger.warn("Usuário {} tentou alterar estoque do produto ID: {} sem permissão", user.getEmail(), id);
            throw new UnauthorizedProductAccessException(id, user.getId());
        }

        if (product.getQuantity() < quantityToRemove) {
            logger.warn("Estoque insuficiente: produto {} - disponível: {}, solicitado: {}", product.getName(), product.getQuantity(), quantityToRemove);
            throw new InsufficientStockException(product.getId(), product.getQuantity(), quantityToRemove);
        }

        Integer previousQuantity = product.getQuantity();
        product.setQuantity(product.getQuantity() - quantityToRemove);
        Product updated = productRepository.save(product);

        movimentationService.registerMovimentation(updated, MovimentationType.EXIT, previousQuantity, updated.getQuantity());
        logger.info("Estoque atualizado: produto {} - antes: {}, depois: {}", product.getName(), previousQuantity, updated.getQuantity());
        return mapToResponseDTO(updated);
    }

    // 8. Listar produtos com ESTOQUE BAIXO (quantidade < 5)
    public List<ProductResponseDTO> listLowStockProducts() {
        User user = getAuthenticatedUser();
        List<Product> products = productRepository.findByUserIdAndQuantityLessThan(user.getId(), 5);
        return products.stream().map(this::mapToResponseDTO).toList();
    }

    // Mapeamento interno
    private ProductResponseDTO mapToResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getQuantity(),
                product.getPrice(),
                product.getCategory(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getMinimumQuantity()
        );
    }
        // ======== ESTATÍSTICAS DO ESTOQUE ========

    /**
     * Retorna estatísticas do estoque do usuário autenticado.
     */
    public ProductStatsDTO getStats() {
        User user = getAuthenticatedUser();

        // Busca todos os produtos do usuário
        List<Product> products = productRepository.findByUserId(user.getId());

        // Calcula as métricas
        Long totalProducts = (long) products.size();

        Long totalItemsInStock = products.stream()
                .mapToLong(Product::getQuantity)
                .sum();

        BigDecimal totalStockValue = products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long lowStockItems = products.stream()
                .filter(p -> p.getQuantity() < 5 && p.getQuantity() > 0)
                .count();

        Long outOfStockItems = products.stream()
                .filter(p -> p.getQuantity() == 0)
                .count();

        return new ProductStatsDTO(
                totalProducts,
                totalItemsInStock,
                totalStockValue,
                lowStockItems,
                outOfStockItems
        );
    }
    // 10. Listar todas as categorias únicas do usuário
    public List<String> listCategories() {
        User user = getAuthenticatedUser();
        List<Product> products = productRepository.findByUserId(user.getId());
        
        return products.stream()
                .map(Product::getCategory)
                .filter(category -> category != null && !category.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    // 11. Listar produtos com estoque abaixo da quantidade mínima definida
    public List<ProductResponseDTO> listProductsBelowMinimum(){
        User user = getAuthenticatedUser();
        List<Product> products = productRepository.findByUserId(user.getId());

        return products.stream()
                .filter(p -> p.getQuantity() < p.getMinimumQuantity())
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
}