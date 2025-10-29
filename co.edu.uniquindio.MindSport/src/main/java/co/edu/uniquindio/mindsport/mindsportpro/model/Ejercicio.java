package co.edu.uniquindio.mindsport.mindsportpro.model;
import co.edu.uniquindio.mindsport.mindsportpro.enums.FaseUso;
import co.edu.uniquindio.mindsport.mindsportpro.enums.TipoEjercicio;

import java.util.Objects;

public class Ejercicio {
    private Integer id;
    private Integer rutinaId;
    private FaseUso faseUso;
    private String titulo;
    private String descripcion;
    private Integer duracion;
    private TipoEjercicio tipoEjercicio;
    public Ejercicio() { }

    public Ejercicio(Integer id, Integer rutinaId, FaseUso faseUso, String titulo,
                     String descripcion, Integer duracion) {
        this.id = id;
        this.rutinaId = rutinaId;
        this.faseUso = faseUso;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.duracion = duracion;
    }

    // Getters y setters


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRutinaId() {
        return rutinaId;
    }

    public void setRutinaId(Integer rutinaId) {
        this.rutinaId = rutinaId;
    }

    public FaseUso getFaseUso() {
        return faseUso;
    }

    public void setFaseUso(FaseUso faseUso) {
        this.faseUso = faseUso;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public TipoEjercicio getTipoEjercicio() {
        return tipoEjercicio;
    }

    public void setTipoEjercicio(TipoEjercicio tipoEjercicio) {
        this.tipoEjercicio = tipoEjercicio;
    }

    @Override
    public String toString() {
        return "Ejercicio{" +
                "id=" + id +
                ", orden=" + faseUso +
                ", titulo='" + titulo + '\'' +
                ", duracionSegundos=" + duracion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ejercicio ejercicio = (Ejercicio) o;
        return Objects.equals(id, ejercicio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
