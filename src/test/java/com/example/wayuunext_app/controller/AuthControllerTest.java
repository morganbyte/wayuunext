package com.example.wayuunext_app.controller;

import com.example.wayuunext_app.model.AuthRequest;
import com.example.wayuunext_app.model.Usuario;
import com.example.wayuunext_app.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Pruebas Funcionales - Autenticación JWT")
public class AuthControllerTest {

    private static final Logger log = LoggerFactory.getLogger(AuthControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthService authService;

    private AuthRequest validAuthRequest;
    private Usuario mockUsuario;
    private String mockToken;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        validAuthRequest = new AuthRequest();
        validAuthRequest.setUsername("testuser");
        validAuthRequest.setPassword("password123");

        mockUsuario = new Usuario();
        mockUsuario.setId(1L);
        mockUsuario.setUsername("testuser");
        mockUsuario.setEmail("test@example.com");

        mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
    }

    @Test
    @DisplayName("Test 1: Login exitoso con credenciales válidas")
    void testLoginSuccess() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(authService.login(validAuthRequest.getUsername(), validAuthRequest.getPassword()))
                .thenReturn(mockToken);
        when(authService.getUsuarioByUsername(validAuthRequest.getUsername()))
                .thenReturn(mockUsuario);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authToken").value(mockToken))
                .andExpect(jsonPath("$.userId").value(1));

        // Verificar que se llamaron los métodos correctos
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authService, times(1)).login(validAuthRequest.getUsername(), validAuthRequest.getPassword());
        verify(authService, times(1)).getUsuarioByUsername(validAuthRequest.getUsername());
        
        log.info("✅ Test 1 EXITOSO: Login HTTP exitoso - Usuario '{}' autenticado, token y userId retornados en JSON", validAuthRequest.getUsername());
    }

    @Test
    @DisplayName("Test 2: Login fallido con credenciales inválidas")
    void testLoginFailureInvalidCredentials() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication failed"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authService, never()).login(anyString(), anyString());
        
        log.info("✅ Test 2 EXITOSO: Credenciales inválidas rechazadas - HTTP 401 con mensaje de error");
    }

    @Test
    @DisplayName("Test 3: Login con usuario inexistente")
    void testLoginUserNotFound() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(authService.login(validAuthRequest.getUsername(), validAuthRequest.getPassword()))
                .thenThrow(new Exception("Usuario no encontrado"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication failed"));
        
        log.info("✅ Test 3 EXITOSO: Usuario inexistente manejado - HTTP 401 con error de autenticación");
    }

    @Test
    @DisplayName("Test 4: Login sin username")
    void testLoginWithoutUsername() throws Exception {
        // Arrange
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized());
        
        log.info("✅ Test 4 EXITOSO: Request sin username rechazado - HTTP 401");
    }

    @Test
    @DisplayName("Test 5: Login sin password")
    void testLoginWithoutPassword() throws Exception {
        // Arrange
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername("testuser");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized());
        
        log.info("✅ Test 5 EXITOSO: Request sin password rechazado - HTTP 401");
    }

    @Test
    @DisplayName("Test 6: Login con body vacío")
    void testLoginWithEmptyBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
        
        log.info("✅ Test 6 EXITOSO: Body vacío rechazado - HTTP 401");
    }

    @Test
    @DisplayName("Test 7: Login con formato JSON inválido")
    void testLoginWithInvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
        
        log.info("✅ Test 7 EXITOSO: JSON malformado rechazado - HTTP 400");
    }

    @Test
    @DisplayName("Test 8: Verificar que el token es generado correctamente")
    void testTokenGeneration() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(authService.login(validAuthRequest.getUsername(), validAuthRequest.getPassword()))
                .thenReturn(mockToken);
        when(authService.getUsuarioByUsername(validAuthRequest.getUsername()))
                .thenReturn(mockUsuario);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authToken").isNotEmpty())
                .andExpect(jsonPath("$.authToken").isString());
        
        log.info("✅ Test 8 EXITOSO: Token generado correctamente - Formato string no vacío en respuesta JSON");
    }

    @Test
    @DisplayName("Test 9: Verificar que se retorna el userId correcto")
    void testUserIdInResponse() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(authService.login(validAuthRequest.getUsername(), validAuthRequest.getPassword()))
                .thenReturn(mockToken);
        when(authService.getUsuarioByUsername(validAuthRequest.getUsername()))
                .thenReturn(mockUsuario);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.userId").value(mockUsuario.getId()));
        
        log.info("✅ Test 9 EXITOSO: UserId retornado correctamente en respuesta - ID: {}", mockUsuario.getId());
    }

    @Test
    @DisplayName("Test 10: Login con contraseña incorrecta")
    void testLoginWithWrongPassword() throws Exception {
        // Arrange
        AuthRequest wrongPasswordRequest = new AuthRequest();
        wrongPasswordRequest.setUsername("testuser");
        wrongPasswordRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
        
        log.info("✅ Test 10 EXITOSO: Contraseña incorrecta rechazada - HTTP 401 con campo de error");
    }
}
