package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Ejercicio;
import co.edu.uniquindio.mindsport.mindsportpro.util.DBUtil;

import java.sql.*;
import java.util.*;

public class EjercicioDAO {

    private static EjercicioDAO instancia;

    private final Map<Integer, Ejercicio> almacenamiento = new LinkedHashMap<>();
    private int seq = 1;

    // Constructor privado
    private EjercicioDAO() {
    }

    // Método estático para obtener la única instancia
    public static EjercicioDAO getInstancia() {
        if (instancia == null) {
            instancia = new EjercicioDAO();
        }
        return instancia;
    }

    public Ejercicio crear(Ejercicio ejercicio) {
        if (ejercicio == null) return null;

        String sql = "INSERT INTO Ejercicio (idModulo, titulo, descripcion, tipo, duracionMin, requiereAudio, urlAudio) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Ejecuta la query y Le dice "devuélveme el ID que generes"

            ps.setObject(1, 1);
            ps.setObject(2, ejercicio.getTitulo()); //setObject maneja los null automaticamente
            ps.setObject(3, ejercicio.getDescripcion());
            ps.setObject(4, "Fuerza");
            ps.setObject(5, ejercicio.getDuracion());
            ps.setObject(6, 1);
            ps.setObject(7, "https://music.youtube.com/watch?v=YihT5dlwqdw");
            /*ps.setObject(4, ejercicio.getFaseUso() != null ? ejercicio.getFaseUso().name() : null); //para los enums si o si hay que usar un ternario ya que puede ser null y name no existiria
            ps.setObject(5, ejercicio.getTipoEjercicio() != null ? ejercicio.getTipoEjercicio().name() : null);
            ps.setObject(6, ejercicio.getRutinaId());*/

            ps.executeUpdate();

            // Obtener ID generado
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ejercicio.setId(rs.getInt(1));
            }
            rs.close();

            return ejercicio;

        } catch (SQLException e) {
            System.err.println("Error al crear ejercicio: " + e.getMessage());
            return null;
        }
    }

    public List<Ejercicio> listar() {
        return new ArrayList<>(almacenamiento.values());
    }

    public Optional<Ejercicio> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(almacenamiento.get(id));
    }

    public boolean actualizar(Ejercicio ejercicio) {
        if (ejercicio == null || ejercicio.getId() == null) return false;
        Integer id = ejercicio.getId();
        if (!almacenamiento.containsKey(id)) return false;
        almacenamiento.put(id, ejercicio);
        return true;
    }

    public boolean eliminar(Ejercicio ejercicio) {
        if (ejercicio == null || ejercicio.getId() == null) return false;
        return almacenamiento.remove(ejercicio.getId()) != null;
    }

    public boolean eliminarPorId(Integer id) {
        if (id == null) return false;
        return almacenamiento.remove(id) != null;
    }

    public List<Ejercicio> filtrar(String texto) {
        if (texto == null || texto.trim().isEmpty()) return listar();
        String f = texto.trim().toLowerCase();

        Integer posibleRutinaId = null;
        try { posibleRutinaId = Integer.parseInt(f); } catch (NumberFormatException ignored) {}

        List<Ejercicio> resultado = new ArrayList<>();
        for (Ejercicio e : almacenamiento.values()) {
            boolean matched = false;
            if (e.getTitulo() != null && e.getTitulo().toLowerCase().contains(f)) matched = true;
            if (!matched && e.getDescripcion() != null && e.getDescripcion().toLowerCase().contains(f)) matched = true;
            if (!matched && posibleRutinaId != null && e.getRutinaId() != null && e.getRutinaId().equals(posibleRutinaId)) matched = true;
            if (matched) resultado.add(e);
        }
        return resultado;
    }

    public List<Ejercicio> listarPorRutina(Integer rutinaId) {
        if (rutinaId == null) return Collections.emptyList();
        List<Ejercicio> lista = new ArrayList<>();
        for (Ejercicio e : almacenamiento.values()) {
            if (rutinaId.equals(e.getRutinaId())) lista.add(e);
        }
        return lista;
    }

    public void limpiar() {
        almacenamiento.clear();
        seq = 1;
    }
}