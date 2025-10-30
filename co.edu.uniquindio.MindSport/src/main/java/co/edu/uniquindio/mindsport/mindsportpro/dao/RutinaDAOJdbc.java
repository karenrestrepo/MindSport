package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Rutina;
import co.edu.uniquindio.mindsport.mindsportpro.model.Ejercicio;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RutinaDAOJdbc {

    private static RutinaDAOJdbc instancia;

    private RutinaDAOJdbc() {}

    public static RutinaDAOJdbc getInstancia() {
        if (instancia == null) instancia = new RutinaDAOJdbc();
        return instancia;
    }

    /**
     * Crea una rutina y sus relaciones con ejercicios (tabla Rutina_Ejercicio).
     * Realiza todo en una transacción.
     */
    public Rutina crear(Rutina rutina) {
        if (rutina == null) return null;
        String sqlInsertRutina = "INSERT INTO Rutina (cedulaCoach, titulo, descripcion, duracionEstimada, nivelDificultad, publicada) VALUES (?,?,?,?,?,?)";
        try (Connection cn = DBUtil.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement ps = cn.prepareStatement(sqlInsertRutina, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, rutina.getCedulaCoach());
                ps.setString(2, rutina.getTitulo());
                ps.setString(3, rutina.getDescripcion());
                if (rutina.getDuracionEstimada() != null) ps.setInt(4, rutina.getDuracionEstimada()); else ps.setNull(4, Types.INTEGER);
                ps.setString(5, rutina.getNivelDificultad() != null ? rutina.getNivelDificultad().name() : null);
                ps.setBoolean(6, rutina.isPublicada());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        rutina.setId(rs.getInt(1));
                    }
                }
            }

            // Insertar relaciones Rutina_Ejercicio
            insertarRelacionesRutinaEjercicio(cn, rutina);

            cn.commit();
            cn.setAutoCommit(true);
            return rutina;
        } catch (SQLException e) {
            e.printStackTrace();
            // En caso de error devolvemos null
            return null;
        }
    }

    /** Lista todas las rutinas, cargando también sus ejercicios asociados. */
    public List<Rutina> listar() {
        List<Rutina> lista = new ArrayList<>();
        String sql = "SELECT id, cedulaCoach, titulo, descripcion, duracionEstimada, nivelDificultad, publicada FROM Rutina ORDER BY id";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rutina r = mapRow(rs);
                cargarEjerciciosEnRutina(cn, r);
                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** Busca por id y carga ejercicios */
    public Optional<Rutina> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id, cedulaCoach, titulo, descripcion, duracionEstimada, nivelDificultad, publicada FROM Rutina WHERE id = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rutina r = mapRow(rs);
                    cargarEjerciciosEnRutina(cn, r);
                    return Optional.of(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /** Actualiza rutina y sus relaciones con ejercicios (elimina relaciones previas y re-inserta). */
    public boolean actualizar(Rutina rutina) {
        if (rutina == null || rutina.getId() == null) return false;
        String sqlUpdate = "UPDATE Rutina SET cedulaCoach = ?, titulo = ?, descripcion = ?, duracionEstimada = ?, nivelDificultad = ?, publicada = ? WHERE id = ?";
        try (Connection cn = DBUtil.getConnection()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sqlUpdate)) {
                ps.setString(1, rutina.getCedulaCoach());
                ps.setString(2, rutina.getTitulo());
                ps.setString(3, rutina.getDescripcion());
                if (rutina.getDuracionEstimada() != null) ps.setInt(4, rutina.getDuracionEstimada()); else ps.setNull(4, Types.INTEGER);
                ps.setString(5, rutina.getNivelDificultad() != null ? rutina.getNivelDificultad().name() : null);
                ps.setBoolean(6, rutina.isPublicada());
                ps.setInt(7, rutina.getId());
                ps.executeUpdate();
            }

            // Eliminar y volver a insertar relaciones en Rutina_Ejercicio
            try (PreparedStatement del = cn.prepareStatement("DELETE FROM Rutina_Ejercicio WHERE rutina_id = ?")) {
                del.setInt(1, rutina.getId());
                del.executeUpdate();
            }

            insertarRelacionesRutinaEjercicio(cn, rutina);

            cn.commit();
            cn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Elimina rutina. FK en Rutina_Ejercicio tiene ON DELETE CASCADE, pero eliminamos relaciones por seguridad. */
    public boolean eliminar(Rutina rutina) {
        if (rutina == null || rutina.getId() == null) return false;
        return eliminarPorId(rutina.getId());
    }

    public boolean eliminarPorId(Integer id) {
        if (id == null) return false;
        String sql = "DELETE FROM Rutina WHERE id = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Filtra por título, descripción o id (comportamiento similar al DAO en memoria). */
    public List<Rutina> filtrar(String texto) {
        if (texto == null || texto.trim().isEmpty()) return listar();
        String f = "%" + texto.trim().toLowerCase() + "%";
        List<Rutina> resultado = new ArrayList<>();

        // Si texto es número, intentar búsqueda por id primero
        try {
            int posibleId = Integer.parseInt(texto.trim());
            buscarPorId(posibleId).ifPresent(resultado::add);
        } catch (NumberFormatException ignored) {}

        String sql = "SELECT id, cedulaCoach, titulo, descripcion, duracionEstimada, nivelDificultad, publicada FROM Rutina " +
                "WHERE LOWER(titulo) LIKE ? OR LOWER(descripcion) LIKE ? OR LOWER(cedulacoach) LIKE ? ORDER BY id";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, f);
            ps.setString(2, f);
            ps.setString(3, f);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rutina r = mapRow(rs);
                    cargarEjerciciosEnRutina(cn, r);
                    // evitar duplicados si ya agregó por id
                    if (resultado.stream().noneMatch(x -> x.getId() != null && x.getId().equals(r.getId()))) {
                        resultado.add(r);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    // -------- helpers privados --------

    private Rutina mapRow(ResultSet rs) throws SQLException {
        Rutina r = new Rutina();
        r.setId(rs.getInt("id"));
        r.setCedulaCoach(rs.getString("cedulaCoach"));
        r.setTitulo(rs.getString("titulo"));
        r.setDescripcion(rs.getString("descripcion"));

        int dur = rs.getInt("duracionEstimada");
        if (!rs.wasNull()) r.setDuracionEstimada(dur);

        String nivel = rs.getString("nivelDificultad");
        if (nivel != null) {
            try {
                // intento convertir si el enum existe en tu proyecto
                r.setNivelDificultad(Enum.valueOf(r.getNivelDificultad().getDeclaringClass(), nivel));
            } catch (Exception ex) {
                // si falla (p. ej. no coincide el enum exacto), no hacer nada
            }
        }

        // publicada (tinyint)
        r.setPublicada(rs.getBoolean("publicada"));

        return r;
    }

    /**
     * Inserta registros en Rutina_Ejercicio según la lista de ejercicios en el objeto rutina.
     * Usa la conexión recibida (parte de una transacción).
     */
    private void insertarRelacionesRutinaEjercicio(Connection cn, Rutina rutina) throws SQLException {
        if (rutina.getEjercicios() == null || rutina.getEjercicios().isEmpty()) return;
        String sqlIns = "INSERT INTO Rutina_Ejercicio (rutina_id, ejercicio_id) VALUES (?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sqlIns)) {
            for (Ejercicio e : rutina.getEjercicios()) {
                if (e != null && e.getId() != null) {
                    ps.setInt(1, rutina.getId());
                    ps.setInt(2, e.getId());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    /**
     * Carga la lista de Ejercicio (objetos completos) asociados a la rutina.
     * Para obtener los objetos Ejercicio consulta la tabla Ejercicio por id.
     */
    private void cargarEjerciciosEnRutina(Connection cn, Rutina rutina) {
        if (rutina == null || rutina.getId() == null) return;
        String sql = "SELECT e.id, e.faseUso, e.titulo, e.descripcion, e.duracion, e.tipoEjercicio " +
                "FROM Ejercicio e " +
                "INNER JOIN Rutina_Ejercicio re ON e.id = re.ejercicio_id " +
                "WHERE re.rutina_id = ? ORDER BY e.id";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, rutina.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<Ejercicio> ejercicios = new ArrayList<>();
                while (rs.next()) {
                    Ejercicio ej = new Ejercicio();
                    ej.setId(rs.getInt("id"));
                    ej.setTitulo(rs.getString("titulo"));
                    ej.setDescripcion(rs.getString("descripcion"));
                    int dur = rs.getInt("duracion");
                    if (!rs.wasNull()) ej.setDuracion(dur);

                    String fase = rs.getString("faseUso");
                    if (fase != null) {
                        try { ej.setFaseUso(Enum.valueOf(ej.getFaseUso().getDeclaringClass(), fase)); } catch (Exception ignored) {}
                    }

                    String tipo = rs.getString("tipoEjercicio");
                    if (tipo != null) {
                        try { ej.setTipoEjercicio(Enum.valueOf(ej.getTipoEjercicio().getDeclaringClass(), tipo)); } catch (Exception ignored) {}
                    }

                    ejercicios.add(ej);
                }
                rutina.setEjercicios(ejercicios);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

