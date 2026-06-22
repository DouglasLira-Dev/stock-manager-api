package com.estoque.api.repository;

import com.estoque.api.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Listar produtos de um usuário (paginação)
    Page<Product> findByUserId(Long userId, Pageable pageable);

    // Filtrar por categoria
    Page<Product> findByUserIdAndCategoryContainingIgnoreCase(Long userId, String category, Pageable pageable);

    // Filtrar por nome
    Page<Product> findByUserIdAndNameContainingIgnoreCase(Long userId, String name, Pageable pageable);

    // Filtrar por categoria e nome
    Page<Product> findByUserIdAndCategoryContainingIgnoreCaseAndNameContainingIgnoreCase(
            Long userId, String category, String name, Pageable pageable);

    // Produtos com estoque baixo (quantidade < limite)
    List<Product> findByUserIdAndQuantityLessThan(Long userId, Integer limit);
    List<Product> findByUserId(Long userId);
}