package com.example.wayuunext_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.wayuunext_app.model.ResultadoUsuario;

@Repository
public interface ResultadoUsuarioRepository extends JpaRepository<ResultadoUsuario, Long> {
    @Query("SELECT COUNT(DISTINCT r.test.id) FROM ResultadoUsuario r WHERE r.user.id = :userId AND r.completado = true")
    long countTestsAprobados(@Param("userId") Long userId);
}
