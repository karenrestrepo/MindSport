package co.edu.uniquindio.mindsport.mindsportpro.model;
import java.util.Objects;

public class Tecnica {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String aplicabilidad; // p. ej. "Pre-competición, Entrenamiento, Recuperación"

    public Tecnica() { }

    public Tecnica(Integer id, String nombre, String descripcion, String aplicabilidad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.aplicabilidad = aplicabilidad;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getAplicabilidad() { return aplicabilidad; }
    public void setAplicabilidad(String aplicabilidad) { this.aplicabilidad = aplicabilidad; }

    @Override
    public String toString() {
        return "Tecnica{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", aplicabilidad='" + aplicabilidad + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tecnica tecnica = (Tecnica) o;
        return Objects.equals(id, tecnica.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}