package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Ejercicio;
import co.edu.uniquindio.mindsport.mindsportpro.enums.FaseUso;
import co.edu.uniquindio.mindsport.mindsportpro.enums.TipoEjercicio;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EjercicioDAOJdbc {

    private static EjercicioDAOJdbc instancia;

    private EjercicioDAOJdbc() {}

    public static EjercicioDAOJdbc getInstancia() {
        if (instancia == null) instancia = new EjercicioDAOJdbc();
        return instancia;
    }

    public Ejercicio crear(Ejercicio ejercicio) {
        if (ejercicio == null) return null;
        String sql = "INSERT INTO Ejercicio (faseUso, titulo, descripcion, duracion, tipoEjercicio) VALUES (?,?,?,?,?)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ejercicio.getFaseUso() != null ? ejercicio.getFaseUso().name() : null);
            ps.setString(2, ejercicio.getTitulo());
            ps.setString(3, ejercicio.getDescripcion());
            if (ejercicio.getDuracion() != null) ps.setInt(4, ejercicio.getDuracion()); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, ejercicio.getTipoEjercicio() != null ? ejercicio.getTipoEjercicio().name() : null);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    ejercicio.setId(generatedId);
                }
            }

            return ejercicio;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Ejercicio> listar() {
        List<Ejercicio> lista = new ArrayList<>();
        String sql = "SELECT id, faseUso, titulo, descripcion, duracion, tipoEjercicio FROM Ejercicio ORDER BY id";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Optional<Ejercicio> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id, faseUso, titulo, descripcion, duracion, tipoEjercicio FROM Ejercicio WHERE id = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean actualizar(Ejercicio ejercicio) {
        if (ejercicio == null || ejercicio.getId() == null) return false;
        String sql = "UPDATE Ejercicio SET faseUso = ?, titulo = ?, descripcion = ?, duracion = ?, tipoEjercicio = ? WHERE id = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, ejercicio.getFaseUso() != null ? ejercicio.getFaseUso().name() : null);
            ps.setString(2, ejercicio.getTitulo());
            ps.setString(3, ejercicio.getDescripcion());
            if (ejercicio.getDuracion() != null) ps.setInt(4, ejercicio.getDuracion()); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, ejercicio.getTipoEjercicio() != null ? ejercicio.getTipoEjercicio().name() : null);
            ps.setInt(6, ejercicio.getId());

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(Ejercicio ejercicio) {
        if (ejercicio == null || ejercicio.getId() == null) return false;
        return eliminarPorId(ejercicio.getId());
    }

    public boolean eliminarPorId(Integer id) {
        if (id == null) return false;
        String sql = "DELETE FROM Ejercicio WHERE id = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ---------------- helper ----------------
    private Ejercicio mapRow(ResultSet rs) throws SQLException {
        Ejercicio e = new Ejercicio();
        e.setId(rs.getInt("id"));

        String faseStr = rs.getString("faseUso");
        if (faseStr != null) {
            try { e.setFaseUso(FaseUso.valueOf(faseStr)); } catch (Exception ignored) {}
        }

        e.setTitulo(rs.getString("titulo"));
        e.setDescripcion(rs.getString("descripcion"));

        int dur = rs.getInt("duracion");
        if (!rs.wasNull()) e.setDuracion(dur);

        String tipoStr = rs.getString("tipoEjercicio");
        if (tipoStr != null) {
            try { e.setTipoEjercicio(TipoEjercicio.valueOf(tipoStr)); } catch (Exception ignored) {}
        }

        return e;
    }
}
