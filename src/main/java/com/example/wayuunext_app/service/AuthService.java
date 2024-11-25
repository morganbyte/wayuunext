package com.example.wayuunext_app.service;

import com.example.wayuunext_app.model.Usuario;
import com.example.wayuunext_app.repository.UserRepository;
import com.example.wayuunext_app.security.JwUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwUtil jwUtil;

    public String login(String username, String password) throws Exception {
        Usuario user = userRepository.findByUsername(username).orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Credenciales inválidas");      
        }

        return jwUtil.generateToken(user);
    }

    public Usuario getUsuarioByUsername(String username) throws Exception {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
    }
}
