package com.estoque.api.controller;

import com.estoque.api.dto.MovimentationResponseDTO;
import com.estoque.api.model.MovimentationType;
import com.estoque.api.service.MovimentationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/movimentations")
@Tag(name = "Movimentações", description = "Endpoints para histórico de movimentações de estoque")
public class MovimentationController {

    private final MovimentationService movimentationService;

    public MovimentationController(MovimentationService movimentationService) {
        this.movimentationService = movimentationService;
    }

    @GetMapping
    @Operation(summary = "Histórico de movimentações", description = "Lista o histórico de entrada/saída de estoque com paginação e filtros opcionais")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<MovimentationResponseDTO>> list(
            @RequestParam(required = false) MovimentationType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime to,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MovimentationResponseDTO> movimentations = movimentationService.listUserMovimentations(type, from, to, pageable);
        return ResponseEntity.ok(movimentations);
    }
}