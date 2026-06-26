package com.estoque.api.repository;

import com.estoque.api.model.Movimentation;
import com.estoque.api.model.MovimentationType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentationRepository extends JpaRepository<Movimentation, Long> {
    Page<Movimentation> findByUserId(Long userId, Pageable pageable);

    Page<Movimentation> findByUserIdAndType(Long userId, MovimentationType type, Pageable pageable);

    Page<Movimentation> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Movimentation> findByUserIdAndTypeAndCreatedAtBetween(Long userId, MovimentationType type, LocalDateTime from, LocalDateTime to, Pageable pageable);
    
    List<Movimentation> findByProductId(Long productId);
}