package com.example.wayuunext_app.controller;

import java.security.Principal;

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

    // el progreso se actualiza en el servidor al evaluar un test (TestController),
    // aquí solo se consulta el del usuario autenticado
    @GetMapping
    public ResponseEntity<Integer> obtenerProgress(Principal principal) {
        Usuario usuario = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(usuario.getProgress());
    }
}
