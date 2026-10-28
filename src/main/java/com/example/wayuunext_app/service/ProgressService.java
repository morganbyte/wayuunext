package com.example.wayuunext_app.service;

import com.example.wayuunext_app.model.ResultadoUsuario;
import com.example.wayuunext_app.model.Test;
import com.example.wayuunext_app.model.Usuario;
import com.example.wayuunext_app.repository.ResultadoUsuarioRepository;
import com.example.wayuunext_app.repository.TestRepository;
import com.example.wayuunext_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgressService {
    public static final int PUNTUACION_APROBADO = 70;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private ResultadoUsuarioRepository resultadoUsuarioRepository;

    // guarda el intento y recalcula el progreso a partir de los tests aprobados,
    // así repetir un test aprobado no suma progreso dos veces
    @Transactional
    public int registrarResultado(Usuario usuario, Test test, int puntuacion) {
        boolean aprobado = puntuacion >= PUNTUACION_APROBADO;
        resultadoUsuarioRepository.save(new ResultadoUsuario(usuario, test, puntuacion, aprobado));

        long aprobados = resultadoUsuarioRepository.countTestsAprobados(usuario.getId());
        long totalTests = testRepository.count();
        int progreso = totalTests == 0 ? 0 : (int) Math.min(100, aprobados * 100 / totalTests);

        usuario.setProgress(progreso);
        userRepository.save(usuario);
        return progreso;
    }
}
