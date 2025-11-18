package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.PlanInfrautilizado;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlanInfrautilizadoDAOJdbc {

    private static PlanInfrautilizadoDAOJdbc instancia;

    private PlanInfrautilizadoDAOJdbc() {}

    public static PlanInfrautilizadoDAOJdbc getInstancia() {
        if (instancia == null) instancia = new PlanInfrautilizadoDAOJdbc();
        return instancia;
    }

    private static final String SQL = ""
            + "SELECT "
            + "    p.idPlan AS idPlan, "
            + "    p.nombre AS nombrePlan, "
            + "    (SELECT COUNT(*) FROM Inscripcion i WHERE i.idPlan = p.idPlan) AS totalInscripciones, "
            + "    (SELECT COUNT(DISTINCT i.cedulaAtleta) FROM Inscripcion i WHERE i.idPlan = p.idPlan AND EXISTS (SELECT 1 FROM Sesion s WHERE s.cedulaAtleta = i.cedulaAtleta)) AS atletasConSesiones, "
            + "    (SELECT COUNT(DISTINCT i.cedulaAtleta) FROM Inscripcion i WHERE i.idPlan = p.idPlan AND NOT EXISTS (SELECT 1 FROM Sesion s WHERE s.cedulaAtleta = i.cedulaAtleta)) AS atletasSinSesiones, "
            + "    ((SELECT COUNT(DISTINCT i.cedulaAtleta) FROM Inscripcion i WHERE i.idPlan = p.idPlan AND NOT EXISTS (SELECT 1 FROM Sesion s WHERE s.cedulaAtleta = i.cedulaAtleta)) / NULLIF((SELECT COUNT(DISTINCT i2.cedulaAtleta) FROM Inscripcion i2 WHERE i2.idPlan = p.idPlan), 0) * 100) AS porcentajeSinSesiones "
            + "FROM Plan p "
            + "WHERE (SELECT COUNT(*) FROM Inscripcion i WHERE i.idPlan = p.idPlan) > 0 "
            + "ORDER BY porcentajeSinSesiones DESC, totalInscripciones DESC";

    public List<PlanInfrautilizado> listar() {
        List<PlanInfrautilizado> lista = new ArrayList<>();
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            System.out.println("[PlanInfrautilizadoDAO] Registros cargados: " + lista.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private PlanInfrautilizado mapRow(ResultSet rs) throws SQLException {
        PlanInfrautilizado p = new PlanInfrautilizado();
        p.setIdPlan(rs.getInt("idPlan"));
        p.setNombrePlan(rs.getString("nombrePlan"));

        int total = rs.getInt("totalInscripciones");
        p.setTotalInscripciones(rs.wasNull() ? null : total);

        int conSesiones = rs.getInt("atletasConSesiones");
        p.setAtletasConSesiones(rs.wasNull() ? null : conSesiones);

        int sinSesiones = rs.getInt("atletasSinSesiones");
        p.setAtletasSinSesiones(rs.wasNull() ? null : sinSesiones);

        double porcentaje = rs.getDouble("porcentajeSinSesiones");
        p.setPorcentajeSinSesiones(rs.wasNull() ? null : porcentaje);
        return p;
    }
}
