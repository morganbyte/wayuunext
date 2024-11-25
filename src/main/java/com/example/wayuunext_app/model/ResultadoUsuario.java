package com.example.wayuunext_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "resultado_usuario")
public class ResultadoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario user;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    private Integer puntuacion;
    private Boolean completado;

    public ResultadoUsuario() { }

    public ResultadoUsuario(Integer puntuacion, Boolean completado) {
        this.puntuacion = puntuacion;
        this.completado = completado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }

    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
}

