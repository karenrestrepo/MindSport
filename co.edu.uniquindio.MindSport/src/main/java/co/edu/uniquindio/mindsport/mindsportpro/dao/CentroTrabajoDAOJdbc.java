package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.CentroTrabajo;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CentroTrabajoDAOJdbc {

    public List<CentroTrabajo> listar() {
        List<CentroTrabajo> lista = new ArrayList<>();
        String sql = "SELECT idCentro, nombre, ciudad FROM CentroTrabajo ORDER BY nombre";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CentroTrabajo c = new CentroTrabajo(
                        rs.getInt("idCentro"),
                        rs.getString("nombre"),
                        rs.getString("ciudad")
                );
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
