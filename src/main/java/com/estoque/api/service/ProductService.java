package com.estoque.api.service;

import com.estoque.api.dto.ProductRequestDTO;
import com.estoque.api.dto.ProductResponseDTO;
import com.estoque.api.dto.ProductStatsDTO;
import com.estoque.api.model.MovimentationType;
import com.estoque.api.model.Product;
import com.estoque.api.model.User;
import com.estoque.api.repository.ProductRepository;
import com.estoque.api.repository.UserRepository;
import com.estoque.api.exception.ProductNotFoundException;
import com.estoque.api.exception.UnauthorizedProductAccessException;
import com.estoque.api.exception.UserNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MovimentationService movimentationService;

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

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setQuantity(dto.quantity());
        product.setPrice(dto.price());
        product.setCategory(dto.category());
        product.setUser(user);
        product.setMinimumQuantity(dto.minimumQuantity() != null ? dto.minimumQuantity() : 0); // Define quantidade mínima, default 0

        Product saved = productRepository.save(product);
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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getUser().getId().equals(user.getId())) {
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
        return mapToResponseDTO(updated);
    }

    // 5. Deletar produto
    public void deleteProduct(Long id) {
        User user = getAuthenticatedUser();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedProductAccessException(id, user.getId());
        }

        productRepository.delete(product);
    }

    // 6. REGRA DE NEGÓCIO: ENTRADA DE ESTOQUE (adicionar quantidade)
    public ProductResponseDTO addStock(Long id, Integer quantityToAdd) {
        if (quantityToAdd <= 0) {
            throw new RuntimeException("Quantidade a adicionar deve ser positiva");
        }

        User user = getAuthenticatedUser();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedProductAccessException(id, user.getId());
        }
        // Guarda a quantidade anterior antes de atualizar o estoque, para fins de registro da movimentação
        Integer previousQuantity = product.getQuantity();

        // Atualiza a quantidade do produto
        product.setQuantity(product.getQuantity() + quantityToAdd);
        Product updated = productRepository.save(product);

        // Registra a movimentação (ENTRY)
        movimentationService.registerMovimentation(
            updated,
            MovimentationType.ENTRY,
            previousQuantity,
             updated.getQuantity()
        );
        
        return mapToResponseDTO(updated);
    }

    // 7. REGRA DE NEGÓCIO: SAÍDA DE ESTOQUE (remover quantidade)
    public ProductResponseDTO removeStock(Long id, Integer quantityToRemove) {
        if (quantityToRemove <= 0) {
            throw new RuntimeException("Quantidade a remover deve ser positiva");
        }

        User user = getAuthenticatedUser();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedProductAccessException(id, user.getId());
        }

        // Regra de negócio crítica: não permitir estoque negativo
        if (product.getQuantity() < quantityToRemove) {
            throw new RuntimeException("Estoque insuficiente! Disponível: " + product.getQuantity());
        }
        // Guarda a quantidade anterior antes de atualizar o estoque, para fins de registro da movimentação
        Integer previousQuantity = product.getQuantity();

        // Atualiza a quantidade do produto
        product.setQuantity(product.getQuantity() - quantityToRemove);
        Product updated = productRepository.save(product);

        // Registra a movimentação (EXIT)
        movimentationService.registerMovimentation(
            updated,
            MovimentationType.EXIT,
            previousQuantity,
            updated.getQuantity()
        );

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