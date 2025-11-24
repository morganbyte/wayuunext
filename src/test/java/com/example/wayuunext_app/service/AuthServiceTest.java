package com.example.wayuunext_app.service;

import com.example.wayuunext_app.model.Usuario;
import com.example.wayuunext_app.repository.UserRepository;
import com.example.wayuunext_app.security.JwUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas Funcionales - AuthService")
public class AuthServiceTest {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwUtil jwUtil;

    @InjectMocks
    private AuthService authService;

    private Usuario testUser;
    private String rawPassword = "password123";
    private String encodedPassword = "$2a$10$encodedPasswordHash";
    private String mockToken = "mock.jwt.token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new Usuario();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(encodedPassword);
        testUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Test 1: Login exitoso con credenciales válidas")
    void testLoginSuccess() throws Exception {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwUtil.generateToken(testUser)).thenReturn(mockToken);

        // Act
        String token = authService.login(testUser.getUsername(), rawPassword);

        // Assert
        assertNotNull(token, "El token no debe ser nulo");
        assertEquals(mockToken, token, "El token debe coincidir con el token generado");
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
        verify(jwUtil, times(1)).generateToken(testUser);
        
        log.info("✅ Test 1 EXITOSO: Login con credenciales válidas - Token generado correctamente para usuario '{}'", testUser.getUsername());
    }

    @Test
    @DisplayName("Test 2: Login falla con usuario inexistente")
    void testLoginUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            authService.login("nonexistent", rawPassword);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwUtil, never()).generateToken(any());
        
        log.info("✅ Test 2 EXITOSO: Login rechazado correctamente para usuario inexistente - Mensaje: '{}'", exception.getMessage());
    }

    @Test
    @DisplayName("Test 3: Login falla con contraseña incorrecta")
    void testLoginWithWrongPassword() {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", encodedPassword)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            authService.login(testUser.getUsername(), "wrongpassword");
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
        verify(passwordEncoder, times(1)).matches("wrongpassword", encodedPassword);
        verify(jwUtil, never()).generateToken(any());
        
        log.info("✅ Test 3 EXITOSO: Login rechazado correctamente por contraseña incorrecta para usuario '{}'", testUser.getUsername());
    }

    @Test
    @DisplayName("Test 4: Obtener usuario por username exitosamente")
    void testGetUsuarioByUsernameSuccess() throws Exception {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act
        Usuario resultado = authService.getUsuarioByUsername(testUser.getUsername());

        // Assert
        assertNotNull(resultado, "El usuario no debe ser nulo");
        assertEquals(testUser.getId(), resultado.getId());
        assertEquals(testUser.getUsername(), resultado.getUsername());
        assertEquals(testUser.getEmail(), resultado.getEmail());
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
        
        log.info("✅ Test 4 EXITOSO: Usuario '{}' recuperado correctamente - ID: {}, Email: {}", resultado.getUsername(), resultado.getId(), resultado.getEmail());
    }

    @Test
    @DisplayName("Test 5: Obtener usuario por username falla si no existe")
    void testGetUsuarioByUsernameNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            authService.getUsuarioByUsername("nonexistent");
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
        
        log.info("✅ Test 5 EXITOSO: Búsqueda de usuario inexistente rechazada correctamente");
    }

    @Test
    @DisplayName("Test 6: Login con username null")
    void testLoginWithNullUsername() {
        // Arrange
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            authService.login(null, rawPassword);
        });
        
        log.info("✅ Test 6 EXITOSO: Login con username null rechazado correctamente");
    }

    @Test
    @DisplayName("Test 7: Login con password null")
    void testLoginWithNullPassword() {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(null, encodedPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            authService.login(testUser.getUsername(), null);
        });
        
        log.info("✅ Test 7 EXITOSO: Login con password null rechazado correctamente");
    }

    @Test
    @DisplayName("Test 8: Login con username vacío")
    void testLoginWithEmptyUsername() {
        // Arrange
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            authService.login("", rawPassword);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        
        log.info("✅ Test 8 EXITOSO: Login con username vacío rechazado correctamente");
    }

    @Test
    @DisplayName("Test 9: Login con password vacío")
    void testLoginWithEmptyPassword() {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("", encodedPassword)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            authService.login(testUser.getUsername(), "");
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        
        log.info("✅ Test 9 EXITOSO: Login con password vacío rechazado correctamente");
    }

    @Test
    @DisplayName("Test 10: Verificar que se usa el encoder correcto")
    void testPasswordEncoderIsUsed() throws Exception {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwUtil.generateToken(testUser)).thenReturn(mockToken);

        // Act
        authService.login(testUser.getUsername(), rawPassword);

        // Assert
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
        
        log.info("✅ Test 10 EXITOSO: PasswordEncoder utilizado correctamente para validar credenciales");
    }

    @Test
    @DisplayName("Test 11: Verificar que se genera token con el usuario correcto")
    void testTokenGeneratedWithCorrectUser() throws Exception {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwUtil.generateToken(testUser)).thenReturn(mockToken);

        // Act
        authService.login(testUser.getUsername(), rawPassword);

        // Assert
        verify(jwUtil, times(1)).generateToken(testUser);
        
        log.info("✅ Test 11 EXITOSO: Token JWT generado con el usuario correcto '{}'", testUser.getUsername());
    }

    @Test
    @DisplayName("Test 12: Login con múltiples intentos fallidos")
    void testMultipleFailedLoginAttempts() {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), eq(encodedPassword))).thenReturn(false);

        // Act & Assert - Primer intento
        assertThrows(Exception.class, () -> {
            authService.login(testUser.getUsername(), "wrong1");
        });

        // Act & Assert - Segundo intento
        assertThrows(Exception.class, () -> {
            authService.login(testUser.getUsername(), "wrong2");
        });

        // Act & Assert - Tercer intento
        assertThrows(Exception.class, () -> {
            authService.login(testUser.getUsername(), "wrong3");
        });

        verify(userRepository, times(3)).findByUsername(testUser.getUsername());
        verify(passwordEncoder, times(3)).matches(anyString(), eq(encodedPassword));
        verify(jwUtil, never()).generateToken(any());
        
        log.info("✅ Test 12 EXITOSO: Múltiples intentos de login fallidos (3) rechazados correctamente sin generar token");
    }

    @Test
    @DisplayName("Test 13: Login exitoso después de intentos fallidos")
    void testSuccessfulLoginAfterFailedAttempts() throws Exception {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", encodedPassword)).thenReturn(false);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwUtil.generateToken(testUser)).thenReturn(mockToken);

        // Act & Assert - Intento fallido
        assertThrows(Exception.class, () -> {
            authService.login(testUser.getUsername(), "wrong");
        });

        // Act - Intento exitoso
        String token = authService.login(testUser.getUsername(), rawPassword);

        // Assert
        assertNotNull(token);
        assertEquals(mockToken, token);
        verify(jwUtil, times(1)).generateToken(testUser);
        
        log.info("✅ Test 13 EXITOSO: Login exitoso después de intento fallido - Sistema permite reintentos correctamente");
    }

    @Test
    @DisplayName("Test 14: Obtener usuario retorna objeto completo")
    void testGetUsuarioReturnsCompleteObject() throws Exception {
        // Arrange
        testUser.setProgress(5);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act
        Usuario resultado = authService.getUsuarioByUsername(testUser.getUsername());

        // Assert
        assertNotNull(resultado);
        assertEquals(testUser.getId(), resultado.getId());
        assertEquals(testUser.getUsername(), resultado.getUsername());
        assertEquals(testUser.getEmail(), resultado.getEmail());
        assertEquals(testUser.getProgress(), resultado.getProgress());
        
        log.info("✅ Test 14 EXITOSO: Usuario completo recuperado con todos sus campos - Progress: {}", resultado.getProgress());
    }

    @Test
    @DisplayName("Test 15: Login case-sensitive para username")
    void testLoginCaseSensitiveUsername() {
        // Arrange
        when(userRepository.findByUsername("TESTUSER")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            authService.login("TESTUSER", rawPassword);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("TESTUSER");
        
        log.info("✅ Test 15 EXITOSO: Sistema valida correctamente case-sensitivity en username - 'TESTUSER' vs 'testuser'");
    }
}
