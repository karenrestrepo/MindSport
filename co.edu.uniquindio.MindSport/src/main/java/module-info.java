module co.edu.uniquindio.mindsport.mindsportpro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.swing;   // <- NECESARIO por SwingFXUtils
    requires com.github.librepdf.openpdf;

    opens co.edu.uniquindio.mindsport.mindsportpro to javafx.fxml;
    exports co.edu.uniquindio.mindsport.mindsportpro;
    opens co.edu.uniquindio.mindsport.mindsportpro.model;
    exports co.edu.uniquindio.mindsport.mindsportpro.model;
    opens co.edu.uniquindio.mindsport.mindsportpro.controller;
    exports co.edu.uniquindio.mindsport.mindsportpro.controller;
    
}
