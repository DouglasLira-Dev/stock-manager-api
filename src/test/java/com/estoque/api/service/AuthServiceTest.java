package com.estoque.api.service;

import com.estoque.api.dto.AuthRequestDTO;
import com.estoque.api.dto.AuthResponseDTO;
import com.estoque.api.dto.RegisterRequestDTO;
import com.estoque.api.model.Role;
import com.estoque.api.model.User;
import com.estoque.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o AuthService.
 * Usa Mockito para simular dependências (UserRepository, PasswordEncoder, etc.)
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private RegisterRequestDTO registerRequest;
    private AuthRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        // Configura dados comuns para os testes
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("João Silva");
        mockUser.setEmail("joao@email.com");
        mockUser.setPassword("senhaCriptografada");
        mockUser.setRole(Role.ROLE_USER);

        registerRequest = new RegisterRequestDTO(
                "João Silva",
                "joao@email.com",
                "123456"
        );

        loginRequest = new AuthRequestDTO(
                "joao@email.com",
                "123456"
        );
    }

    // ======== TESTES DE REGISTRO ========

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void shouldRegisterUserSuccessfully() {
        // Arrange: configura o comportamento dos mocks
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("senhaCriptografada");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("tokenJWT");

        // Act: executa o registro
        AuthResponseDTO response = authService.register(registerRequest);

        // Assert: verifica os resultados
        assertNotNull(response);
        assertEquals("joao@email.com", response.email());
        assertEquals("ROLE_USER", response.role());
        assertNotNull(response.token());

        // Verifica se os métodos foram chamados corretamente
        verify(userRepository).existsByEmail(registerRequest.email());
        verify(passwordEncoder).encode(registerRequest.password());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar com email já existente")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange: configura o mock para retornar true (email já existe)
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        // Act & Assert: verifica se a exceção é lançada
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals("Email já está em uso!", exception.getMessage());

        // Verifica que o save NUNCA foi chamado
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    // ======== TESTES DE LOGIN ========

    @Test
    @DisplayName("Deve fazer login com sucesso e retornar token JWT")
    void shouldLoginSuccessfully() {
        // Arrange: configura os mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // Autenticação bem-sucedida
        when(userRepository.findByEmail(loginRequest.email()))
                .thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser)).thenReturn("tokenJWT");

        // Act: executa o login
        AuthResponseDTO response = authService.login(loginRequest);

        // Assert: verifica os resultados
        assertNotNull(response);
        assertEquals("joao@email.com", response.email());
        assertEquals("ROLE_USER", response.role());
        assertNotNull(response.token());

        // Verifica as chamadas
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(loginRequest.email());
        verify(jwtService).generateToken(mockUser);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não existe no login")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange: configura o mock para lançar exceção na autenticação
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Credenciais inválidas"));

        // Act & Assert: verifica a exceção
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest)
        );

        // Verifica que o findByEmail NUNCA foi chamado
        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha está incorreta")
    void shouldThrowExceptionWhenPasswordIsWrong() {
        // Arrange: autenticação falha
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Credenciais inválidas"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Credenciais inválidas", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }
}