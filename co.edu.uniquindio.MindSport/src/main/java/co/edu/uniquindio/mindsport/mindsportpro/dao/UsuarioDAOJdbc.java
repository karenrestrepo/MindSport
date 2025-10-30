package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Usuario;
import co.edu.uniquindio.mindsport.mindsportpro.model.Atleta;
import co.edu.uniquindio.mindsport.mindsportpro.model.Coach;
import co.edu.uniquindio.mindsport.mindsportpro.enums.Rol;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOJdbc {

    private static UsuarioDAOJdbc instancia;

    private UsuarioDAOJdbc() {}

    public static UsuarioDAOJdbc getInstancia() {
        if (instancia == null) instancia = new UsuarioDAOJdbc();
        return instancia;
    }

    /** Convertir ResultSet (fila de Usuario) a objeto Usuario (Atleta/Coach si aplica). */
    private Usuario mapRowToUsuario(ResultSet rs) throws SQLException {
        String cedula = rs.getString("cedula");
        String nombres = rs.getString("nombres");
        String apellidos = rs.getString("apellidos");
        String correo = rs.getString("correo");
        String generoStr = rs.getString("genero");
        String contrasena = rs.getString("contrasena");
        String rolStr = rs.getString("rol");

        // Instanciar según rol (si rol es nulo o desconocido, instanciamos Usuario genérico si existe clase)
        Rol rol = null;
        if (rolStr != null) {
            try { rol = Rol.valueOf(rolStr); } catch (Exception ignored) {}
        }

        Usuario u;
        if (rol == Rol.ATLETA) {
            Atleta a = new Atleta();
            a.setCedula(cedula);
            a.setNombres(nombres);
            a.setApellidos(apellidos);
            a.setCorreo(correo);
            a.setContrasena(contrasena);
            // obtener campos de atleta
            // Haremos otra consulta para obtener datos propios (peso, altura, perfil, fecha_nacimiento)
            u = a;
        } else if (rol == Rol.COACH) {
            Coach c = new Coach();
            c.setCedula(cedula);
            c.setNombres(nombres);
            c.setApellidos(apellidos);
            c.setCorreo(correo);
            c.setContrasena(contrasena);
            u = c;
        } else {
            // Si tu clase Usuario es abstracta y no puedes instanciarla, devolver null o una subclase por defecto.
            // Supondré que tienes una clase Usuario concreta (o puedes devolver Atleta por defecto).
            Atleta a = new Atleta(); // fallback
            a.setCedula(cedula);
            a.setNombres(nombres);
            a.setApellidos(apellidos);
            a.setCorreo(correo);
            a.setContrasena(contrasena);
            u = a;
        }

        // genero y rol si existen setters
        try { java.lang.reflect.Method m = u.getClass().getMethod("setGenero", co.edu.uniquindio.mindsport.mindsportpro.enums.Genero.class);
            if (generoStr != null) {
                co.edu.uniquindio.mindsport.mindsportpro.enums.Genero gen = co.edu.uniquindio.mindsport.mindsportpro.enums.Genero.valueOf(generoStr);
                m.invoke(u, gen);
            }
        } catch (Exception ignored) {}

        try { java.lang.reflect.Method mr = u.getClass().getMethod("setRol", Rol.class); mr.invoke(u, rol); } catch (Exception ignored) {}

        return u;
    }

    /** LISTAR todos los usuarios (y mapear hijos y telefonos). */
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT cedula, nombres, apellidos, correo, genero, contrasena, rol FROM Usuario";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = mapRowToUsuario(rs);
                if (u != null) {
                    // rellenar detalles de hijo y teléfonos
                    cargarCamposHijoYTelefonos(u);
                    lista.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** Crear usuario + hijo (Atleta/Coach) + telefonos en una transacción */
    public Usuario crear(Usuario u) {
        String insertUsuario = "INSERT INTO Usuario (cedula, nombres, apellidos, correo, genero, contrasena, rol) VALUES (?,?,?,?,?,?,?)";
        try (Connection cn = DBUtil.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement ps = cn.prepareStatement(insertUsuario)) {
                ps.setString(1, u.getCedula());
                ps.setString(2, u.getNombres());
                ps.setString(3, u.getApellidos());
                ps.setString(4, u.getCorreo());
                // genero y rol pueden ser null
                String genero = null;
                try {
                    Object g = u.getClass().getMethod("getGenero").invoke(u);
                    genero = (g != null) ? g.toString() : null;
                } catch (Exception ignored) {}
                ps.setString(5, genero);
                ps.setString(6, u.getContrasena());
                String rol = null;
                try {
                    Object r = u.getClass().getMethod("getRol").invoke(u);
                    rol = (r != null) ? r.toString() : null;
                } catch (Exception ignored) {}
                ps.setString(7, rol);

                ps.executeUpdate();
            }

            // insertar fila en tabla hija según rol
            if (u instanceof Atleta) {
                Atleta a = (Atleta) u;
                String sqlA = "INSERT INTO Atleta (cedula, perfil_deportivo, peso, altura, fecha_nacimiento) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlA)) {
                    ps.setString(1, a.getCedula());
                    ps.setString(2, a.getPerfilDeportivo());
                    if (a.getPeso() != null) ps.setDouble(3, a.getPeso()); else ps.setNull(3, Types.DOUBLE);
                    if (a.getAltura() != null) ps.setDouble(4, a.getAltura()); else ps.setNull(4, Types.DOUBLE);
                    if (a.getFechaNacimiento() != null) ps.setDate(5, Date.valueOf(a.getFechaNacimiento())); else ps.setNull(5, Types.DATE);
                    ps.executeUpdate();
                }
            } else if (u instanceof Coach) {
                Coach c = (Coach) u;
                String sqlC = "INSERT INTO Coach (cedula, id_profesional, especialidad, centro_trabajo, disponibilidad) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlC)) {
                    ps.setString(1, c.getCedula());
                    ps.setString(2, c.getIdProfesional());
                    ps.setString(3, c.getEspecialidad());
                    ps.setString(4, c.getCentroTrabajo());
                    ps.setString(5, c.getDisponibilidad());
                    ps.executeUpdate();
                }
            }

            // telefonos (lista)
            try {
                Object tObj = u.getClass().getMethod("getTelefonos").invoke(u);
                if (tObj instanceof List) {
                    List<?> telefonos = (List<?>) tObj;
                    String insTel = "INSERT INTO TelefonoUsuario (cedula, numero) VALUES (?,?)";
                    try (PreparedStatement ps = cn.prepareStatement(insTel)) {
                        for (Object tel : telefonos) {
                            if (tel != null) {
                                ps.setString(1, u.getCedula());
                                ps.setString(2, tel.toString());
                                ps.addBatch();
                            }
                        }
                        ps.executeBatch();
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}

            cn.commit();
            cn.setAutoCommit(true);
            return u;
        } catch (SQLException e) {
            e.printStackTrace();
            // si falla, intenta rollback automático por try-with-resources al cerrar si no commit
            return null;
        }
    }

    /** Actualizar usuario + hijo + telefonos (transacción). */
    public boolean actualizar(Usuario u) {
        String updUsuario = "UPDATE Usuario SET nombres=?, apellidos=?, correo=?, genero=?, contrasena=?, rol=? WHERE cedula=?";
        try (Connection cn = DBUtil.getConnection()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(updUsuario)) {
                ps.setString(1, u.getNombres());
                ps.setString(2, u.getApellidos());
                ps.setString(3, u.getCorreo());
                String genero = null;
                try { Object g = u.getClass().getMethod("getGenero").invoke(u); genero = (g!=null)?g.toString():null; } catch (Exception ignored) {}
                ps.setString(4, genero);
                ps.setString(5, u.getContrasena());
                String rol = null;
                try { Object r = u.getClass().getMethod("getRol").invoke(u); rol = (r!=null)?r.toString():null; } catch (Exception ignored) {}
                ps.setString(6, rol);
                ps.setString(7, u.getCedula());
                ps.executeUpdate();
            }

            // actualizar tablas hijas: lo más simple es eliminar la fila hija (si existe) y volver a insertar según instancia
            try (PreparedStatement delA = cn.prepareStatement("DELETE FROM Atleta WHERE cedula = ?");
                 PreparedStatement delC = cn.prepareStatement("DELETE FROM Coach WHERE cedula = ?")) {
                delA.setString(1, u.getCedula()); delA.executeUpdate();
                delC.setString(1, u.getCedula()); delC.executeUpdate();
            }

            if (u instanceof Atleta) {
                Atleta a = (Atleta) u;
                String sqlA = "INSERT INTO Atleta (cedula, perfil_deportivo, peso, altura, fecha_nacimiento) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlA)) {
                    ps.setString(1, a.getCedula());
                    ps.setString(2, a.getPerfilDeportivo());
                    if (a.getPeso() != null) ps.setDouble(3, a.getPeso()); else ps.setNull(3, Types.DOUBLE);
                    if (a.getAltura() != null) ps.setDouble(4, a.getAltura()); else ps.setNull(4, Types.DOUBLE);
                    if (a.getFechaNacimiento() != null) ps.setDate(5, Date.valueOf(a.getFechaNacimiento())); else ps.setNull(5, Types.DATE);
                    ps.executeUpdate();
                }
            } else if (u instanceof Coach) {
                Coach c = (Coach) u;
                String sqlC = "INSERT INTO Coach (cedula, id_profesional, especialidad, centro_trabajo, disponibilidad) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlC)) {
                    ps.setString(1, c.getCedula());
                    ps.setString(2, c.getIdProfesional());
                    ps.setString(3, c.getEspecialidad());
                    ps.setString(4, c.getCentroTrabajo());
                    ps.setString(5, c.getDisponibilidad());
                    ps.executeUpdate();
                }
            }

            // telefonos: elimina todos y re-inserta
            try (PreparedStatement delTel = cn.prepareStatement("DELETE FROM TelefonoUsuario WHERE cedula = ?")) {
                delTel.setString(1, u.getCedula());
                delTel.executeUpdate();
            }
            try {
                Object tObj = u.getClass().getMethod("getTelefonos").invoke(u);
                if (tObj instanceof List) {
                    List<?> telefonos = (List<?>) tObj;
                    String insTel = "INSERT INTO TelefonoUsuario (cedula, numero) VALUES (?,?)";
                    try (PreparedStatement ps = cn.prepareStatement(insTel)) {
                        for (Object tel : telefonos) {
                            if (tel != null) {
                                ps.setString(1, u.getCedula());
                                ps.setString(2, tel.toString());
                                ps.addBatch();
                            }
                        }
                        ps.executeBatch();
                    }
                }
            } catch (NoSuchMethodException ignored) {} catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            cn.commit();
            cn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Eliminar por objeto (usa cedula). */
    public boolean eliminar(Usuario u) {
        return eliminarPorCedula(u.getCedula());
    }

    /** Eliminar por cedula (borra registro, cascade en Atleta/Coach si DB lo tiene). */
    public boolean eliminarPorCedula(String cedula) {
        String sql = "DELETE FROM Usuario WHERE cedula = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Borrar todo (útil solo en desarrollo). */
    public void borrarTodo() {
        try (Connection cn = DBUtil.getConnection();
             Statement st = cn.createStatement()) {
            st.executeUpdate("DELETE FROM TelefonoUsuario");
            st.executeUpdate("DELETE FROM Atleta");
            st.executeUpdate("DELETE FROM Coach");
            st.executeUpdate("DELETE FROM Usuario");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Buscar por cédula */
    public Optional<Usuario> buscarPorCedula(String cedula) {
        String sql = "SELECT cedula, nombres, apellidos, correo, genero, contrasena, rol FROM Usuario WHERE cedula = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = mapRowToUsuario(rs);
                    cargarCamposHijoYTelefonos(u);
                    return Optional.ofNullable(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /** Carga campos de tabla hija y telefonos en el objeto Usuario pasado */
    private void cargarCamposHijoYTelefonos(Usuario u) {
        if (u == null) return;
        String cedula = u.getCedula();
        try (Connection cn = DBUtil.getConnection()) {
            // comprobar si es Atleta
            try (PreparedStatement ps = cn.prepareStatement("SELECT perfil_deportivo, peso, altura, fecha_nacimiento FROM Atleta WHERE cedula = ?")) {
                ps.setString(1, cedula);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && u instanceof Atleta) {
                        Atleta a = (Atleta) u;
                        a.setPerfilDeportivo(rs.getString("perfil_deportivo"));
                        double peso = rs.getDouble("peso");
                        if (!rs.wasNull()) a.setPeso(peso);
                        double altura = rs.getDouble("altura");
                        if (!rs.wasNull()) a.setAltura(altura);
                        Date fn = rs.getDate("fecha_nacimiento");
                        if (fn != null) a.setFechaNacimiento(fn.toLocalDate());
                    }
                }
            }
            // comprobar si es Coach
            try (PreparedStatement ps = cn.prepareStatement("SELECT id_profesional, especialidad, centro_trabajo, disponibilidad FROM Coach WHERE cedula = ?")) {
                ps.setString(1, cedula);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && u instanceof Coach) {
                        Coach c = (Coach) u;
                        c.setIdProfesional(rs.getString("id_profesional"));
                        c.setEspecialidad(rs.getString("especialidad"));
                        c.setCentroTrabajo(rs.getString("centro_trabajo"));
                        c.setDisponibilidad(rs.getString("disponibilidad"));
                    }
                }
            }

            // telefonos
            try (PreparedStatement ps = cn.prepareStatement("SELECT numero FROM TelefonoUsuario WHERE cedula = ?")) {
                ps.setString(1, cedula);
                try (ResultSet rs = ps.executeQuery()) {
                    List<String> telefonos = new ArrayList<>();
                    while (rs.next()) telefonos.add(rs.getString("numero"));
                    try { java.lang.reflect.Method m = u.getClass().getMethod("setTelefonos", List.class); m.invoke(u, telefonos); } catch (Exception ignored) {}
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

