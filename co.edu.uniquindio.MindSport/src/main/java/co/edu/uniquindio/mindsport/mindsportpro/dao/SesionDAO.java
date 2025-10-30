package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Sesion;

import java.util.*;

public class SesionDAO {

    private final Map<Integer, Sesion> almacenamiento = new LinkedHashMap<>();
    private int seq = 1;

    public SesionDAO() { }

    public Sesion crear(Sesion s) {
        if (s == null) return null;
        s.setId(seq++);
        almacenamiento.put(s.getId(), s);
        return s;
    }

    public List<Sesion> listar() {
        return new ArrayList<>(almacenamiento.values());
    }

    public Optional<Sesion> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(almacenamiento.get(id));
    }

    public boolean actualizar(Sesion s) {
        if (s == null || s.getId() == null) return false;
        if (!almacenamiento.containsKey(s.getId())) return false;
        almacenamiento.put(s.getId(), s);
        return true;
    }

    public boolean eliminar(Sesion s) {
        if (s == null || s.getId() == null) return false;
        return almacenamiento.remove(s.getId()) != null;
    }

    public List<Sesion> filtrar(String texto) {
        if (texto == null || texto.trim().isEmpty()) return listar();
        String f = texto.trim().toLowerCase();
        List<Sesion> res = new ArrayList<>();
        for (Sesion s : almacenamiento.values()) {
            if (s.getCedulaAtleta() != null && s.getCedulaAtleta().toLowerCase().contains(f)) res.add(s);
            else if (s.getRutinaId() != null && String.valueOf(s.getRutinaId()).contains(f)) res.add(s);
            else if (s.getFecha() != null && s.getFecha().toString().contains(f)) res.add(s);
            else if (s.getObservacionCoach() != null && s.getObservacionCoach().toLowerCase().contains(f)) res.add(s);
        }
        return res;
    }

    public void limpiar() {
        almacenamiento.clear();
        seq = 1;
    }
}
