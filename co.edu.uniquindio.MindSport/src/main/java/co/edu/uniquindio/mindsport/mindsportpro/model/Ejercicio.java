package co.edu.uniquindio.mindsport.mindsportpro.model;
import java.util.Objects;

public class Ejercicio {
    private Integer id;             // identificador del ejercicio
    private Integer rutinaId;       // FK a la rutina (opcional si se guarda embebido)
    private Integer orden;          // orden dentro de la rutina (1,2,3...)
    private String titulo;          // título del ejercicio
    private String descripcion;     // descripción detallada
    private Integer duracionSegundos; // duración en segundos

    public Ejercicio() { }

    public Ejercicio(Integer id, Integer rutinaId, Integer orden, String titulo,
                     String descripcion, Integer duracionSegundos) {
        this.id = id;
        this.rutinaId = rutinaId;
        this.orden = orden;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.duracionSegundos = duracionSegundos;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getRutinaId() { return rutinaId; }
    public void setRutinaId(Integer rutinaId) { this.rutinaId = rutinaId; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(Integer duracionSegundos) { this.duracionSegundos = duracionSegundos; }

    @Override
    public String toString() {
        return "Ejercicio{" +
                "id=" + id +
                ", orden=" + orden +
                ", titulo='" + titulo + '\'' +
                ", duracionSegundos=" + duracionSegundos +
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
