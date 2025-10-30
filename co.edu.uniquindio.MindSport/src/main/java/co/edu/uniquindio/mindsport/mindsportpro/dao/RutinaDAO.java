package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Rutina;

import java.util.*;

public class RutinaDAO {

    private static RutinaDAO instancia;
    private final Map<Integer, Rutina> almacenamiento = new LinkedHashMap<>();
    private int seq = 1;

    public RutinaDAO() { }

    public static RutinaDAO getInstancia() {
        if (instancia == null) {
            instancia = new RutinaDAO();
        }
        return instancia;
    }

    public Rutina crear(Rutina rutina) {
        if (rutina == null) return null;
        rutina.setId(seq++);
        almacenamiento.put(rutina.getId(), rutina);
        return rutina;
    }

    public List<Rutina> listar() {
        return new ArrayList<>(almacenamiento.values());
    }

    public Optional<Rutina> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(almacenamiento.get(id));
    }

    public boolean actualizar(Rutina rutina) {
        if (rutina == null || rutina.getId() == null) return false;
        Integer id = rutina.getId();
        if (!almacenamiento.containsKey(id)) return false;
        almacenamiento.put(id, rutina);
        return true;
    }

    public boolean eliminar(Rutina rutina) {
        if (rutina == null || rutina.getId() == null) return false;
        return almacenamiento.remove(rutina.getId()) != null;
    }

    public boolean eliminarPorId(Integer id) {
        if (id == null) return false;
        return almacenamiento.remove(id) != null;
    }

    public List<Rutina> filtrar(String texto) {
        if (texto == null || texto.trim().isEmpty()) return listar();
        String f = texto.trim().toLowerCase();

        List<Rutina> resultado = new ArrayList<>();
        for (Rutina r : almacenamiento.values()) {
            boolean matched = false;
            if (r.getTitulo() != null && r.getTitulo().toLowerCase().contains(f)) matched = true;
            if (!matched && r.getDescripcion() != null && r.getDescripcion().toLowerCase().contains(f)) matched = true;
            if (!matched && r.getCedulaCoach() != null && r.getCedulaCoach().toLowerCase().contains(f)) matched = true;
            if (matched) resultado.add(r);
        }
        return resultado;
    }

    public void limpiar() {
        almacenamiento.clear();
        seq = 1;
    }
}