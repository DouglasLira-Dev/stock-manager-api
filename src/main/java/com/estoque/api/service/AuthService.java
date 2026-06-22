package com.estoque.api.service;

import com.estoque.api.dto.AuthRequestDTO;
import com.estoque.api.dto.AuthResponseDTO;
import com.estoque.api.dto.RegisterRequestDTO;
import com.estoque.api.model.Role;
import com.estoque.api.model.User;
import com.estoque.api.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela lógica de autenticação:
 * - Registro de novos usuários
 * - Login e geração de token JWT
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Cadastra um novo usuário e retorna um token JWT.
     */
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Verifica se o email já está em uso
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email já está em uso!");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password())); // Criptografa a senha
        user.setRole(Role.ROLE_USER); // Por padrão, usuário comum

        userRepository.save(user);

        // Gera token JWT para o novo usuário (já autenticado)
        String token = jwtService.generateToken(user);
        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name());
    }

    /**
     * Autentica um usuário e retorna um token JWT.
     */
    public AuthResponseDTO login(AuthRequestDTO request) {
        // Delega a autenticação ao AuthenticationManager do Spring
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // Se chegou aqui, o usuário foi autenticado com sucesso
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtService.generateToken(user);
        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name());
    }
}