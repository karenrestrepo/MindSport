package co.edu.uniquindio.mindsport.mindsportpro.model;

/**
 * DTO para reporte de rutinas mas utilizadas con estadisticas.
 */
public class RutinaEstadistica extends Rutina {
    private String nombresCoach;
    private String apellidosCoach;
    private Integer sesionesRealizadas;
    private Integer atletasUnicos;
    private Double puntuacionPromedio;

    public String getNombresCoach() {
        return nombresCoach;
    }

    public void setNombresCoach(String nombresCoach) {
        this.nombresCoach = nombresCoach;
    }

    public String getApellidosCoach() {
        return apellidosCoach;
    }

    public void setApellidosCoach(String apellidosCoach) {
        this.apellidosCoach = apellidosCoach;
    }

    public Integer getSesionesRealizadas() {
        return sesionesRealizadas;
    }

    public void setSesionesRealizadas(Integer sesionesRealizadas) {
        this.sesionesRealizadas = sesionesRealizadas;
    }

    public Integer getAtletasUnicos() {
        return atletasUnicos;
    }

    public void setAtletasUnicos(Integer atletasUnicos) {
        this.atletasUnicos = atletasUnicos;
    }

    public Double getPuntuacionPromedio() {
        return puntuacionPromedio;
    }

    public void setPuntuacionPromedio(Double puntuacionPromedio) {
        this.puntuacionPromedio = puntuacionPromedio;
    }
}
