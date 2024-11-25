package com.example.wayuunext_app.controller;

import com.example.wayuunext_app.model.AuthRequest;
import com.example.wayuunext_app.model.Usuario;
import com.example.wayuunext_app.service.AuthService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            String token = authService.login(authRequest.getUsername(), authRequest.getPassword());
            Usuario usuario = authService.getUsuarioByUsername(authRequest.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("authToken", token);
            response.put("userId", usuario.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication failed"));
        }
    }
}
