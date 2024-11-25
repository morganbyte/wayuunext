package com.example.wayuunext_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.wayuunext_app.model.ResultadoUsuario;

@Repository
public interface ResultadoUsuarioRepository extends JpaRepository<ResultadoUsuario, Long> {
    
}
