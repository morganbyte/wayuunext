package com.example.wayuunext_app.model;


public class RespuestaDTO {
    private Long preguntaId;
    private Integer respuesta;

    public RespuestaDTO() { }

    public RespuestaDTO(Long preguntaId, Integer respuesta) {
        this.preguntaId = preguntaId;
        this.respuesta = respuesta;
    }

    public Long getPreguntaId() {
        return preguntaId;
    }

    public void setPreguntaId(Long preguntaId) {
        this.preguntaId = preguntaId;
    }

    public Integer getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Integer respuesta) {
        this.respuesta = respuesta;
    }

    @Override
    public String toString() {
        return "RespuestaDTO{" +
                "preguntaId=" + preguntaId +
                ", respuesta=" + respuesta +
                '}';
    }
}
