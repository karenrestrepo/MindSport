package co.edu.uniquindio.mindsport.mindsportpro.model;
import java.time.LocalDateTime;
import java.util.Objects;

public class Logro {
    private Integer id;            // identificador del logro
    private String nombre;
    private String descripcion;
    private String criterio;       // criterio formal (ej. "completar_7_dias", "score>=90")
    private Integer puntos;        // puntos que otorga el logro (opcional)

    // Campos para cuando el logro está asignado a un usuario
    private Integer usuarioId;     // FK al usuario si es un registro
    private LocalDateTime obtenidoEn;

    public Logro() { }

    // Constructor para definición de logro
    public Logro(Integer id, String nombre, String descripcion, String criterio, Integer puntos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.criterio = criterio;
        this.puntos = puntos;
    }

    // Constructor para registro de logro asignado
    public Logro(Integer id, String nombre, String descripcion, String criterio, Integer puntos,
                 Integer usuarioId, LocalDateTime obtenidoEn) {
        this(id, nombre, descripcion, criterio, puntos);
        this.usuarioId = usuarioId;
        this.obtenidoEn = obtenidoEn;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCriterio() { return criterio; }
    public void setCriterio(String criterio) { this.criterio = criterio; }
    public Integer getPuntos() { return puntos; }
    public void setPuntos(Integer puntos) { this.puntos = puntos; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getObtenidoEn() { return obtenidoEn; }
    public void setObtenidoEn(LocalDateTime obtenidoEn) { this.obtenidoEn = obtenidoEn; }

    @Override
    public String toString() {
        return "Logro{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", criterio='" + criterio + '\'' +
                ", puntos=" + puntos +
                ", usuarioId=" + usuarioId +
                ", obtenidoEn=" + obtenidoEn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Logro logro = (Logro) o;
        return Objects.equals(id, logro.id) && Objects.equals(usuarioId, logro.usuarioId)
                && Objects.equals(obtenidoEn, logro.obtenidoEn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, usuarioId, obtenidoEn);
    }
}