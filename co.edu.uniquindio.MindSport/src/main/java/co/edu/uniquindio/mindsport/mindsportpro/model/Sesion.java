package co.edu.uniquindio.mindsport.mindsportpro.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Sesion {
    private Integer id;
    private Integer usuarioId;          // FK al usuario (deportista)
    private Integer rutinaId;           // FK a la rutina ejecutada
    private LocalDateTime fechaHora;    // fecha y hora de la ejecución
    private Integer duracionRealSegundos;
    private Double score;               // puntuación / métrica principal
    private Map<String, Double> metricas = new HashMap<>(); // métricas adicionales
    private String notasDeportista;
    private String feedbackCoach;

    public Sesion() { }

    public Sesion(Integer id, Integer usuarioId, Integer rutinaId, LocalDateTime fechaHora,
                  Integer duracionRealSegundos, Double score, Map<String, Double> metricas,
                  String notasDeportista, String feedbackCoach) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.rutinaId = rutinaId;
        this.fechaHora = fechaHora;
        this.duracionRealSegundos = duracionRealSegundos;
        this.score = score;
        if (metricas != null) this.metricas = metricas;
        this.notasDeportista = notasDeportista;
        this.feedbackCoach = feedbackCoach;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getRutinaId() { return rutinaId; }
    public void setRutinaId(Integer rutinaId) { this.rutinaId = rutinaId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public Integer getDuracionRealSegundos() { return duracionRealSegundos; }
    public void setDuracionRealSegundos(Integer duracionRealSegundos) { this.duracionRealSegundos = duracionRealSegundos; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Map<String, Double> getMetricas() { return metricas; }
    public void setMetricas(Map<String, Double> metricas) { this.metricas = metricas; }
    public String getNotasDeportista() { return notasDeportista; }
    public void setNotasDeportista(String notasDeportista) { this.notasDeportista = notasDeportista; }
    public String getFeedbackCoach() { return feedbackCoach; }
    public void setFeedbackCoach(String feedbackCoach) { this.feedbackCoach = feedbackCoach; }

    @Override
    public String toString() {
        return "Sesion{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", rutinaId=" + rutinaId +
                ", fechaHora=" + fechaHora +
                ", duracionRealSegundos=" + duracionRealSegundos +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sesion sesion = (Sesion) o;
        return Objects.equals(id, sesion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

