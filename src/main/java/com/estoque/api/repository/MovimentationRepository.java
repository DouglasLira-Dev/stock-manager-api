package com.estoque.api.repository;

import com.estoque.api.model.Movimentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentationRepository extends JpaRepository<Movimentation, Long> {
    Page<Movimentation> findByUserId(Long userId, Pageable pageable);
    List<Movimentation> findByProductId(Long productId);
}