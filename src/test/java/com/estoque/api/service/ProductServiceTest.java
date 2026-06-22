package com.estoque.api.service;

import com.estoque.api.dto.ProductRequestDTO;
import com.estoque.api.dto.ProductResponseDTO;
import com.estoque.api.dto.ProductStatsDTO;
import com.estoque.api.model.Product;
import com.estoque.api.model.Role;
import com.estoque.api.model.User;
import com.estoque.api.repository.ProductRepository;
import com.estoque.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Permite stubbings não utilizados
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private MovimentationService movimentationService;

    @InjectMocks
    private ProductService productService;

    private User mockUser;
    private Product mockProduct;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setUp() {
        // Configura o usuário mock
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("João Silva");
        mockUser.setEmail("joao@email.com");
        mockUser.setRole(Role.ROLE_USER);

        // Configura o produto mock
        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Notebook Dell");
        mockProduct.setDescription("I7 16GB RAM");
        mockProduct.setQuantity(10);
        mockProduct.setPrice(new BigDecimal("4500.00"));
        mockProduct.setCategory("Eletrônicos");
        mockProduct.setUser(mockUser);

        // Configura o DTO de requisição
        productRequestDTO = new ProductRequestDTO(
            "Notebook Dell",
            "I7 16GB RAM",
            10,
            new BigDecimal("4500.00"),
            "Eletrônicos",
            3  // minimumQuantity
        );

        // Configura o SecurityContext para retornar o usuário autenticado
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("joao@email.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(mockUser));
    }

    // ======== TESTES DE CRIAÇÃO ========

    @Test
    @DisplayName("Deve criar um produto com sucesso")
    void shouldCreateProductSuccessfully() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // Act
        ProductResponseDTO response = productService.createProduct(productRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Notebook Dell", response.name());
        assertEquals(10, response.quantity());
        assertEquals(new BigDecimal("4500.00"), response.price());

        verify(productRepository).save(any(Product.class));
    }

    // ======== TESTES DE LISTAGEM ========

    @Test
    @DisplayName("Deve listar produtos do usuário com paginação")
    void shouldListUserProducts() {
        // Arrange
        Page<Product> productPage = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(productPage);

        // Act
        Page<ProductResponseDTO> response = productService.listUserProducts(null, null, Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Notebook Dell", response.getContent().get(0).name());

        verify(productRepository).findByUserId(eq(1L), any(Pageable.class));
    }

    // ======== TESTES DE BUSCA POR ID ========

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void shouldGetProductByIdSuccessfully() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        // Act
        ProductResponseDTO response = productService.getProductById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Notebook Dell", response.name());

        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar produto de outro usuário")
    void shouldThrowExceptionWhenProductBelongsToAnotherUser() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        mockProduct.setUser(otherUser);

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.getProductById(1L)
        );

        assertTrue(exception.getMessage().contains("não tem permissão"));
    }

    // ======== TESTES DE ATUALIZAÇÃO ========

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void shouldUpdateProductSuccessfully() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // Act
        ProductResponseDTO response = productService.updateProduct(1L, productRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Notebook Dell", response.name());

        verify(productRepository).save(any(Product.class));
    }

    // ======== TESTES DE DELEÇÃO ========

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void shouldDeleteProductSuccessfully() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        doNothing().when(productRepository).delete(mockProduct);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).delete(mockProduct);
    }

    // ======== TESTES DE ENTRADA DE ESTOQUE ========

    @Test
    @DisplayName("Deve adicionar quantidade ao estoque com sucesso")
    void shouldAddStockSuccessfully() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // Act
        ProductResponseDTO response = productService.addStock(1L, 5);

        // Assert
        assertNotNull(response);
        assertEquals(15, response.quantity()); // 10 + 5

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar quantidade negativa")
    void shouldThrowExceptionWhenAddingNegativeQuantity() {
        // Act & Assert - NÃO precisa de stubbings aqui!
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.addStock(1L, -5)
        );

        assertEquals("Quantidade a adicionar deve ser positiva", exception.getMessage());
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    // ======== TESTES DE SAÍDA DE ESTOQUE ========

    @Test
    @DisplayName("Deve remover quantidade do estoque com sucesso")
    void shouldRemoveStockSuccessfully() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // Act
        ProductResponseDTO response = productService.removeStock(1L, 3);

        // Assert
        assertNotNull(response);
        assertEquals(7, response.quantity()); // 10 - 3

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover mais do que tem em estoque")
    void shouldThrowExceptionWhenRemovingMoreThanAvailable() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.removeStock(1L, 15)
        );

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover quantidade negativa")
    void shouldThrowExceptionWhenRemovingNegativeQuantity() {
        // Act & Assert - NÃO precisa de stubbings aqui!
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.removeStock(1L, -5)
        );

        assertEquals("Quantidade a remover deve ser positiva", exception.getMessage());
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    // ======== TESTES DE ESTOQUE BAIXO ========

    @Test
    @DisplayName("Deve listar produtos com estoque baixo (< 5 unidades)")
    void shouldListLowStockProducts() {
        // Arrange
        List<Product> lowStockProducts = List.of(mockProduct);
        when(productRepository.findByUserIdAndQuantityLessThan(eq(1L), eq(5)))
                .thenReturn(lowStockProducts);

        // Act
        List<ProductResponseDTO> response = productService.listLowStockProducts();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Notebook Dell", response.get(0).name());

        verify(productRepository).findByUserIdAndQuantityLessThan(eq(1L), eq(5));
    }
    // ======== TESTES DE ESTATÍSTICAS ========
    @Test
    @DisplayName("Deve calcular estatísticas corretamente com produtos no estoque")
    void shouldCalculateStatsWithProductsInStock() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Notebook");
        product1.setQuantity(10);
        product1.setPrice(new BigDecimal("4500.00"));
        product1.setUser(mockUser);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setQuantity(3); // Estoque baixo (< 5)
        product2.setPrice(new BigDecimal("150.00"));
        product2.setUser(mockUser);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("Teclado");
        product3.setQuantity(0); // Estoque zerado
        product3.setPrice(new BigDecimal("200.00"));
        product3.setUser(mockUser);

        Product product4 = new Product();
        product4.setId(4L);
        product4.setName("Monitor");
        product4.setQuantity(8);
        product4.setPrice(new BigDecimal("1200.00"));
        product4.setUser(mockUser);

        List<Product> products = List.of(product1, product2, product3, product4);
        when(productRepository.findByUserId(1L)).thenReturn(products);

        // Act
        ProductStatsDTO stats = productService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(4L, stats.totalProducts());
        assertEquals(21L, stats.totalItemsInStock()); // 10 + 3 + 0 + 8 = 21
        assertEquals(new BigDecimal("55050.00"), stats.totalStockValue()); // 45000 + 450 + 0 + 9600 = 55050
        assertEquals(1L, stats.lowStockItems()); // Mouse (3 unidades)
        assertEquals(1L, stats.outOfStockItems()); // Teclado (0 unidades)
    }

    @Test
    @DisplayName("Deve retornar estatísticas zeradas quando não há produtos")
    void shouldReturnZeroStatsWhenNoProducts() {
        // Arrange
        when(productRepository.findByUserId(1L)).thenReturn(List.of());

        // Act
        ProductStatsDTO stats = productService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(0L, stats.totalProducts());
        assertEquals(0L, stats.totalItemsInStock());
        assertEquals(BigDecimal.ZERO, stats.totalStockValue());
        assertEquals(0L, stats.lowStockItems());
        assertEquals(0L, stats.outOfStockItems());
    }

    @Test
    @DisplayName("Deve calcular corretamente produtos com estoque baixo e zerado")
    void shouldCorrectlyIdentifyLowStockAndOutOfStock() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Produto com 4 unidades");
        product1.setQuantity(4); // Estoque baixo
        product1.setPrice(new BigDecimal("10.00"));
        product1.setUser(mockUser);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Produto com 0 unidades");
        product2.setQuantity(0); // Estoque zerado
        product2.setPrice(new BigDecimal("20.00"));
        product2.setUser(mockUser);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("Produto com 10 unidades");
        product3.setQuantity(10); // Estoque normal
        product3.setPrice(new BigDecimal("30.00"));
        product3.setUser(mockUser);

        List<Product> products = List.of(product1, product2, product3);
        when(productRepository.findByUserId(1L)).thenReturn(products);

        // Act
        ProductStatsDTO stats = productService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(3L, stats.totalProducts());
        assertEquals(14L, stats.totalItemsInStock()); // 4 + 0 + 10 = 14
        assertEquals(new BigDecimal("340.00"), stats.totalStockValue()); // 40 + 0 + 300 = 340
        assertEquals(1L, stats.lowStockItems()); // Apenas o produto com 4 unidades
        assertEquals(1L, stats.outOfStockItems()); // Apenas o produto com 0 unidades
    }   
}