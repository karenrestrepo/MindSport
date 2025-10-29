package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Ejercicio;

import java.util.*;

/**
 * DAO en memoria para manejar Ejercicios.
 * Compatible con la clase Ejercicio que enviaste.
 */
public class EjercicioDAO {

    private final Map<Integer, Ejercicio> almacenamiento = new LinkedHashMap<>();
    private int seq = 1;

    public EjercicioDAO() {
        // opcional: inicializar algunos ejercicios de ejemplo
    }

    /**
     * Crea un nuevo ejercicio con id automático.
     */
    public Ejercicio crear(Ejercicio ejercicio) {
        if (ejercicio == null) return null;
        ejercicio.setId(seq++);
        almacenamiento.put(ejercicio.getId(), ejercicio);
        return ejercicio;
    }

    /**
     * Lista todos los ejercicios.
     */
    public List<Ejercicio> listar() {
        return new ArrayList<>(almacenamiento.values());
    }

    /**
     * Busca un ejercicio por id.
     */
    public Optional<Ejercicio> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(almacenamiento.get(id));
    }

    /**
     * Actualiza un ejercicio existente (buscando por id).
     */
    public boolean actualizar(Ejercicio ejercicio) {
        if (ejercicio == null || ejercicio.getId() == null) return false;
        Integer id = ejercicio.getId();
        if (!almacenamiento.containsKey(id)) return false;
        almacenamiento.put(id, ejercicio);
        return true;
    }

    /**
     * Elimina un ejercicio (por objeto).
     */
    public boolean eliminar(Ejercicio ejercicio) {
        if (ejercicio == null || ejercicio.getId() == null) return false;
        return almacenamiento.remove(ejercicio.getId()) != null;
    }

    /**
     * Elimina por id.
     */
    public boolean eliminarPorId(Integer id) {
        if (id == null) return false;
        return almacenamiento.remove(id) != null;
    }

    /**
     * Filtra ejercicios por texto (busca en título o descripción),
     * o por el id de la rutina (si el texto es numérico).
     */
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

    /**
     * Retorna todos los ejercicios asociados a una rutina específica.
     */
    public List<Ejercicio> listarPorRutina(Integer rutinaId) {
        if (rutinaId == null) return Collections.emptyList();
        List<Ejercicio> lista = new ArrayList<>();
        for (Ejercicio e : almacenamiento.values()) {
            if (rutinaId.equals(e.getRutinaId())) lista.add(e);
        }
        return lista;
    }

    /**
     * Limpia toda la base (para pruebas).
     */
    public void limpiar() {
        almacenamiento.clear();
        seq = 1;
    }
}

