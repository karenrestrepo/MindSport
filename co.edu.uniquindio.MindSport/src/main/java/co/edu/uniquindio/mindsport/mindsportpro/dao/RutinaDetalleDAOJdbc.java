package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.RutinaDetalle;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RutinaDetalleDAOJdbc {

    private static RutinaDetalleDAOJdbc instancia;

    private RutinaDetalleDAOJdbc() {}

    public static RutinaDetalleDAOJdbc getInstancia() {
        if (instancia == null) instancia = new RutinaDetalleDAOJdbc();
        return instancia;
    }

    private static final String SQL = ""
            + "SELECT "
            + "    r.id AS idRutina, "
            + "    r.titulo AS tituloRutina, "
            + "    r.nivelDificultad AS nivelDificultad, "
            + "    r.duracionEstimada AS duracionEstimada, "
            + "    u.nombres AS coachNombres, "
            + "    u.apellidos AS coachApellidos, "
            + "    COUNT(re.idEjercicio) AS numEjercicios, "
            + "    SUM(e.duracion) AS duracionTotalEjercicios, "
            + "    AVG(e.duracion) AS duracionPromedioEjercicio, "
            + "    SUM(CASE WHEN e.tipoEjercicio = 'FISICO_TRADICIONAL' THEN 1 ELSE 0 END) AS ejerciciosFisicos, "
            + "    SUM(CASE WHEN e.tipoEjercicio = 'MENTAL_EMOCIONAL' THEN 1 ELSE 0 END) AS ejerciciosMentales, "
            + "    SUM(CASE WHEN e.tipoEjercicio = 'MIXTO' THEN 1 ELSE 0 END) AS ejerciciosMixtos, "
            + "    GROUP_CONCAT(DISTINCT e.faseUso ORDER BY e.faseUso SEPARATOR ', ') AS fasesIncluidas "
            + "FROM Rutina r "
            + "INNER JOIN Usuario u ON r.cedulaCoach = u.cedula "
            + "INNER JOIN RutinaEjercicio re ON r.id = re.idRutina "
            + "INNER JOIN Ejercicio e ON re.idEjercicio = e.id "
            + "GROUP BY r.id, r.titulo, r.nivelDificultad, r.duracionEstimada, u.nombres, u.apellidos "
            + "HAVING COUNT(re.idEjercicio) > 0 "
            + "ORDER BY COUNT(re.idEjercicio) DESC, SUM(e.duracion) DESC";

    public List<RutinaDetalle> listar() {
        List<RutinaDetalle> lista = new ArrayList<>();
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            System.out.println("[RutinaDetalleDAO] Registros cargados: " + lista.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private RutinaDetalle mapRow(ResultSet rs) throws SQLException {
        RutinaDetalle r = new RutinaDetalle();
        r.setId(rs.getInt("idRutina"));
        r.setTitulo(rs.getString("tituloRutina"));
        r.setNivelDificultadTexto(rs.getString("nivelDificultad"));
        int durEst = rs.getInt("duracionEstimada");
        r.setDuracionEstimada(rs.wasNull() ? null : durEst);
        r.setNombresCoach(rs.getString("coachNombres"));
        r.setApellidosCoach(rs.getString("coachApellidos"));

        int numE = rs.getInt("numEjercicios");
        r.setNumeroEjercicios(rs.wasNull() ? null : numE);
        int durTot = rs.getInt("duracionTotalEjercicios");
        r.setDuracionTotalEjercicios(rs.wasNull() ? null : durTot);
        double durProm = rs.getDouble("duracionPromedioEjercicio");
        r.setDuracionPromedioEjercicio(rs.wasNull() ? null : durProm);
        int fis = rs.getInt("ejerciciosFisicos");
        r.setEjerciciosFisicos(rs.wasNull() ? null : fis);
        int men = rs.getInt("ejerciciosMentales");
        r.setEjerciciosMentales(rs.wasNull() ? null : men);
        int mix = rs.getInt("ejerciciosMixtos");
        r.setEjerciciosMixtos(rs.wasNull() ? null : mix);
        r.setFasesIncluidas(rs.getString("fasesIncluidas"));
        return r;
    }
}
