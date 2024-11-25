package com.example.wayuunext_app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.wayuunext_app.model.*;
import com.example.wayuunext_app.repository.*;

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
    private ResultadoUsuarioRepository resultadoUsuarioRepository;

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
            @RequestBody List<RespuestaDTO> respuestasUsuario) {
  
        List<Pregunta> preguntas = preguntaRepository.findByTestId(testId);
        int correctas = 0;
        List<Map<String, Object>> detalle = new ArrayList<>();

        for (Pregunta pregunta : preguntas) {
            RespuestaDTO respuesta = respuestasUsuario.stream()
                .filter(r -> r.getPreguntaId().equals(pregunta.getId()))
                .findFirst()
                .orElse(null);

            if (respuesta == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Faltan respuestas para alguna pregunta"
                ));
            }

            Opcion opcionCorrecta = opcionRepository.findByPreguntaIdAndIndice(pregunta.getId(), respuesta.getRespuesta()).orElse(null);
            boolean esCorrecta = opcionCorrecta != null && 
                                 opcionCorrecta.getIndice().equals(respuesta.getRespuesta()) &&
                                 pregunta.getCorrecta().equals(opcionCorrecta.getIndice());

            if (esCorrecta) correctas++;

            Map<String, Object> info = new HashMap<>();
            info.put("pregunta", pregunta.getTexto());
            info.put("respuestaUsuario", respuesta.getRespuesta());
            info.put("respuestaCorrecta", pregunta.getCorrecta());
            info.put("esCorrecta", esCorrecta);
            detalle.add(info);
        }

        int puntuacion = (correctas * 100) / preguntas.size();
        String mensaje = puntuacion >= 70 ? "Test aprobado" : "Test no aprobado";

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("puntuacion", puntuacion);
        resultado.put("detalle", detalle);
        resultado.put("message", mensaje);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/{testId}/guardar-resultado")
    public ResponseEntity<String> guardarResultado(
        @PathVariable Long testId,
        @RequestBody ResultadoUsuario resultado) {

    resultadoUsuarioRepository.save(resultado);
    return ResponseEntity.ok("Resultado guardado exitosamente.");
    }

    @PostMapping("/create")
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        Test savedTest = testRepository.save(test);
        return ResponseEntity.ok(savedTest);
}
}
