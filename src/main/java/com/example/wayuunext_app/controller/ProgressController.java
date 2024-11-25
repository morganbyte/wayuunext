package com.example.wayuunext_app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.wayuunext_app.model.*;
import com.example.wayuunext_app.repository.*;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestRepository testRepository;

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateProgress(@RequestParam Long userId, @RequestBody Map<String, Object> body) {
        int puntuacion;
        Long testId;

        try {
            puntuacion = Integer.parseInt(body.get("puntuacion").toString());
            testId = Long.parseLong(body.get("testId").toString());
        } catch (NumberFormatException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Datos inválidos en el cuerpo de la solicitud"));
        }

        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (testRepository.findById(testId).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El test especificado no existe"));
        }

        Map<String, String> response = new HashMap<>();

        System.out.println("Usuario ID: " + userId);
        System.out.println("Puntuación: " + puntuacion);
        System.out.println("Test ID: " + testId);

        if (puntuacion >= 70) {
            List<Test> tests = testRepository.findAll();
            int totalTests = tests.size();
            int nuevosPuntos = 100 / totalTests;

            usuario.setProgress(Math.min(usuario.getProgress() + nuevosPuntos, 100)); 
            userRepository.save(usuario);

            response.put("message", "Progreso actualizado correctamente");
            response.put("progreso", usuario.getProgress() + "%");
            response.put("puntuacion", String.valueOf(puntuacion));
            response.put("testId", String.valueOf(testId));
            return ResponseEntity.ok(response);
        }

        response.put("message", "El test no fue aprobado. No se actualizó el progreso.");
        response.put("puntuacion", String.valueOf(puntuacion));
        response.put("testId", String.valueOf(testId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Integer> obtenerProgress(@PathVariable Long userId) {
        System.out.println("Solicitud recibida para obtener progreso. UserID: " + userId);
        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        System.out.println("Progreso obtenido para el usuario: " + usuario.getProgress());
        return ResponseEntity.ok(usuario.getProgress());
    } 
}
