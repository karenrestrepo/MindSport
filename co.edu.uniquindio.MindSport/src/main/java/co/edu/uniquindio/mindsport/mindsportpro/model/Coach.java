package co.edu.uniquindio.mindsport.mindsportpro.model;

import co.edu.uniquindio.mindsport.mindsportpro.enums.Genero;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Coach extends Usuario{
    private String idProfesional;
    private String especialidad;
    private String centroTrabajo;
    private List<String> listaCertificaciones = new ArrayList<>();
    private String disponibilidad;

    public Coach() {
        super();
        this.setRol(2);  // ← 2 = COACH
    }

    public Coach(String id, String nombres, String apellidos, String correo, Genero genero, String contrasena,
                 List<String> telefonos, LocalDateTime fechaRegistro, Integer rol, String idProfesional, String especialidad,
                 String centroTrabajo, List<String> listaCertificaciones, String disponibilidad) {
        // ↑ CAMBIAR: Rol rol → Integer rol
        super(id, nombres, apellidos, correo, genero, contrasena, telefonos, fechaRegistro, rol);
        this.idProfesional = idProfesional;
        this.especialidad = especialidad;
        this.centroTrabajo = centroTrabajo;
        this.listaCertificaciones = listaCertificaciones;
        this.disponibilidad = disponibilidad;
    }

    public String getIdProfesional() {
        return idProfesional;
    }

    public void setIdProfesional(String idProfesional) {
        this.idProfesional = idProfesional;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getCentroTrabajo() {
        return centroTrabajo;
    }

    public void setCentroTrabajo(String centroTrabajo) {
        this.centroTrabajo = centroTrabajo;
    }

    public List<String> getListaCertificaciones() {
        return listaCertificaciones;
    }

    public void setListaCertificaciones(List<String> listaCertificaciones) {
        this.listaCertificaciones = listaCertificaciones;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    @Override
    public String toString() {
        return "Coach{" +
                "idProfesional='" + idProfesional + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", centroTrabajo='" + centroTrabajo + '\'' +
                ", listaCertificaciones=" + listaCertificaciones +
                ", disponibilidad='" + disponibilidad + '\'' +
                "} " + super.toString();
    }
}
