package com.example.wayuunext_app.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "pregunta")
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    private String texto;
    private Integer correcta;

    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Opcion> opciones = new ArrayList<>();

    public Pregunta() { }

    public Pregunta(String texto, Integer correcta) {
        this.texto = texto;
        this.correcta = correcta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Integer getCorrecta() { return correcta; }
    public void setCorrecta(Integer correcta) { this.correcta = correcta; }

    public List<Opcion> getOpciones() { return opciones; }
    public void setOpciones(List<Opcion> opciones) { this.opciones = opciones; }
}
