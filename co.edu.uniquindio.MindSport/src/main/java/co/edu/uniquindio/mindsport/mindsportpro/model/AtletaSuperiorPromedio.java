package co.edu.uniquindio.mindsport.mindsportpro.model;

/**
 * DTO para atletas cuyo total de sesiones es mayor o igual al promedio global.
 */
public class AtletaSuperiorPromedio {
    private String cedula;
    private String nombres;
    private String apellidos;
    private Integer totalSesiones;
    private Double puntuacionPromedio;
    private Integer minutosTotales;

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Integer getTotalSesiones() {
        return totalSesiones;
    }

    public void setTotalSesiones(Integer totalSesiones) {
        this.totalSesiones = totalSesiones;
    }

    public Double getPuntuacionPromedio() {
        return puntuacionPromedio;
    }

    public void setPuntuacionPromedio(Double puntuacionPromedio) {
        this.puntuacionPromedio = puntuacionPromedio;
    }

    public Integer getMinutosTotales() {
        return minutosTotales;
    }

    public void setMinutosTotales(Integer minutosTotales) {
        this.minutosTotales = minutosTotales;
    }
}
