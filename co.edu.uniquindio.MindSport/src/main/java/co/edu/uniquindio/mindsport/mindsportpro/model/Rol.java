package co.edu.uniquindio.mindsport.mindsportpro.model;

public class Rol {
    private Integer codigo;
    private String descripcion;

    public Rol() {}

    public Rol(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return descripcion; // Para que se muestre en el ComboBox
    }
}
