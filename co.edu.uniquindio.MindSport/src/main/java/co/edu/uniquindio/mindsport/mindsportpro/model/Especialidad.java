package co.edu.uniquindio.mindsport.mindsportpro.model;

public class Especialidad {
    private int idEspecialidad;
    private String codigo;
    private String descripcion;

    public Especialidad() {}

    public Especialidad(int idEspecialidad, String codigo, String descripcion) {
        this.idEspecialidad = idEspecialidad;
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return codigo + " - " + descripcion;
    }
}
