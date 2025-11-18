package co.edu.uniquindio.mindsport.mindsportpro.util;

import co.edu.uniquindio.mindsport.mindsportpro.model.Usuario;
import javafx.stage.Stage;

public class SessionManager {

    private static SessionManager instance;

    private Usuario usuarioActual;

    private Stage stagePrincipal;

    

    private SessionManager() {}

    

    public static SessionManager getInstance() {

        if (instance == null) {

            instance = new SessionManager();

        }

        return instance;

    }

    

    public void login(Usuario usuario, Stage stage) {

        this.usuarioActual = usuario;

        this.stagePrincipal = stage;

    }

    

    public void logout() {

        this.usuarioActual = null;

    }

    

    public Usuario getUsuarioActual() {

        return usuarioActual;

    }

    

    public Stage getStagePrincipal() {

        return stagePrincipal;

    }

    

    public boolean isLoggedIn() {

        return usuarioActual != null;

    }

}


