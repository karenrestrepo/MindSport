package co.edu.uniquindio.mindsport.mindsportpro.model;

import java.time.LocalDate;
import java.util.Objects;

public class Sesion {
    private Integer id;
    private String cedulaAtleta;          // FK al usuario (Atleta)
    private Integer rutinaId;           // FK a la rutina ejecutada
    private LocalDate fecha;    // fecha y hora de la ejecución
    private Integer duracionReal;
    private Double puntuacion;               // puntuación / métrica principal
    private String observacionCoach;

    public Sesion() { }

    public Sesion(Integer id, String cedulaAtleta, Integer rutinaId, LocalDate fecha,
                  Integer duracionReal, Double puntuacion, String observacionCoach) {
        this.id = id;
        this.cedulaAtleta = cedulaAtleta;
        this.rutinaId = rutinaId;
        this.fecha = fecha;
        this.duracionReal = duracionReal;
        this.puntuacion = puntuacion;
        this.observacionCoach = observacionCoach;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCedulaAtleta() { return cedulaAtleta; }
    public void setCedulaAtleta(String cedulaAtleta) { this.cedulaAtleta = cedulaAtleta; }
    public Integer getRutinaId() { return rutinaId; }
    public void setRutinaId(Integer rutinaId) { this.rutinaId = rutinaId; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Integer getDuracionReal() { return duracionReal; }
    public void setDuracionReal(Integer duracionReal) { this.duracionReal = duracionReal; }
    public Double getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Double puntuacion) { this.puntuacion = puntuacion; }
    public String getObservacionCoach() { return observacionCoach; }
    public void setObservacionCoach(String observacionCoach) { this.observacionCoach = observacionCoach; }

    @Override
    public String toString() {
        return "Sesion{" +
                "id=" + id +
                ", cedulaAtleta=" + cedulaAtleta +
                ", rutinaId=" + rutinaId +
                ", fecha=" + fecha +
                ", duracionReal=" + duracionReal +
                ", score=" + puntuacion +
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

