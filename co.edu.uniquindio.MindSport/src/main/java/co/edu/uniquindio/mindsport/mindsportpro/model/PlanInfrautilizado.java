package co.edu.uniquindio.mindsport.mindsportpro.model;

/**
 * DTO para identificar planes con alta proporción de inscritos sin sesiones.
 */
public class PlanInfrautilizado {
    private Integer idPlan;
    private String nombrePlan;
    private Integer totalInscripciones;
    private Integer atletasConSesiones;
    private Integer atletasSinSesiones;
    private Double porcentajeSinSesiones;

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public Integer getTotalInscripciones() {
        return totalInscripciones;
    }

    public void setTotalInscripciones(Integer totalInscripciones) {
        this.totalInscripciones = totalInscripciones;
    }

    public Integer getAtletasConSesiones() {
        return atletasConSesiones;
    }

    public void setAtletasConSesiones(Integer atletasConSesiones) {
        this.atletasConSesiones = atletasConSesiones;
    }

    public Integer getAtletasSinSesiones() {
        return atletasSinSesiones;
    }

    public void setAtletasSinSesiones(Integer atletasSinSesiones) {
        this.atletasSinSesiones = atletasSinSesiones;
    }

    public Double getPorcentajeSinSesiones() {
        return porcentajeSinSesiones;
    }

    public void setPorcentajeSinSesiones(Double porcentajeSinSesiones) {
        this.porcentajeSinSesiones = porcentajeSinSesiones;
    }
}
