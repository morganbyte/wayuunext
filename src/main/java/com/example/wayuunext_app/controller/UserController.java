package com.example.wayuunext_app.controller;

import com.example.wayuunext_app.model.Usuario;
import com.example.wayuunext_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController { 
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody Usuario usuario) {
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password cannot be null or empty");
        }

        Usuario savedUser = userService.addUser(usuario);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario usuario) {
        // verifica que la contraseña no sea nula ni vacía
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password cannot be null or empty");
        }
        
        Usuario savedUser = userService.addUser(usuario);
        return ResponseEntity.ok(savedUser);
    }
}
