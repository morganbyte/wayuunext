package com.example.wayuunext_app.security;

import com.example.wayuunext_app.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Pruebas Funcionales - JWT Utility")
public class JwUtilTest {

    private static final Logger log = LoggerFactory.getLogger(JwUtilTest.class);

    @Autowired
    private JwUtil jwUtil;

    private Usuario testUser;

    @BeforeEach
    void setUp() {
        testUser = new Usuario();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Test 1: Generar token JWT válido")
    void testGenerateToken() {
        // Act
        String token = jwUtil.generateToken(testUser);

        // Assert
        assertNotNull(token, "El token no debe ser nulo");
        assertFalse(token.isEmpty(), "El token no debe estar vacío");
        assertTrue(token.split("\\.").length == 3, "El token debe tener 3 partes separadas por puntos");
        
        log.info("✅ Test 1 EXITOSO: Token JWT generado correctamente - 3 partes (header.payload.signature)");
    }

    @Test
    @DisplayName("Test 2: Validar token válido")
    void testValidateValidToken() {
        // Arrange
        String token = jwUtil.generateToken(testUser);

        // Act
        boolean isValid = jwUtil.validateToken(token);

        // Assert
        assertTrue(isValid, "El token generado debe ser válido");
        
        log.info("✅ Test 2 EXITOSO: Token válido validado correctamente - isValid=true");
    }

    @Test
    @DisplayName("Test 3: Rechazar token inválido")
    void testValidateInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwUtil.validateToken(invalidToken);

        // Assert
        assertFalse(isValid, "Un token inválido debe ser rechazado");
        
        log.info("✅ Test 3 EXITOSO: Token inválido rechazado correctamente - isValid=false");
    }

    @Test
    @DisplayName("Test 4: Extraer username del token")
    void testGetUsernameFromToken() {
        // Arrange
        String token = jwUtil.generateToken(testUser);

        // Act
        String username = jwUtil.getUsernameFromToken(token);

        // Assert
        assertNotNull(username, "El username no debe ser nulo");
        assertEquals(testUser.getUsername(), username, "El username extraído debe coincidir con el usuario original");
        
        log.info("✅ Test 4 EXITOSO: Username extraído correctamente del token - Usuario: '{}'", username);
    }

    @Test
    @DisplayName("Test 5: Generar tokens diferentes para diferentes usuarios")
    void testGenerateDifferentTokensForDifferentUsers() {
        // Arrange
        Usuario user2 = new Usuario();
        user2.setId(2L);
        user2.setUsername("testuser2");
        user2.setEmail("test2@example.com");

        // Act
        String token1 = jwUtil.generateToken(testUser);
        String token2 = jwUtil.generateToken(user2);

        // Assert
        assertNotEquals(token1, token2, "Tokens de diferentes usuarios deben ser diferentes");
        
        log.info("✅ Test 5 EXITOSO: Tokens diferentes generados para usuarios distintos - '{}' vs '{}'", testUser.getUsername(), user2.getUsername());
    }

    @Test
    @DisplayName("Test 6: Rechazar token vacío")
    void testValidateEmptyToken() {
        // Arrange
        String emptyToken = "";

        // Act
        boolean isValid = jwUtil.validateToken(emptyToken);

        // Assert
        assertFalse(isValid, "Un token vacío debe ser rechazado");
        
        log.info("✅ Test 6 EXITOSO: Token vacío rechazado correctamente");
    }

    @Test
    @DisplayName("Test 7: Rechazar token nulo")
    void testValidateNullToken() {
        // Act
        boolean isValid = jwUtil.validateToken(null);

        // Assert
        assertFalse(isValid, "Un token nulo debe ser rechazado");
        
        log.info("✅ Test 7 EXITOSO: Token nulo rechazado correctamente");
    }

    @Test
    @DisplayName("Test 8: Token contiene la información correcta")
    void testTokenContainsCorrectInformation() {
        // Arrange & Act
        String token = jwUtil.generateToken(testUser);
        String extractedUsername = jwUtil.getUsernameFromToken(token);

        // Assert
        assertEquals(testUser.getUsername(), extractedUsername, "El token debe contener el username correcto");
        
        log.info("✅ Test 8 EXITOSO: Token contiene información correcta - Username: '{}'", extractedUsername);
    }

    @Test
    @DisplayName("Test 9: Generar múltiples tokens para el mismo usuario")
    void testGenerateMultipleTokensForSameUser() throws InterruptedException {
        // Act - Generar tokens con una pausa entre ellos
        String token1 = jwUtil.generateToken(testUser);
        Thread.sleep(1000); // Pausa de 1 segundo para asegurar diferentes timestamps
        String token2 = jwUtil.generateToken(testUser);

        // Assert
        assertNotEquals(token1, token2, "Tokens generados en diferentes momentos deben ser diferentes");
        
        // Pero ambos deben ser válidos
        assertTrue(jwUtil.validateToken(token1), "Token 1 debe ser válido");
        assertTrue(jwUtil.validateToken(token2), "Token 2 debe ser válido");
        
        log.info("✅ Test 9 EXITOSO: Múltiples tokens generados para mismo usuario - Ambos válidos con diferentes timestamps");
    }

    @Test
    @DisplayName("Test 10: Token malformado debe ser rechazado")
    void testValidateMalformedToken() {
        // Arrange
        String malformedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.malformed";

        // Act
        boolean isValid = jwUtil.validateToken(malformedToken);

        // Assert
        assertFalse(isValid, "Un token malformado debe ser rechazado");
        
        log.info("✅ Test 10 EXITOSO: Token malformado rechazado correctamente");
    }

    @Test
    @DisplayName("Test 11: Verificar que el token no es nulo después de generarse")
    void testGeneratedTokenIsNotNull() {
        // Act
        String token = jwUtil.generateToken(testUser);

        // Assert
        assertNotNull(token, "El token generado nunca debe ser nulo");
        
        log.info("✅ Test 11 EXITOSO: Token generado no es nulo - Garantía de generación correcta");
    }

    @Test
    @DisplayName("Test 12: Extraer username de token inválido debe lanzar excepción")
    void testGetUsernameFromInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.string";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwUtil.getUsernameFromToken(invalidToken);
        }, "Extraer username de token inválido debe lanzar excepción");
        
        log.info("✅ Test 12 EXITOSO: Excepción lanzada al extraer username de token inválido - Manejo de errores correcto");
    }
}
