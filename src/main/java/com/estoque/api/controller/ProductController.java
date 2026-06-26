package com.estoque.api.controller;

import com.estoque.api.dto.ProductRequestDTO;
import com.estoque.api.dto.ProductResponseDTO;
import com.estoque.api.dto.ProductStatsDTO;
import com.estoque.api.dto.StockUpdateDTO;
import com.estoque.api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos e controle de estoque")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Criar novo produto", description = "Cadastra um novo produto no estoque do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Lista produtos do usuário com paginação e filtros opcionais por categoria e nome")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<ProductResponseDTO>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductResponseDTO> products = productService.listUserProducts(category, name, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna os detalhes de um produto específico do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto atualizado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.updateProduct(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar produto", description = "Remove um produto do estoque")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produto deletado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock/add")
    @Operation(summary = "Entrada de estoque", description = "Adiciona quantidade ao estoque de um produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estoque atualizado"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ProductResponseDTO> addStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateDTO dto) {
        ProductResponseDTO response = productService.addStock(id, dto.quantity());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/stock/remove")
    @Operation(summary = "Saída de estoque", description = "Remove quantidade do estoque de um produto com validação de saldo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estoque atualizado"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida ou estoque insuficiente"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ProductResponseDTO> removeStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateDTO dto) {
        ProductResponseDTO response = productService.removeStock(id, dto.quantity());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Produtos com estoque baixo", description = "Lista produtos com quantidade menor que 5 unidades")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<ProductResponseDTO>> listLowStock() {
        List<ProductResponseDTO> products = productService.listLowStockProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/below-minimum")
    @Operation(summary = "Produtos abaixo do mínimo", description = "Lista produtos com quantidade abaixo do mínimo definido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<ProductResponseDTO>> listBelowMinimum() {
        List<ProductResponseDTO> products = productService.listProductsBelowMinimum();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/stats")
    @Operation(summary = "Estatísticas do estoque", description = "Retorna métricas do estoque do usuário: total, valor, itens baixos e zerados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas calculadas"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ProductStatsDTO> getStats() {
        ProductStatsDTO stats = productService.getStats();
        return ResponseEntity.ok(stats);
    }
}