package com.estoque.api.controller;

import com.estoque.api.dto.ProductRequestDTO;
import com.estoque.api.dto.ProductResponseDTO;
import com.estoque.api.dto.ProductStatsDTO;
import com.estoque.api.dto.StockUpdateDTO;
import com.estoque.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 1. Criar produto
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Listar produtos (com paginação, filtro por categoria e nome)
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductResponseDTO> products = productService.listUserProducts(category, name, pageable);
        return ResponseEntity.ok(products);
    }

    // 3. Buscar produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    // 4. Atualizar produto
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.updateProduct(id, dto);
        return ResponseEntity.ok(response);
    }

    // 5. Deletar produto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // 6. ENTRADA DE ESTOQUE
    @PatchMapping("/{id}/stock/add")
    public ResponseEntity<ProductResponseDTO> addStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateDTO dto) {
        ProductResponseDTO response = productService.addStock(id, dto.quantity());
        return ResponseEntity.ok(response);
    }

    // 7. SAÍDA DE ESTOQUE
    @PatchMapping("/{id}/stock/remove")
    public ResponseEntity<ProductResponseDTO> removeStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateDTO dto) {
        ProductResponseDTO response = productService.removeStock(id, dto.quantity());
        return ResponseEntity.ok(response);
    }

    // 8. Listar produtos com estoque baixo (quantidade < 5)
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDTO>> listLowStock() {
        List<ProductResponseDTO> products = productService.listLowStockProducts();
        return ResponseEntity.ok(products);
    }
    // 9. Estatísticas do estoque
    @GetMapping("/stats")
    public ResponseEntity<ProductStatsDTO> getStats() {
        ProductStatsDTO stats = productService.getStats();
        return ResponseEntity.ok(stats);
    }
    // 10. Listar produtos com estoque zerado
    @GetMapping("/below-minimum")
    public ResponseEntity<List<ProductResponseDTO>> listProductsBelowMinimum() {
        List<ProductResponseDTO> products = productService.listProductsBelowMinimum();
        return ResponseEntity.ok(products);
    }
}