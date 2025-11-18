package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.enums.TipoPerfil;
import co.edu.uniquindio.mindsport.mindsportpro.model.*;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
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

    private Usuario mapRowToUsuario(ResultSet rs) throws SQLException {
        String cedula = rs.getString("cedula");
        String nombres = rs.getString("nombres");
        String apellidos = rs.getString("apellidos");
        String correo = rs.getString("correo");
        String generoStr = rs.getString("genero");
        String contrasena = rs.getString("contrasena");
        Integer rolId = rs.getInt("rol");
        if (rs.wasNull()) rolId = null;

        Usuario u;
        if (rolId != null && rolId == 1) {  // ATLETA
            Atleta a = new Atleta();
            a.setCedula(cedula);
            a.setNombres(nombres);
            a.setApellidos(apellidos);
            a.setCorreo(correo);
            a.setContrasena(contrasena);

            // ✅ CARGAR DATOS ESPECÍFICOS DE ATLETA
            int idPerfil = rs.getInt("perfil_deportivo");
            if (!rs.wasNull()) {
                a.setTipoPerfil(TipoPerfil.fromId(idPerfil));
            }

            double peso = rs.getDouble("peso");
            if (!rs.wasNull()) a.setPeso(peso);

            double altura = rs.getDouble("altura");
            if (!rs.wasNull()) a.setAltura(altura);

            Date fechaNac = rs.getDate("fecha_nacimiento");
            if (fechaNac != null) a.setFechaNacimiento(fechaNac.toLocalDate());

            u = a;

        } else if (rolId != null && rolId == 2) {  // COACH
            Coach c = new Coach();
            c.setCedula(cedula);
            c.setNombres(nombres);
            c.setApellidos(apellidos);
            c.setCorreo(correo);
            c.setContrasena(contrasena);

            // ✅ CARGAR DATOS ESPECÍFICOS DE COACH
            c.setIdProfesional(rs.getString("id_profesional"));
            int idEsp = rs.getInt("especialidad");
            int idCentro = rs.getInt("centro_trabajo");

            if (!rs.wasNull()) {
                Especialidad esp = new Especialidad();
                esp.setIdEspecialidad(idEsp);
                // opcional: si tu consulta trae más columnas puedes setear código y descripción
                c.setEspecialidad(esp);
            }

            if (!rs.wasNull()) {
                CentroTrabajo centro = new CentroTrabajo();
                centro.setIdCentro(idCentro);
                // opcional: igual, podrías setear nombre/ciudad si los incluyes en el SELECT
                c.setCentroTrabajo(centro);
            }
            c.setDisponibilidad(rs.getString("disponibilidad"));

            u = c;

        } else {
            // fallback a Atleta
            Atleta a = new Atleta();
            a.setCedula(cedula);
            a.setNombres(nombres);
            a.setApellidos(apellidos);
            a.setCorreo(correo);
            a.setContrasena(contrasena);
            u = a;
        }

        // Configurar género
        try {
            java.lang.reflect.Method m = u.getClass().getMethod("setGenero", co.edu.uniquindio.mindsport.mindsportpro.enums.Genero.class);
            if (generoStr != null) {
                co.edu.uniquindio.mindsport.mindsportpro.enums.Genero gen = co.edu.uniquindio.mindsport.mindsportpro.enums.Genero.valueOf(generoStr);
                m.invoke(u, gen);
            }
        } catch (Exception ignored) {}

        // Configurar rol
        u.setRol(rolId);

        return u;
    }

    /** LISTAR todos los usuarios (y mapear hijos y telefonos). */
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT " +
                "u.cedula, u.nombres, u.apellidos, u.correo, u.genero, u.contrasena, u.rol, " +
                "a.perfil_deportivo, a.peso, a.altura, a.fecha_nacimiento, " +
                "c.id_profesional, c.especialidad, c.centro_Trabajo, c.disponibilidad " +
                "FROM Usuario u " +
                "LEFT JOIN Atleta a ON u.cedula = a.cedula " +
                "LEFT JOIN Coach c ON u.cedula = c.cedula " +
                "ORDER BY u.cedula";

        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = mapRowToUsuario(rs);
                if (u != null) {
                    // Solo cargar teléfonos
                    cargarTelefonos(cn, u);
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
                Integer rol = u.getRol();
                if (rol != null) {
                    ps.setInt(7, rol);
                } else {
                    ps.setNull(7, Types.INTEGER);
                }

                ps.executeUpdate();
            }

            // insertar fila en tabla hija según rol
            if (u instanceof Atleta) {
                Atleta a = (Atleta) u;
                String sqlA = "INSERT INTO Atleta (cedula, perfil_deportivo, peso, altura, fecha_nacimiento) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlA)) {
                    ps.setString(1, a.getCedula());
                    if (a.getTipoPerfil() != null)
                        ps.setInt(2, a.getTipoPerfil().getId());
                    else
                        ps.setNull(2, Types.INTEGER);
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
                    if (c.getEspecialidad() != null)
                        ps.setInt(3, c.getEspecialidad().getIdEspecialidad());
                    else
                        ps.setNull(3, Types.INTEGER);

                    if (c.getCentroTrabajo() != null)
                        ps.setInt(4, c.getCentroTrabajo().getIdCentro());
                    else
                        ps.setNull(4, Types.INTEGER);
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
                Integer rol = u.getRol();
                if (rol != null) {
                    ps.setInt(6, rol);
                } else {
                    ps.setNull(6, Types.INTEGER);
                }
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
                    if (a.getTipoPerfil() != null) ps.setInt(2, a.getTipoPerfil().getId());
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
                    if (c.getEspecialidad() != null)
                        ps.setInt(3, c.getEspecialidad().getIdEspecialidad());
                    else
                        ps.setNull(3, Types.INTEGER);

                    if (c.getCentroTrabajo() != null)
                        ps.setInt(4, c.getCentroTrabajo().getIdCentro());
                    else
                        ps.setNull(4, Types.INTEGER);
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

    /** Buscar por cédula */
    public Optional<Usuario> buscarPorCedula(String cedula) {
        String sql = "SELECT " +
                "u.cedula, u.nombres, u.apellidos, u.correo, u.genero, u.contrasena, u.rol, " +
                "a.perfil_deportivo, a.peso, a.altura, a.fecha_nacimiento, " +
                "c.id_profesional, c.especialidad, c.centro_Trabajo, c.disponibilidad " +
                "FROM Usuario u " +
                "LEFT JOIN Atleta a ON u.cedula = a.cedula " +
                "LEFT JOIN Coach c ON u.cedula = c.cedula " +
                "WHERE u.cedula = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = mapRowToUsuario(rs);
                    cargarTelefonos(cn, u);  // ← CAMBIAR: solo teléfonos
                    return Optional.ofNullable(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void cargarTelefonos(Connection cn, Usuario u) {
        if (u == null) return;

        try (PreparedStatement ps = cn.prepareStatement("SELECT numero FROM TelefonoUsuario WHERE cedula = ?")) {
            ps.setString(1, u.getCedula());
            try (ResultSet rs = ps.executeQuery()) {
                List<String> telefonos = new ArrayList<>();
                while (rs.next()) {
                    telefonos.add(rs.getString("numero"));
                }
                u.setTelefonos(telefonos);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Autentica un usuario por cédula y contraseña
     * @param cedula cédula del usuario
     * @param contrasena contraseña en texto plano
     * @return Optional con el usuario si las credenciales son correctas
     */
    public Optional<Usuario> autenticar(String cedula, String contrasena) {
        if (cedula == null || contrasena == null) {
            return Optional.empty();
        }
        
        Optional<Usuario> usuarioOpt = buscarPorCedula(cedula);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Validar contraseña en texto plano
            if (contrasena.equals(usuario.getContrasena())) {
                return usuarioOpt;
            }
        }
        
        return Optional.empty();
    }

}

