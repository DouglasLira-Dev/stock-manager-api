package com.estoque.api.repository;

import com.estoque.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para a entidade User.
 * Fornece operações CRUD e consultas personalizadas.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo email.
     * Usado no login e na geração do token.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se já existe um usuário com o email informado.
     * Usado no cadastro para evitar duplicidade.
     */
    boolean existsByEmail(String email);
}