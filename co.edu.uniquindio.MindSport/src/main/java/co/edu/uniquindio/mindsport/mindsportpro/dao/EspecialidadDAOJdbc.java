package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Especialidad;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadDAOJdbc {

    public List<Especialidad> listar() {
        List<Especialidad> lista = new ArrayList<>();
        String sql = "SELECT idEspecialidad, codigo, descripcion FROM Especialidad ORDER BY descripcion";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Especialidad e = new Especialidad(
                        rs.getInt("idEspecialidad"),
                        rs.getString("codigo"),
                        rs.getString("descripcion")
                );
                lista.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
