package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.AtletaSuperiorPromedio;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AtletaSuperiorPromedioDAOJdbc {

    private static AtletaSuperiorPromedioDAOJdbc instancia;

    private AtletaSuperiorPromedioDAOJdbc() {}

    public static AtletaSuperiorPromedioDAOJdbc getInstancia() {
        if (instancia == null) instancia = new AtletaSuperiorPromedioDAOJdbc();
        return instancia;
    }

    private static final String SQL = ""
            + "SELECT "
            + "    u.cedula AS cedula, "
            + "    u.nombres AS nombres, "
            + "    u.apellidos AS apellidos, "
            + "    (SELECT COUNT(*) FROM Sesion s WHERE s.cedulaAtleta = a.cedula) AS totalSesiones, "
            + "    (SELECT AVG(s2.puntuacion) FROM Sesion s2 WHERE s2.cedulaAtleta = a.cedula) AS puntuacionPromedio, "
            + "    (SELECT SUM(s3.duracionReal) FROM Sesion s3 WHERE s3.cedulaAtleta = a.cedula) AS minutosTotales "
            + "FROM Usuario u "
            + "INNER JOIN Atleta a ON u.cedula = a.cedula "
            + "WHERE (SELECT COUNT(*) FROM Sesion s WHERE s.cedulaAtleta = a.cedula) >= ( "
            + "    SELECT AVG(t.cSesiones) FROM ( "
            + "        SELECT COUNT(*) AS cSesiones FROM Sesion GROUP BY cedulaAtleta "
            + "    ) t "
            + ") "
            + "ORDER BY totalSesiones DESC;";

    public List<AtletaSuperiorPromedio> listar() {
        List<AtletaSuperiorPromedio> lista = new ArrayList<>();
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            System.out.println("[AtletaSuperiorPromedioDAO] Registros cargados: " + lista.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private AtletaSuperiorPromedio mapRow(ResultSet rs) throws SQLException {
        AtletaSuperiorPromedio a = new AtletaSuperiorPromedio();
        a.setCedula(rs.getString("cedula"));
        a.setNombres(rs.getString("nombres"));
        a.setApellidos(rs.getString("apellidos"));

        int total = rs.getInt("totalSesiones");
        a.setTotalSesiones(rs.wasNull() ? null : total);

        double punt = rs.getDouble("puntuacionPromedio");
        a.setPuntuacionPromedio(rs.wasNull() ? null : punt);

        int minutos = rs.getInt("minutosTotales");
        a.setMinutosTotales(rs.wasNull() ? null : minutos);
        return a;
    }
}
