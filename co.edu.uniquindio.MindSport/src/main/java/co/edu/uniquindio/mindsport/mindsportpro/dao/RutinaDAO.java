package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Rutina;

import java.util.*;

public class RutinaDAO {

    private final Map<Integer, Rutina> almacenamiento = new LinkedHashMap<>();
    private int seq = 1;

    public RutinaDAO() {
        // opcional: poblar con datos de ejemplo si quieres
    }

    /**
     * Crea una nueva rutina y le asigna un id automático.
     * @param rutina Rutina a crear
     * @return la rutina creada (con id asignado)
     */
    public Rutina crear(Rutina rutina) {
        if (rutina == null) return null;
        rutina.setId(seq++);
        almacenamiento.put(rutina.getId(), rutina);
        return rutina;
    }

    /**
     * Lista todas las rutinas (orden de inserción).
     * @return lista de rutinas
     */
    public List<Rutina> listar() {
        return new ArrayList<>(almacenamiento.values());
    }

    /**
     * Busca una rutina por su id.
     * @param id identificador
     * @return Optional con la rutina si existe
     */
    public Optional<Rutina> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(almacenamiento.get(id));
    }

    /**
     * Actualiza una rutina existente (busca por id).
     * @param rutina rutina con cambios (debe tener id no nulo)
     * @return true si se actualizó, false si no existía
     */
    public boolean actualizar(Rutina rutina) {
        if (rutina == null || rutina.getId() == null) return false;
        Integer id = rutina.getId();
        if (!almacenamiento.containsKey(id)) return false;
        almacenamiento.put(id, rutina);
        return true;
    }

    /**
     * Elimina la rutina (por objeto).
     * @param rutina rutina a eliminar
     * @return true si se eliminó
     */
    public boolean eliminar(Rutina rutina) {
        if (rutina == null || rutina.getId() == null) return false;
        return almacenamiento.remove(rutina.getId()) != null;
    }

    /**
     * Elimina por id.
     * @param id id a eliminar
     * @return true si se eliminó
     */
    public boolean eliminarPorId(Integer id) {
        if (id == null) return false;
        return almacenamiento.remove(id) != null;
    }

    /**
     * Filtra rutinas por texto (busca en título y descripción) o por idCoach.
     * Si el texto es null/empty devuelve todas.
     * Si el texto es numérico se comprueba también si coincide con idCoach.
     *
     * @param texto filtro (insensible a mayúsculas)
     * @return lista de rutinas filtradas
     */
    public List<Rutina> filtrar(String texto) {
        if (texto == null || texto.trim().isEmpty()) return listar();
        String f = texto.trim().toLowerCase();

        // intentar parsear a entero para buscar por idCoach
        Integer posibleCoachId = null;
        try { posibleCoachId = Integer.parseInt(f); } catch (NumberFormatException ignored) {}

        List<Rutina> resultado = new ArrayList<>();
        for (Rutina r : almacenamiento.values()) {
            boolean matched = false;
            if (r.getTitulo() != null && r.getTitulo().toLowerCase().contains(f)) matched = true;
            if (!matched && r.getDescripcion() != null && r.getDescripcion().toLowerCase().contains(f)) matched = true;
            if (!matched && posibleCoachId != null && r.getIdCoach() != null && r.getIdCoach().equals(posibleCoachId)) matched = true;
            if (matched) resultado.add(r);
        }
        return resultado;
    }

    /**
     * Borra todo (útil para pruebas)
     */
    public void limpiar() {
        almacenamiento.clear();
        seq = 1;
    }
}