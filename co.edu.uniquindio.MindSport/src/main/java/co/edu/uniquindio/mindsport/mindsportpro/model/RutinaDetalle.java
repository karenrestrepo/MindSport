package co.edu.uniquindio.mindsport.mindsportpro.model;

/**
 * DTO para la composicion detallada de una rutina.
 */
public class RutinaDetalle extends Rutina {
    private String nivelDificultadTexto;
    private Integer numeroEjercicios;
    private Integer duracionTotalEjercicios;
    private Double duracionPromedioEjercicio;
    private Integer ejerciciosFisicos;
    private Integer ejerciciosMentales;
    private Integer ejerciciosMixtos;
    private String fasesIncluidas;
    private String nombresCoach;
    private String apellidosCoach;

    public String getNivelDificultadTexto() {
        return nivelDificultadTexto;
    }

    public void setNivelDificultadTexto(String nivelDificultadTexto) {
        this.nivelDificultadTexto = nivelDificultadTexto;
    }

    public Integer getNumeroEjercicios() {
        return numeroEjercicios;
    }

    public void setNumeroEjercicios(Integer numeroEjercicios) {
        this.numeroEjercicios = numeroEjercicios;
    }

    public Integer getDuracionTotalEjercicios() {
        return duracionTotalEjercicios;
    }

    public void setDuracionTotalEjercicios(Integer duracionTotalEjercicios) {
        this.duracionTotalEjercicios = duracionTotalEjercicios;
    }

    public Double getDuracionPromedioEjercicio() {
        return duracionPromedioEjercicio;
    }

    public void setDuracionPromedioEjercicio(Double duracionPromedioEjercicio) {
        this.duracionPromedioEjercicio = duracionPromedioEjercicio;
    }

    public Integer getEjerciciosFisicos() {
        return ejerciciosFisicos;
    }

    public void setEjerciciosFisicos(Integer ejerciciosFisicos) {
        this.ejerciciosFisicos = ejerciciosFisicos;
    }

    public Integer getEjerciciosMentales() {
        return ejerciciosMentales;
    }

    public void setEjerciciosMentales(Integer ejerciciosMentales) {
        this.ejerciciosMentales = ejerciciosMentales;
    }

    public Integer getEjerciciosMixtos() {
        return ejerciciosMixtos;
    }

    public void setEjerciciosMixtos(Integer ejerciciosMixtos) {
        this.ejerciciosMixtos = ejerciciosMixtos;
    }

    public String getFasesIncluidas() {
        return fasesIncluidas;
    }

    public void setFasesIncluidas(String fasesIncluidas) {
        this.fasesIncluidas = fasesIncluidas;
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
}
