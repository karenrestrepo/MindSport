package co.edu.uniquindio.mindsport.mindsportpro.model;

import co.edu.uniquindio.mindsport.mindsportpro.enums.Genero;
import co.edu.uniquindio.mindsport.mindsportpro.enums.Rol;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public abstract class Usuario {
    private String nombres;
    private String apellidos;
    private String cedula;
    private String correo;
    private Genero genero;
    private String contrasena;
    private List<String> telefonos;
    private LocalDateTime fechaRegistro;
    private Rol rol;

    public Usuario(){
        this.fechaRegistro = LocalDateTime.now();
    }

    public Usuario(String cedula,String nombres, String apellidos, String correo, Genero genero, String contrasena,
                   List<String> telefonos, LocalDateTime fechaRegistro, Rol rol) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.genero = genero;
        this.contrasena = contrasena;
        this.telefonos = telefonos;
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDateTime.now();
        this.rol = rol;
    }

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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public List<String> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<String> telefonos) {
        this.telefonos = telefonos;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "cedula='" + cedula + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", correo='" + correo + '\'' +
                ", genero=" + genero +
                ", contrasena='" + contrasena + '\'' +
                ", telefonos=" + telefonos +
                ", fechaRegistro=" + fechaRegistro +
                ", rol='" + rol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;
        return Objects.equals(cedula, usuario.cedula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cedula);
    }
}
