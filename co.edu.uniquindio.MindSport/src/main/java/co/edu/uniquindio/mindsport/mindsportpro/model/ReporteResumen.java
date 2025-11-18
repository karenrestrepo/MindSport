package co.edu.uniquindio.mindsport.mindsportpro.model;

import java.time.LocalDate;

/**
 * DTO de reporte que hereda datos base de Atleta.
 */
public class ReporteResumen extends Atleta {
    private Integer totalSesiones;
    private Double puntuacionPromedio;
    private Double duracionPromedioMinutos;
    private Integer tiempoTotalEntrenadoMinutos;
    private LocalDate primeraSesion;
    private LocalDate ultimaSesion;
    private Integer diasEntrenamiento;
    private String perfilDeportivoTexto;

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

    public Double getDuracionPromedioMinutos() {
        return duracionPromedioMinutos;
    }

    public void setDuracionPromedioMinutos(Double duracionPromedioMinutos) {
        this.duracionPromedioMinutos = duracionPromedioMinutos;
    }

    public Integer getTiempoTotalEntrenadoMinutos() {
        return tiempoTotalEntrenadoMinutos;
    }

    public void setTiempoTotalEntrenadoMinutos(Integer tiempoTotalEntrenadoMinutos) {
        this.tiempoTotalEntrenadoMinutos = tiempoTotalEntrenadoMinutos;
    }

    public LocalDate getPrimeraSesion() {
        return primeraSesion;
    }

    public void setPrimeraSesion(LocalDate primeraSesion) {
        this.primeraSesion = primeraSesion;
    }

    public LocalDate getUltimaSesion() {
        return ultimaSesion;
    }

    public void setUltimaSesion(LocalDate ultimaSesion) {
        this.ultimaSesion = ultimaSesion;
    }

    public Integer getDiasEntrenamiento() {
        return diasEntrenamiento;
    }

    public void setDiasEntrenamiento(Integer diasEntrenamiento) {
        this.diasEntrenamiento = diasEntrenamiento;
    }

    public String getPerfilDeportivoTexto() {
        if (perfilDeportivoTexto != null) {
            return perfilDeportivoTexto;
        }
        return getTipoPerfil() != null ? getTipoPerfil().toString() : null;
    }

    public void setPerfilDeportivoTexto(String perfilDeportivoTexto) {
        this.perfilDeportivoTexto = perfilDeportivoTexto;
    }
}
