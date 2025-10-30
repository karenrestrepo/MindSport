package co.edu.uniquindio.mindsport.mindsportpro.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilidad para gestionar conexiones a la base de datos MariaDB
 * Implementa patrón Singleton para la conexión
 */
public class DBUtil {

    // Configuración de conexión
    private static final String HOST = "8.219.99.245";
    private static final String PORT = "3306";
    private static final String DATABASE = "MindSportPro";
    private static final String URL = "jdbc:mariadb://" + HOST + ":" + PORT + "/" + DATABASE;
    private static final String USER = "Proyecto";
    private static final String PASSWORD = "mindsportpro";

    private static Connection connection = null;

    private DBUtil() {}

    /**
     * Obtiene una conexión a la base de datos
     * @return Connection objeto de conexión a MariaDB
     * @throws SQLException si hay error al conectar
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver de MariaDB no encontrado", e);
            }
        }
        return connection;
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica si la conexión está activa
     * @return true si está conectado, false en caso contrario
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}