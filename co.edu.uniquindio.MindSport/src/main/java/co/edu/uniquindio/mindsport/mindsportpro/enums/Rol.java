package co.edu.uniquindio.mindsport.mindsportpro.enums;

public enum Rol {
    ATLETA(1),
    COACH(2);

    private final int codigo;

    Rol(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static Rol fromCodigo(int codigo) {
        for (Rol r : values()) {
            if (r.codigo == codigo) {
                return r;
            }
        }
        return null;
    }
}