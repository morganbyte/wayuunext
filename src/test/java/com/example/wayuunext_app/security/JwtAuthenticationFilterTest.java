package com.example.wayuunext_app.security;

import com.example.wayuunext_app.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas Funcionales - JWT Authentication Filter")
public class JwtAuthenticationFilterTest {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilterTest.class);

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwUtil jwUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private String validToken;
    private String invalidToken;
    private Usuario testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwUtil);
        
        // Limpiar el contexto de seguridad antes de cada prueba
        SecurityContextHolder.clearContext();

        testUser = new Usuario();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        validToken = "valid.jwt.token";
        invalidToken = "invalid.jwt.token";
    }

    @Test
    @DisplayName("Test 1: Filtro procesa correctamente un token válido")
    void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwUtil.getUsernameFromToken(validToken)).thenReturn(testUser.getUsername());
        when(jwUtil.validateToken(validToken)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), 
                "La autenticación debe estar establecida en el contexto de seguridad");
        assertEquals(testUser.getUsername(), 
                SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                "El username en el contexto debe coincidir");
        
        log.info("✅ Test 1 EXITOSO: Token válido procesado - Usuario '{}' autenticado en contexto de seguridad", testUser.getUsername());
    }

    @Test
    @DisplayName("Test 2: Filtro rechaza token inválido")
    void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwUtil.getUsernameFromToken(invalidToken)).thenReturn(testUser.getUsername());
        when(jwUtil.validateToken(invalidToken)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "La autenticación no debe estar establecida para un token inválido");
        
        log.info("✅ Test 2 EXITOSO: Token inválido rechazado correctamente - No se estableció autenticación");
    }

    @Test
    @DisplayName("Test 3: Filtro maneja petición sin header de autorización")
    void testDoFilterInternalWithoutAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "La autenticación no debe estar establecida sin header de autorización");
        verify(jwUtil, never()).getUsernameFromToken(anyString());
        verify(jwUtil, never()).validateToken(anyString());
        
        log.info("✅ Test 3 EXITOSO: Petición sin header Authorization manejada correctamente - Filtro omite procesamiento JWT");
    }

    @Test
    @DisplayName("Test 4: Filtro maneja header de autorización sin prefijo Bearer")
    void testDoFilterInternalWithoutBearerPrefix() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(validToken);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "La autenticación no debe estar establecida sin prefijo Bearer");
        verify(jwUtil, never()).getUsernameFromToken(anyString());
        
        log.info("✅ Test 4 EXITOSO: Header sin prefijo 'Bearer' rechazado correctamente");
    }

    @Test
    @DisplayName("Test 5: Filtro extrae correctamente el token del header")
    void testTokenExtractionFromHeader() throws ServletException, IOException {
        // Arrange
        String bearerToken = "Bearer " + validToken;
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwUtil.getUsernameFromToken(validToken)).thenReturn(testUser.getUsername());
        when(jwUtil.validateToken(validToken)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwUtil, times(1)).getUsernameFromToken(validToken);
        verify(jwUtil, times(1)).validateToken(validToken);
        
        log.info("✅ Test 5 EXITOSO: Token extraído correctamente del header 'Bearer' - Token validado y username obtenido");
    }

    @Test
    @DisplayName("Test 6: Filtro no reemplaza autenticación existente")
    void testDoNotReplaceExistingAuthentication() throws ServletException, IOException {
        // Arrange
        // Simular que ya hay una autenticación en el contexto
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken existingAuth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "existinguser", null, new java.util.ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwUtil.getUsernameFromToken(validToken)).thenReturn(testUser.getUsername());
        when(jwUtil.validateToken(validToken)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertEquals("existinguser", 
                SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                "La autenticación existente no debe ser reemplazada");
        
        log.info("✅ Test 6 EXITOSO: Autenticación existente preservada correctamente - Usuario 'existinguser' mantenido");
    }

    @Test
    @DisplayName("Test 7: Filtro propaga excepción al extraer username")
    void testHandleExceptionWhenExtractingUsername() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwUtil.getUsernameFromToken(validToken)).thenThrow(new RuntimeException("Token parsing error"));

        // Act & Assert - El filtro debe propagar la excepción
        assertThrows(RuntimeException.class, () -> {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        });

        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "La autenticación no debe estar establecida cuando hay un error");
        
        log.info("✅ Test 7 EXITOSO: Excepción en parsing de token propagada correctamente - Seguridad mantenida");
    }

    @Test
    @DisplayName("Test 8: Filtro maneja header vacío")
    void testDoFilterInternalWithEmptyHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        log.info("✅ Test 8 EXITOSO: Header vacío manejado correctamente - Petición procesada sin autenticación");
    }

    @Test
    @DisplayName("Test 9: Filtro maneja Bearer sin token")
    void testDoFilterInternalWithBearerButNoToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        // El filtro intentará procesar un token vacío
        verify(jwUtil, times(1)).getUsernameFromToken("");
        
        log.info("✅ Test 9 EXITOSO: 'Bearer' sin token manejado - Filtro intenta extraer token vacío");
    }

    @Test
    @DisplayName("Test 10: Filtro siempre continúa la cadena de filtros")
    void testFilterChainAlwaysContinues() throws ServletException, IOException {
        // Arrange - Diferentes escenarios
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwUtil.getUsernameFromToken(validToken)).thenReturn(testUser.getUsername());
        when(jwUtil.validateToken(validToken)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        
        log.info("✅ Test 10 EXITOSO: Cadena de filtros continúa correctamente - FilterChain.doFilter() invocado");
    }

    @Test
    @DisplayName("Test 11: Verificar que la autenticación incluye detalles del request")
    void testAuthenticationIncludesRequestDetails() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwUtil.getUsernameFromToken(validToken)).thenReturn(testUser.getUsername());
        when(jwUtil.validateToken(validToken)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication().getDetails(),
                "Los detalles de autenticación deben estar presentes");
        
        log.info("✅ Test 11 EXITOSO: Autenticación incluye detalles del request - Metadatos de seguridad completos");
    }

    @Test
    @DisplayName("Test 12: Filtro maneja token con espacios extra")
    void testDoFilterInternalWithExtraSpaces() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer  " + validToken); // Doble espacio

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        // El filtro extraerá " " + validToken (con espacio inicial)
        
        log.info("✅ Test 12 EXITOSO: Token con espacios extra procesado - Filtro maneja formatos no estándar");
    }
}
