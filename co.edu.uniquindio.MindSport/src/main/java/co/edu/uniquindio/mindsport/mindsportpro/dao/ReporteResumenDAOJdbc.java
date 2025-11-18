package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.enums.TipoPerfil;
import co.edu.uniquindio.mindsport.mindsportpro.model.ReporteResumen;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ReporteResumenDAOJdbc {

    private static ReporteResumenDAOJdbc instancia;

    private ReporteResumenDAOJdbc() {}

    public static ReporteResumenDAOJdbc getInstancia() {
        if (instancia == null) instancia = new ReporteResumenDAOJdbc();
        return instancia;
    }

    private static final String SQL_REPORTE = ""
            + "SELECT "
            + "    u.cedula AS cedula, "
            + "    u.nombres AS nombres, "
            + "    u.apellidos AS apellidos, "
            + "    p.tipoPerfil AS perfilDeportivo, "
            + "    COUNT(s.id) AS totalSesiones, "
            + "    AVG(s.puntuacion) AS puntuacionPromedio, "
            + "    AVG(s.duracionReal) AS duracionPromedioMin, "
            + "    SUM(s.duracionReal) AS tiempoTotalMin, "
            + "    MIN(s.fecha) AS primeraSesion, "
            + "    MAX(s.fecha) AS ultimaSesion, "
            + "    DATEDIFF(MAX(s.fecha), MIN(s.fecha)) AS diasEntrenamiento "
            + "FROM Usuario u "
            + "INNER JOIN Atleta a ON u.cedula = a.cedula "
            + "INNER JOIN PerfilDeportivo p ON a.perfil_deportivo = p.idPerfilAtleta "
            + "LEFT JOIN Sesion s ON a.cedula = s.cedulaAtleta "
            + "WHERE u.cedula = ? "
            + "GROUP BY u.cedula, u.nombres, u.apellidos, p.tipoPerfil "
            + "HAVING COUNT(s.id) > 0";

    public Optional<ReporteResumen> obtenerResumenPorAtleta(String cedula) {
        if (cedula == null || cedula.isBlank()) return Optional.empty();

        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_REPORTE)) {

            ps.setString(1, cedula);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[ReporteResumenDAO] Resultado encontrado para cedula " + cedula);
                    return Optional.of(mapRow(rs));
                }
                System.out.println("[ReporteResumenDAO] Sin resultados para cedula " + cedula);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private ReporteResumen mapRow(ResultSet rs) throws SQLException {
        ReporteResumen r = new ReporteResumen();

        r.setCedula(rs.getString("cedula"));
        r.setNombres(rs.getString("nombres"));
        r.setApellidos(rs.getString("apellidos"));

        String perfilTxt = rs.getString("perfilDeportivo");
        r.setPerfilDeportivoTexto(perfilTxt);
        if (perfilTxt != null) {
            for (TipoPerfil tp : TipoPerfil.values()) {
                if (tp.toString().equalsIgnoreCase(perfilTxt) || tp.getTipoPerfil().equalsIgnoreCase(perfilTxt)) {
                    r.setTipoPerfil(tp);
                    break;
                }
            }
        }

        int totalSesiones = rs.getInt("totalSesiones");
        r.setTotalSesiones(rs.wasNull() ? null : totalSesiones);

        double puntaje = rs.getDouble("puntuacionPromedio");
        r.setPuntuacionPromedio(rs.wasNull() ? null : puntaje);

        double duracionPromedio = rs.getDouble("duracionPromedioMin");
        r.setDuracionPromedioMinutos(rs.wasNull() ? null : duracionPromedio);

        int tiempoTotal = rs.getInt("tiempoTotalMin");
        r.setTiempoTotalEntrenadoMinutos(rs.wasNull() ? null : tiempoTotal);

        Date primera = rs.getDate("primeraSesion");
        if (primera != null) {
            r.setPrimeraSesion(primera.toLocalDate());
        }

        Date ultima = rs.getDate("ultimaSesion");
        if (ultima != null) {
            r.setUltimaSesion(ultima.toLocalDate());
        }

        int dias = rs.getInt("diasEntrenamiento");
        r.setDiasEntrenamiento(rs.wasNull() ? null : dias);

        return r;
    }
}
