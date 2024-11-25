package com.example.wayuunext_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "opcion")
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pregunta_id")
    private Pregunta pregunta;

    private String texto;
    @Column(nullable = false)
    private Integer indice;

    public Opcion() { }

    public Opcion(String texto, Integer indice) {
        this.texto = texto;
        this.indice = indice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Integer getIndice() { return indice; }
    public void setIndice(Integer indice) { this.indice = indice; }
}
