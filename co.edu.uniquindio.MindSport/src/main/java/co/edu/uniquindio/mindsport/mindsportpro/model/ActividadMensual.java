package co.edu.uniquindio.mindsport.mindsportpro.model;

import java.time.YearMonth;

/**
 * DTO para tendencias de actividad mensual.
 */
public class ActividadMensual {
    private Integer ano;
    private Integer mesNumero;
    private String mesNombre;
    private String periodo; // formato yyyy-MM
    private Integer totalSesiones;
    private Integer atletasActivos;
    private Integer rutinasUtilizadas;
    private Double puntuacionPromedio;
    private Double duracionPromedioMin;
    private Integer tiempoTotalMin;

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMesNumero() {
        return mesNumero;
    }

    public void setMesNumero(Integer mesNumero) {
        this.mesNumero = mesNumero;
    }

    public String getMesNombre() {
        return mesNombre;
    }

    public void setMesNombre(String mesNombre) {
        this.mesNombre = mesNombre;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Integer getTotalSesiones() {
        return totalSesiones;
    }

    public void setTotalSesiones(Integer totalSesiones) {
        this.totalSesiones = totalSesiones;
    }

    public Integer getAtletasActivos() {
        return atletasActivos;
    }

    public void setAtletasActivos(Integer atletasActivos) {
        this.atletasActivos = atletasActivos;
    }

    public Integer getRutinasUtilizadas() {
        return rutinasUtilizadas;
    }

    public void setRutinasUtilizadas(Integer rutinasUtilizadas) {
        this.rutinasUtilizadas = rutinasUtilizadas;
    }

    public Double getPuntuacionPromedio() {
        return puntuacionPromedio;
    }

    public void setPuntuacionPromedio(Double puntuacionPromedio) {
        this.puntuacionPromedio = puntuacionPromedio;
    }

    public Double getDuracionPromedioMin() {
        return duracionPromedioMin;
    }

    public void setDuracionPromedioMin(Double duracionPromedioMin) {
        this.duracionPromedioMin = duracionPromedioMin;
    }

    public Integer getTiempoTotalMin() {
        return tiempoTotalMin;
    }

    public void setTiempoTotalMin(Integer tiempoTotalMin) {
        this.tiempoTotalMin = tiempoTotalMin;
    }

    public YearMonth getYearMonth() {
        if (ano == null || mesNumero == null) return null;
        return YearMonth.of(ano, mesNumero);
    }
}
