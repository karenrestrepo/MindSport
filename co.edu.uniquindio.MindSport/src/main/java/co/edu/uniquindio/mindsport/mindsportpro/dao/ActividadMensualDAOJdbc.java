package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.ActividadMensual;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActividadMensualDAOJdbc {

    private static ActividadMensualDAOJdbc instancia;

    private ActividadMensualDAOJdbc() {}

    public static ActividadMensualDAOJdbc getInstancia() {
        if (instancia == null) instancia = new ActividadMensualDAOJdbc();
        return instancia;
    }

    private static final String SQL = ""
            + "SELECT "
            + "    YEAR(s.fecha) AS anio, "
            + "    MONTH(s.fecha) AS mesNumero, "
            + "    MONTHNAME(s.fecha) AS mesNombre, "
            + "    DATE_FORMAT(s.fecha, '%Y-%m') AS periodo, "
            + "    COUNT(s.id) AS totalSesiones, "
            + "    COUNT(DISTINCT s.cedulaAtleta) AS atletasActivos, "
            + "    COUNT(DISTINCT s.rutinaId) AS rutinasUtilizadas, "
            + "    AVG(s.puntuacion) AS puntuacionPromedio, "
            + "    AVG(s.duracionReal) AS duracionPromedio, "
            + "    SUM(s.duracionReal) AS tiempoTotal "
            + "FROM Sesion s "
            + "WHERE s.fecha >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) "
            + "GROUP BY YEAR(s.fecha), MONTH(s.fecha), MONTHNAME(s.fecha), DATE_FORMAT(s.fecha, '%Y-%m') "
            + "ORDER BY YEAR(s.fecha) DESC, MONTH(s.fecha) DESC";

    public List<ActividadMensual> listar() {
        List<ActividadMensual> lista = new ArrayList<>();
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            System.out.println("[ActividadMensualDAO] Registros cargados: " + lista.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private ActividadMensual mapRow(ResultSet rs) throws SQLException {
        ActividadMensual a = new ActividadMensual();
        a.setAno(rs.getInt("anio"));
        a.setMesNumero(rs.getInt("mesNumero"));
        a.setMesNombre(rs.getString("mesNombre"));
        a.setPeriodo(rs.getString("periodo"));
        int total = rs.getInt("totalSesiones");
        a.setTotalSesiones(rs.wasNull() ? null : total);
        int act = rs.getInt("atletasActivos");
        a.setAtletasActivos(rs.wasNull() ? null : act);
        int rut = rs.getInt("rutinasUtilizadas");
        a.setRutinasUtilizadas(rs.wasNull() ? null : rut);
        double punt = rs.getDouble("puntuacionPromedio");
        a.setPuntuacionPromedio(rs.wasNull() ? null : punt);
        double durProm = rs.getDouble("duracionPromedio");
        a.setDuracionPromedioMin(rs.wasNull() ? null : durProm);
        int tot = rs.getInt("tiempoTotal");
        a.setTiempoTotalMin(rs.wasNull() ? null : tot);
        return a;
    }
}
