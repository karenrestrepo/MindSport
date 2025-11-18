package co.edu.uniquindio.mindsport.mindsportpro.model;

/**
 * DTO para medir exclusividad de atletas atendidos por coach.
 */
public class CoachExclusividad {
    private String cedulaCoach;
    private String nombresCoach;
    private String apellidosCoach;
    private Integer atletasAtendidos;
    private Integer atletasExclusivos;

    public String getCedulaCoach() {
        return cedulaCoach;
    }

    public void setCedulaCoach(String cedulaCoach) {
        this.cedulaCoach = cedulaCoach;
    }

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

    public Integer getAtletasAtendidos() {
        return atletasAtendidos;
    }

    public void setAtletasAtendidos(Integer atletasAtendidos) {
        this.atletasAtendidos = atletasAtendidos;
    }

    public Integer getAtletasExclusivos() {
        return atletasExclusivos;
    }

    public void setAtletasExclusivos(Integer atletasExclusivos) {
        this.atletasExclusivos = atletasExclusivos;
    }
}
