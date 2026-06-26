package com.estoque.api.controller;

import com.estoque.api.dto.MovimentationResponseDTO;
import com.estoque.api.model.MovimentationType;
import com.estoque.api.service.MovimentationService;

import java.time.LocalDateTime;

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

@RestController
@RequestMapping("/api/movimentations")
public class MovimentationController {

    private final MovimentationService movimentationService;

    public MovimentationController(MovimentationService movimentationService) {
        this.movimentationService = movimentationService;
    }

    @GetMapping
    public ResponseEntity<Page<MovimentationResponseDTO>> list(
            @RequestParam(required = false) MovimentationType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime to,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MovimentationResponseDTO> movimentations = movimentationService.listUserMovimentations(type, from, to, pageable);
        return ResponseEntity.ok(movimentations);
    }
}