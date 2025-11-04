package co.edu.uniquindio.mindsport.mindsportpro.model;

public class CentroTrabajo {
    private int idCentro;
    private String nombre;
    private String ciudad;

    public CentroTrabajo() {}

    public CentroTrabajo(int idCentro, String nombre, String ciudad) {
        this.idCentro = idCentro;
        this.nombre = nombre;
        this.ciudad = ciudad;
    }

    public int getIdCentro() {
        return idCentro;
    }

    public void setIdCentro(int idCentro) {
        this.idCentro = idCentro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return nombre + " (" + ciudad + ")";
    }
}
