package com.example.wayuunext_app.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "test")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private String tema;
    private String cantidadPreguntas;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pregunta> preguntas = new ArrayList<>();

    public Test() { }

    public Test(String titulo, String descripcion, String tema, String cantidadPreguntas) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tema = tema;
        this.cantidadPreguntas = cantidadPreguntas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public String getCantidadPreguntas() { return cantidadPreguntas; }
    public void setCantidadPreguntas(String cantidadPreguntas) { this.cantidadPreguntas = cantidadPreguntas; }

    public List<Pregunta> getPreguntas() { return preguntas; }
    public void setPreguntas(List<Pregunta> preguntas) { this.preguntas = preguntas; }
}
