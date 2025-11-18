package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.CoachExclusividad;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoachExclusividadDAOJdbc {

    private static CoachExclusividadDAOJdbc instancia;

    private CoachExclusividadDAOJdbc() {}

    public static CoachExclusividadDAOJdbc getInstancia() {
        if (instancia == null) instancia = new CoachExclusividadDAOJdbc();
        return instancia;
    }

    private static final String SQL = ""
            + "SELECT "
            + "    c.cedula AS cedulaCoach, "
            + "    u.nombres AS nombresCoach, "
            + "    u.apellidos AS apellidosCoach, "
            + "    (SELECT COUNT(DISTINCT s.cedulaAtleta) "
            + "     FROM Sesion s "
            + "     INNER JOIN Rutina r2 ON s.rutinaId = r2.id "
            + "     WHERE r2.cedulaCoach = c.cedula) AS atletasAtendidos, "
            + "    (SELECT COUNT(DISTINCT s.cedulaAtleta) "
            + "     FROM Sesion s "
            + "     INNER JOIN Rutina r2 ON s.rutinaId = r2.id "
            + "     WHERE r2.cedulaCoach = c.cedula "
            + "       AND NOT EXISTS ( "
            + "           SELECT 1 FROM Sesion s2 "
            + "           INNER JOIN Rutina r3 ON s2.rutinaId = r3.id "
            + "           WHERE s2.cedulaAtleta = s.cedulaAtleta "
            + "             AND r3.cedulaCoach <> c.cedula "
            + "       ) "
            + "    ) AS atletasExclusivos "
            + "FROM Coach c "
            + "INNER JOIN Usuario u ON c.cedula = u.cedula "
            + "ORDER BY atletasExclusivos DESC, atletasAtendidos DESC";

    public List<CoachExclusividad> listar() {
        List<CoachExclusividad> lista = new ArrayList<>();
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            System.out.println("[CoachExclusividadDAO] Registros cargados: " + lista.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private CoachExclusividad mapRow(ResultSet rs) throws SQLException {
        CoachExclusividad c = new CoachExclusividad();
        c.setCedulaCoach(rs.getString("cedulaCoach"));
        c.setNombresCoach(rs.getString("nombresCoach"));
        c.setApellidosCoach(rs.getString("apellidosCoach"));

        int atendidos = rs.getInt("atletasAtendidos");
        c.setAtletasAtendidos(rs.wasNull() ? null : atendidos);

        int exclusivos = rs.getInt("atletasExclusivos");
        c.setAtletasExclusivos(rs.wasNull() ? null : exclusivos);
        return c;
    }
}
