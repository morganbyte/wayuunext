package com.example.wayuunext_app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.wayuunext_app.model.*;
import com.example.wayuunext_app.repository.TestRepository;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;

    public Test getTestWithQuestions(Long testId) {
        return testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test no encontrado"));
        
    }

    public int evaluarRespuestas(List<Integer> respuestasUsuario, List<Integer> respuestasCorrectas) {
        int correctas = 0;
        for (int i = 0; i < respuestasUsuario.size(); i++) {
            if (respuestasUsuario.get(i).equals(respuestasCorrectas.get(i))) {
                correctas++;
            }
        }
        return (correctas * 100) / respuestasCorrectas.size();
    }
}
