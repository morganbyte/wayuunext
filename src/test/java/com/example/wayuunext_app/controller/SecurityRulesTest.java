package com.example.wayuunext_app.controller;

import com.example.wayuunext_app.model.Opcion;
import com.example.wayuunext_app.model.Pregunta;
import com.example.wayuunext_app.model.Test;
import com.example.wayuunext_app.model.Usuario;
import com.example.wayuunext_app.repository.OpcionRepository;
import com.example.wayuunext_app.repository.PreguntaRepository;
import com.example.wayuunext_app.repository.ResultadoUsuarioRepository;
import com.example.wayuunext_app.repository.TestRepository;
import com.example.wayuunext_app.repository.UserRepository;
import com.example.wayuunext_app.security.JwUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Pruebas de Seguridad - Reglas de acceso")
public class SecurityRulesTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwUtil jwUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private PreguntaRepository preguntaRepository;
    @Autowired
    private OpcionRepository opcionRepository;
    @Autowired
    private ResultadoUsuarioRepository resultadoUsuarioRepository;

    private Long testId;
    private Long preguntaId;

    @BeforeEach
    void setUp() {
        resultadoUsuarioRepository.deleteAll();
        opcionRepository.deleteAll();
        preguntaRepository.deleteAll();
        testRepository.deleteAll();
        userRepository.deleteAll();

        Test test = testRepository.save(new Test("Saludos", "Saludos wayuu", "Saludos", "1"));
        Pregunta pregunta = new Pregunta("¿Cómo se dice hola?", 1);
        pregunta.setTest(test);
        pregunta = preguntaRepository.save(pregunta);
        Opcion opcion = new Opcion("Jamaya", 1);
        opcion.setPregunta(pregunta);
        opcionRepository.save(opcion);

        testId = test.getId();
        preguntaId = pregunta.getId();
    }

    private String tokenPara(String username, String role) {
        Usuario usuario = new Usuario(username, "x", username + "@test.com");
        usuario.setRole(role);
        return jwUtil.generateToken(userRepository.save(usuario));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Las preguntas no revelan la respuesta correcta")
    void preguntasNoIncluyenCorrecta() throws Exception {
        mockMvc.perform(get("/api/tests/" + testId + "/preguntas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].texto").exists())
                .andExpect(jsonPath("$[0].correcta").doesNotExist());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Crear tests requiere rol ADMIN")
    void crearTestRequiereAdmin() throws Exception {
        String body = "{\"titulo\":\"Nuevo\"}";

        mockMvc.perform(post("/api/tests/create").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/tests/create").contentType(MediaType.APPLICATION_JSON).content(body)
                        .header("Authorization", "Bearer " + tokenPara("usuario", "USER")))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/tests/create").contentType(MediaType.APPLICATION_JSON).content(body)
                        .header("Authorization", "Bearer " + tokenPara("admin", "ADMIN")))
                .andExpect(status().isOk());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("El registro ignora id, progreso y rol enviados por el cliente")
    void registroNoPermiteAsignarCamposProtegidos() throws Exception {
        String body = "{\"username\":\"nuevo\",\"password\":\"clave\",\"email\":\"n@test.com\","
                + "\"id\":999,\"progress\":100,\"role\":\"ADMIN\"}";

        mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        Usuario guardado = userRepository.findByUsername("nuevo").orElseThrow();
        assertNotEquals(999L, guardado.getId());
        assertEquals(0, guardado.getProgress());
        assertEquals("USER", guardado.getRole());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Evaluar requiere sesión y el progreso no se duplica al repetir un test")
    void evaluarActualizaProgresoSinDuplicar() throws Exception {
        String respuestas = "[{\"preguntaId\":" + preguntaId + ",\"respuesta\":1}]";

        mockMvc.perform(post("/api/tests/" + testId + "/evaluar").contentType(MediaType.APPLICATION_JSON).content(respuestas))
                .andExpect(status().isForbidden());

        String token = tokenPara("alumno", "USER");
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/tests/" + testId + "/evaluar").contentType(MediaType.APPLICATION_JSON).content(respuestas)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.puntuacion").value(100))
                    .andExpect(jsonPath("$.progreso").value(100));
        }

        mockMvc.perform(get("/api/progress").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("El endpoint antiguo para fijar progreso ya no existe")
    void progresoNoSePuedeFijarDesdeElCliente() throws Exception {
        mockMvc.perform(post("/api/progress/update?userId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"testId\":1,\"puntuacion\":100}")
                        .header("Authorization", "Bearer " + tokenPara("tramposo", "USER")))
                .andExpect(status().is4xxClientError());
    }
}
