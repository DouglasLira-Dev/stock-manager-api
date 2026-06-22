package com.estoque.api.service;

import com.estoque.api.dto.AuthRequestDTO;
import com.estoque.api.dto.AuthResponseDTO;
import com.estoque.api.dto.RegisterRequestDTO;
import com.estoque.api.exception.DuplicateEmailException;
import com.estoque.api.exception.InvalidCredentialsException;
import com.estoque.api.exception.UserNotFoundException;
import com.estoque.api.model.Role;
import com.estoque.api.model.User;
import com.estoque.api.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        logger.info("Tentativa de registro de usuário com email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            logger.warn("Tentativa de registro falhou: email {} já está em uso.", request.email());
            throw new DuplicateEmailException(request.email());
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
        logger.info("Usuário registrado com sucesso: {} (ID: {})", user.getEmail(), user.getId());

        String token = jwtService.generateToken(user);
        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name());
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        logger.info("Tentativa de login para email: {}", request.email());
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (Exception e) {
            logger.warn("Tentativa de login falhou para email: {} - {}", request.email(), e.getMessage());
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado para login: {}", request.email());

                    return new InvalidCredentialsException();
                });

        String token = jwtService.generateToken(user);
        logger.info("Login bem-sucedido para email: {} (ID: {})", user.getEmail(), user.getId());
        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name());
    }
}