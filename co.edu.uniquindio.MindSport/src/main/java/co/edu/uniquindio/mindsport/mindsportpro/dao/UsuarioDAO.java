package co.edu.uniquindio.mindsport.mindsportpro.dao;

import co.edu.uniquindio.mindsport.mindsportpro.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {

    private static UsuarioDAO instancia;

    private final List<Usuario> lista = new ArrayList<>();
    private int seq = 1;

    // Constructor privado para evitar instanciación externa
    private UsuarioDAO() {
        // Opcional: poblar con ejemplos
    }

    // Método estático para obtener la única instancia
    public static UsuarioDAO getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioDAO();
        }
        return instancia;
    }

    public List<Usuario> listar() {
        return new ArrayList<>(lista);
    }

    public Usuario crear(Usuario u) {
        try {
            java.lang.reflect.Method m = u.getClass().getMethod("setId", Integer.class);
            m.invoke(u, seq++);
        } catch (Exception ignored) { }
        lista.add(u);
        return u;
    }

    public Optional<Usuario> buscarPorCedula(String cedula) {
        return lista.stream().filter(u -> {
            try {
                java.lang.reflect.Method m = u.getClass().getMethod("getCedula");
                Object val = m.invoke(u);
                return val != null && val.toString().equals(cedula);
            } catch (Exception e) {
                return false;
            }
        }).findFirst();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return lista.stream().filter(u -> {
            try {
                java.lang.reflect.Method m = u.getClass().getMethod("getId");
                Object val = m.invoke(u);
                return val != null && val.equals(id);
            } catch (Exception e) {
                return false;
            }
        }).findFirst();
    }

    public boolean actualizar(Usuario actualizado) {
        try {
            java.lang.reflect.Method getId = actualizado.getClass().getMethod("getId");
            Object idObj = getId.invoke(actualizado);
            if (idObj == null) return false;
            Integer id = (Integer) idObj;
            for (int i = 0; i < lista.size(); i++) {
                Usuario u = lista.get(i);
                try {
                    java.lang.reflect.Method m = u.getClass().getMethod("getId");
                    Object vid = m.invoke(u);
                    if (vid != null && vid.equals(id)) {
                        lista.set(i, actualizado);
                        return true;
                    }
                } catch (Exception ex) { }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean eliminar(Usuario u) {
        return lista.remove(u);
    }

    public boolean eliminarPorCedula(String cedula) {
        Optional<Usuario> op = buscarPorCedula(cedula);
        op.ifPresent(lista::remove);
        return op.isPresent();
    }

    public void borrarTodo() {
        lista.clear();
        seq = 1;
    }
}