package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Sesion;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SesionDAOJdbc {

    private static SesionDAOJdbc instancia;

    private SesionDAOJdbc() {}

    public static SesionDAOJdbc getInstancia() {
        if (instancia == null) instancia = new SesionDAOJdbc();
        return instancia;
    }

    /** Crear sesión (devuelve el objeto con id autogenerado) */
    public Sesion crear(Sesion s) {
        if (s == null) return null;
        String sql = "INSERT INTO Sesion (cedulaAtleta, rutinaId, fecha, duracionReal, puntuacion, observacionCoach) VALUES (?,?,?,?,?,?)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getCedulaAtleta());
            if (s.getRutinaId() != null) ps.setInt(2, s.getRutinaId()); else ps.setNull(2, Types.INTEGER);
            LocalDate fecha = s.getFecha() != null ? s.getFecha() : LocalDate.now();
            ps.setDate(3, Date.valueOf(fecha));
            if (s.getDuracionReal() != null) ps.setInt(4, s.getDuracionReal()); else ps.setNull(4, Types.INTEGER);
            if (s.getPuntuacion() != null) ps.setDouble(5, s.getPuntuacion()); else ps.setNull(5, Types.DOUBLE);
            ps.setString(6, s.getObservacionCoach());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) s.setId(rs.getInt(1));
            }
            // asegurar que el objeto tenga la fecha usada
            s.setFecha(fecha);
            return s;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Listar todas las sesiones */
    public List<Sesion> listar() {
        List<Sesion> lista = new ArrayList<>();
        String sql = "SELECT id, cedulaAtleta, rutinaId, fecha, duracionReal, puntuacion, observacionCoach FROM Sesion ORDER BY id DESC";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** Listar sesiones filtradas por cédula de atleta */
    public List<Sesion> listarPorAtleta(String cedulaAtleta) {
        if (cedulaAtleta == null || cedulaAtleta.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Sesion> lista = new ArrayList<>();
        String sql = "SELECT id, cedulaAtleta, rutinaId, fecha, duracionReal, puntuacion, observacionCoach FROM Sesion WHERE cedulaAtleta = ? ORDER BY id DESC";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cedulaAtleta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** Buscar por id */
    public Optional<Sesion> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id, cedulaAtleta, rutinaId, fecha, duracionReal, puntuacion, observacionCoach FROM Sesion WHERE id = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /** Actualizar sesión */
    public boolean actualizar(Sesion s) {
        if (s == null || s.getId() == null) return false;
        String sql = "UPDATE Sesion SET cedulaAtleta = ?, rutinaId = ?, fecha = ?, duracionReal = ?, puntuacion = ?, observacionCoach = ? WHERE id = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, s.getCedulaAtleta());
            if (s.getRutinaId() != null) ps.setInt(2, s.getRutinaId()); else ps.setNull(2, Types.INTEGER);
            if (s.getFecha() != null) ps.setDate(3, Date.valueOf(s.getFecha())); else ps.setNull(3, Types.DATE);
            if (s.getDuracionReal() != null) ps.setInt(4, s.getDuracionReal()); else ps.setNull(4, Types.INTEGER);
            if (s.getPuntuacion() != null) ps.setDouble(5, s.getPuntuacion()); else ps.setNull(5, Types.DOUBLE);
            ps.setString(6, s.getObservacionCoach());
            ps.setInt(7, s.getId());

            int afect = ps.executeUpdate();
            return afect > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Eliminar sesión */
    public boolean eliminar(Sesion s) {
        if (s == null || s.getId() == null) return false;
        return eliminarPorId(s.getId());
    }

    public boolean eliminarPorId(Integer id) {
        if (id == null) return false;
        String sql = "DELETE FROM Sesion WHERE id = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int afect = ps.executeUpdate();
            return afect > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- helper ----------------
    private Sesion mapRow(ResultSet rs) throws SQLException {
        Sesion s = new Sesion();
        s.setId(rs.getInt("id"));
        s.setCedulaAtleta(rs.getString("cedulaAtleta"));
        int rid = rs.getInt("rutinaId");
        if (!rs.wasNull()) s.setRutinaId(rid);
        Date d = rs.getDate("fecha");
        if (d != null) s.setFecha(d.toLocalDate());
        int dur = rs.getInt("duracionReal");
        if (!rs.wasNull()) s.setDuracionReal(dur);
        double p = rs.getDouble("puntuacion");
        if (!rs.wasNull()) s.setPuntuacion(p);
        s.setObservacionCoach(rs.getString("observacionCoach"));
        return s;
    }
}
