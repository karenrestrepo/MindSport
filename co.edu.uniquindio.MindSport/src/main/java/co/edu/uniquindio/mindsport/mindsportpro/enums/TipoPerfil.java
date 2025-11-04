package co.edu.uniquindio.mindsport.mindsportpro.enums;


public enum TipoPerfil {
    CORREDOR_RESISTENCIA(1, "corredor", "resistencia"),
    CORREDOR_VELOCIDAD(2, "corredor", "velocidad"),
    BOXEADOR_FUERZA(3, "boxeador", "fuerza"),
    BOXEADOR_MIXTO(4, "boxeador", "mixto"),
    CICLISTA_RESISTENCIA(5, "ciclista", "resistencia"),
    FUTBOLISTA_VELOCIDAD(6, "futbolista", "velocidad"),
    GIMNASIO_HIPERTROFIA(7, "gimnasio", "hipertrofia"),
    GIMNASIO_FUERZA(8, "gimnasio", "fuerza");

    private final int id;
    private final String tipoPerfil;
    private final String enfoque;

    TipoPerfil(int id, String tipoPerfil, String enfoque) {
        this.id = id;
        this.tipoPerfil = tipoPerfil;
        this.enfoque = enfoque;
    }

    public int getId() {
        return id;
    }

    public String getTipoPerfil() {
        return tipoPerfil;
    }

    public String getEnfoque() {
        return enfoque;
    }

    @Override
    public String toString() {
        return tipoPerfil + " - " + enfoque;
    }

    // Buscar por ID (cuando lees desde la BD)
    public static TipoPerfil fromId(int id) {
        for (TipoPerfil tp : values()) {
            if (tp.id == id) {
                return tp;
            }
        }
        throw new IllegalArgumentException("ID de TipoPerfil no válido: " + id);
    }

    // Buscar por tipo y enfoque (si lo necesitas)
    public static TipoPerfil fromValues(String tipo, String enfoque) {
        for (TipoPerfil tp : values()) {
            if (tp.tipoPerfil.equalsIgnoreCase(tipo) && tp.enfoque.equalsIgnoreCase(enfoque)) {
                return tp;
            }
        }
        throw new IllegalArgumentException("Combinación no válida: " + tipo + " - " + enfoque);
    }
}

