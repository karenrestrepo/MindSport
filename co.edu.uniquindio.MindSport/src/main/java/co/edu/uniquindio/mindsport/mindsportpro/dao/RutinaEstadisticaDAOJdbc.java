package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.RutinaEstadistica;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RutinaEstadisticaDAOJdbc {

    private static RutinaEstadisticaDAOJdbc instancia;

    private RutinaEstadisticaDAOJdbc() {}

    public static RutinaEstadisticaDAOJdbc getInstancia() {
        if (instancia == null) instancia = new RutinaEstadisticaDAOJdbc();
        return instancia;
    }

    private static final String SQL_REPORTE = ""
            + "SELECT "
            + "    r.id AS idRutina, "
            + "    r.titulo AS tituloRutina, "
            + "    u.nombres AS nombresCoach, "
            + "    u.apellidos AS apellidosCoach, "
            + "    COUNT(s.id) AS sesionesRealizadas, "
            + "    COUNT(DISTINCT s.cedulaAtleta) AS atletasUnicos, "
            + "    AVG(s.puntuacion) AS puntuacionPromedio "
            + "FROM Rutina r "
            + "INNER JOIN Usuario u ON r.cedulaCoach = u.cedula "
            + "INNER JOIN Sesion s ON r.id = s.rutinaId "
            + "GROUP BY r.id, r.titulo, u.nombres, u.apellidos "
            + "ORDER BY COUNT(s.id) DESC";

    public List<RutinaEstadistica> listar() {
        List<RutinaEstadistica> lista = new ArrayList<>();
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_REPORTE);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            System.out.println("[RutinaEstadisticaDAO] Se cargaron " + lista.size() + " rutinas con estadisticas.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private RutinaEstadistica mapRow(ResultSet rs) throws SQLException {
        RutinaEstadistica r = new RutinaEstadistica();
        r.setId(rs.getInt("idRutina"));
        r.setTitulo(rs.getString("tituloRutina"));
        r.setNombresCoach(rs.getString("nombresCoach"));
        r.setApellidosCoach(rs.getString("apellidosCoach"));
        int sesiones = rs.getInt("sesionesRealizadas");
        r.setSesionesRealizadas(rs.wasNull() ? null : sesiones);
        int unicos = rs.getInt("atletasUnicos");
        r.setAtletasUnicos(rs.wasNull() ? null : unicos);
        double punt = rs.getDouble("puntuacionPromedio");
        r.setPuntuacionPromedio(rs.wasNull() ? null : punt);
        return r;
    }
}
