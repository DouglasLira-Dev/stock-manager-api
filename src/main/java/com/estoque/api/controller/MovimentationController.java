package com.estoque.api.controller;

import com.estoque.api.dto.MovimentationResponseDTO;
import com.estoque.api.service.MovimentationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimentations")
public class MovimentationController {

    private final MovimentationService movimentationService;

    public MovimentationController(MovimentationService movimentationService) {
        this.movimentationService = movimentationService;
    }

    @GetMapping
    public ResponseEntity<Page<MovimentationResponseDTO>> list(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MovimentationResponseDTO> movimentations = movimentationService.listUserMovimentations(pageable);
        return ResponseEntity.ok(movimentations);
    }
}