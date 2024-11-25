package com.example.wayuunext_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.wayuunext_app.model.Opcion;

@Repository
public interface OpcionRepository extends JpaRepository<Opcion, Long> {
    Optional<Opcion> findByPreguntaIdAndIndice(Long preguntaId, Integer indice);
    List<Opcion> findByPreguntaId(Long preguntaId);
}
