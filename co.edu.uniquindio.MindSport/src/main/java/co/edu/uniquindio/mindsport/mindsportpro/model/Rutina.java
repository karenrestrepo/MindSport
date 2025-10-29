package co.edu.uniquindio.mindsport.mindsportpro.model;

import co.edu.uniquindio.mindsport.mindsportpro.enums.NivelDificultad;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rutina {
    private Integer id;
    private String cedulaCoach;
    private String titulo;
    private String descripcion;
    private Integer duracionEstimada;
    private NivelDificultad nivelDificultad;
    private List<Ejercicio> ejercicios = new ArrayList<>();
    private List<Tecnica> tecnicas = new ArrayList<>();
    private boolean publicada = false;

    public Rutina() { }

    public Rutina(Integer id, String cedulaCoach, String titulo, String descripcion,
                  Integer duracionEstimada, NivelDificultad nivelDificultad,
                  List<Ejercicio> ejercicios, List<Tecnica> tecnicas, boolean publicada) {
        this.id = id;
        this.cedulaCoach = cedulaCoach;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.duracionEstimada = duracionEstimada;
        this.nivelDificultad = nivelDificultad;
        if (ejercicios != null) this.ejercicios = ejercicios;
        if (tecnicas != null) this.tecnicas = tecnicas;
        this.publicada = publicada;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCedulaCoach() { return cedulaCoach; }
    public void setCedulaCoach(String cedulaCoach) { this.cedulaCoach = cedulaCoach; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getDuracionEstimada() { return duracionEstimada; }
    public void setDuracionEstimada(Integer duracionEstimada) { this.duracionEstimada = duracionEstimada; }

    public NivelDificultad getNivelDificultad() { return nivelDificultad; }
    public void setNivelDificultad(NivelDificultad nivelDificultad) { this.nivelDificultad = nivelDificultad; }

    public List<Ejercicio> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<Ejercicio> ejercicios) { this.ejercicios = ejercicios; }

    public List<Tecnica> getTecnicas() { return tecnicas; }
    public void setTecnicas(List<Tecnica> tecnicas) { this.tecnicas = tecnicas; }

    public boolean isPublicada() { return publicada; }
    public void setPublicada(boolean publicada) { this.publicada = publicada; }

    @Override
    public String toString() {
        return "Rutina{" +
                "id=" + id +
                ", cedulaCoach='" + cedulaCoach + '\'' +
                ", titulo='" + titulo + '\'' +
                ", duracionEstimada=" + duracionEstimada +
                ", nivelDificultad=" + nivelDificultad +
                ", ejercicios=" + (ejercicios != null ? ejercicios.size() : 0) +
                ", publicada=" + publicada +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rutina rutina = (Rutina) o;
        return Objects.equals(id, rutina.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}