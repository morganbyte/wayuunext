package com.example.wayuunext_app.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.wayuunext_app.model.*;
import com.example.wayuunext_app.repository.*;
import com.example.wayuunext_app.service.ProgressService;

@RestController
@RequestMapping("/api/tests")
public class TestController {
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private PreguntaRepository preguntaRepository;
    @Autowired
    private OpcionRepository opcionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProgressService progressService;

    @GetMapping
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    @GetMapping("/{testId}/preguntas")
    public List<Pregunta> getPreguntasByTestId(@PathVariable Long testId) {
        List<Pregunta> preguntas = preguntaRepository.findByTestId(testId);
        preguntas.forEach(pregunta -> {
            List<Opcion> opciones = opcionRepository.findByPreguntaId(pregunta.getId());
            pregunta.setOpciones(opciones);
        });
        return preguntas;
    }

    @PostMapping("/{testId}/evaluar")
    public ResponseEntity<Map<String, Object>> evaluarTest(
            @PathVariable Long testId,
            @RequestBody List<RespuestaDTO> respuestasUsuario,
            Principal principal) {

        Test test = testRepository.findById(testId).orElse(null);
        List<Pregunta> preguntas = preguntaRepository.findByTestId(testId);
        if (test == null || preguntas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        int correctas = 0;
        List<Map<String, Object>> detalle = new ArrayList<>();

        for (Pregunta pregunta : preguntas) {
            RespuestaDTO respuesta = respuestasUsuario.stream()
                .filter(r -> pregunta.getId().equals(r.getPreguntaId()))
                .findFirst()
                .orElse(null);

            if (respuesta == null || respuesta.getRespuesta() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Faltan respuestas para alguna pregunta"
                ));
            }

            // la corrección se hace solo aquí, el cliente nunca recibe la respuesta correcta antes de contestar
            boolean esCorrecta = respuesta.getRespuesta().equals(pregunta.getCorrecta());
            if (esCorrecta) correctas++;

            Map<String, Object> info = new HashMap<>();
            info.put("pregunta", pregunta.getTexto());
            info.put("respuestaUsuario", respuesta.getRespuesta());
            info.put("respuestaCorrecta", pregunta.getCorrecta());
            info.put("esCorrecta", esCorrecta);
            detalle.add(info);
        }

        int puntuacion = (correctas * 100) / preguntas.size();
        int progreso = progressService.registrarResultado(usuario, test, puntuacion);
        String mensaje = puntuacion >= ProgressService.PUNTUACION_APROBADO ? "Test aprobado" : "Test no aprobado";

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("puntuacion", puntuacion);
        resultado.put("detalle", detalle);
        resultado.put("message", mensaje);
        resultado.put("progreso", progreso);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/create")
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        Test savedTest = testRepository.save(test);
        return ResponseEntity.ok(savedTest);
    }
}
