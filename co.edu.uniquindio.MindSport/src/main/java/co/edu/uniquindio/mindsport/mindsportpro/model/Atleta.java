package co.edu.uniquindio.mindsport.mindsportpro.model;

import co.edu.uniquindio.mindsport.mindsportpro.enums.Genero;
import co.edu.uniquindio.mindsport.mindsportpro.enums.TipoPerfil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Atleta extends Usuario{
    private TipoPerfil tipoPerfil;
    private Double peso;
    private Double altura;
    private LocalDate fechaNacimiento;
    private List<Sesion> historialSesiones = new ArrayList<>();
    private List<Logro> listaLogros = new ArrayList<>();

    public Atleta() {
        super();
        this.setRol(1);  // ← 1 = ATLETA
    }

    public Atleta(String id, String nombres, String apellidos, String correo, Genero genero, String contrasena,
                  List<String> telefonos, LocalDateTime fechaRegistro, Integer rol, TipoPerfil tipoPerfil,
                  double peso, double altura, LocalDate fechaNacimiento, List<Sesion> historialSesiones, List<Logro> listaLogros) {
        super(id, nombres, apellidos, correo, genero, contrasena, telefonos, fechaRegistro, rol);
        this.tipoPerfil = tipoPerfil;
    }

    public TipoPerfil getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public List<Sesion> getHistorialSesiones() {
        return historialSesiones;
    }

    public void setHistorialSesiones(List<Sesion> historialSesiones) {
        this.historialSesiones = historialSesiones;
    }

    public List<Logro> getListaLogros() {
        return listaLogros;
    }

    public void setListaLogros(List<Logro> listaLogros) {
        this.listaLogros = listaLogros;
    }

    @Override
    public String toString() {
        return "Atleta{" +
                "tipoPerfil=" + (tipoPerfil != null ? tipoPerfil.toString() : "null") +
                ", peso=" + peso +
                ", altura=" + altura +
                ", fechaNacimiento=" + fechaNacimiento +
                ", historialSesiones=" + historialSesiones +
                ", listaLogros=" + listaLogros +
                "} " + super.toString();
    }
}
