package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Rol;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDAOJdbc {
    private static RolDAOJdbc instancia;

    private RolDAOJdbc() {}

    public static RolDAOJdbc getInstancia() {
        if (instancia == null) instancia = new RolDAOJdbc();
        return instancia;
    }

    public List<Rol> listar() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT codigo, descripcion FROM Rol ORDER BY codigo";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Rol r = new Rol();
                r.setCodigo(rs.getInt("codigo"));
                r.setDescripcion(rs.getString("descripcion"));
                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
