package co.edu.uniquindio.mindsport.mindsportpro.util;

import co.edu.uniquindio.mindsport.mindsportpro.enums.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Script para poblar la base de datos con datos de prueba.
 * Primero consulta la estructura y datos existentes para evitar conflictos.
 */
public class PoblarBaseDatos {

    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("=== INICIANDO POBLACIÓN DE BASE DE DATOS ===\n");

        try {
            // 1. Consultar estructura y datos existentes
            consultarEstructuraYdatos();

            // 2. Verificar roles existentes (no se modifican)
            verificarRoles();
            
            // 3. Poblar tablas en orden (respetando dependencias)
            poblarCentrosTrabajo();
            poblarEspecialidades();
            poblarUsuarios();
            poblarEjercicios();
            poblarRutinas();
            poblarSesiones();
            poblarTecnicas();
            poblarLogros();
            poblarInscripciones();
            poblarPlanes();
            poblarNotificaciones();
            poblarUsuarioLogros();

            System.out.println("\n=== POBLACIÓN COMPLETADA ===");
        } catch (SQLException e) {
            System.err.println("Error al poblar la base de datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection();
        }
    }

    /**
     * Consulta la estructura de las tablas y muestra los datos existentes
     */
    private static void consultarEstructuraYdatos() throws SQLException {
        System.out.println("--- CONSULTANDO ESTRUCTURA Y DATOS EXISTENTES ---\n");
        
        try (Connection cn = DBUtil.getConnection()) {
            DatabaseMetaData metaData = cn.getMetaData();
            String[] tipos = {"TABLE"};
            ResultSet tables = metaData.getTables(null, null, null, tipos);
            
            List<String> nombresTablas = new ArrayList<>();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                nombresTablas.add(tableName);
            }
            
            // Consultar datos existentes en cada tabla
            for (String tabla : nombresTablas) {
                // Ignorar tablas del sistema que pueden causar errores
                if (tabla.startsWith("global_") || tabla.startsWith("session_") || 
                    tabla.startsWith("innodb_") || tabla.startsWith("performance_")) {
                    continue;
                }
                String sql = "SELECT COUNT(*) as total FROM " + tabla;
                try (PreparedStatement ps = cn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int total = rs.getInt("total");
                        System.out.println("Tabla: " + tabla + " - Registros existentes: " + total);
                    }
                } catch (SQLException e) {
                    // Ignorar errores en tablas del sistema o inaccesibles
                    System.out.println("Tabla: " + tabla + " - No se pudo consultar (tabla del sistema o inaccesible)");
                }
            }
            System.out.println();
        }
    }

    /**
     * Verificar que los roles existan (1=Atleta, 2=Coach)
     * No se insertan roles, solo se verifica que existan
     */
    private static void verificarRoles() throws SQLException {
        System.out.println("--- VERIFICANDO ROLES ---");
        try (Connection cn = DBUtil.getConnection()) {
            String checkSql = "SELECT codigo, descripcion FROM Rol WHERE codigo IN (1, 2)";
            try (PreparedStatement ps = cn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {
                System.out.println("Roles disponibles:");
                while (rs.next()) {
                    System.out.println("  - Código: " + rs.getInt("codigo") + " - " + rs.getString("descripcion"));
                }
            }
            System.out.println();
        }
    }

    /**
     * Poblar tabla CentroTrabajo
     */
    private static void poblarCentrosTrabajo() throws SQLException {
        System.out.println("--- POBLANDO CENTROS DE TRABAJO ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Obtener máximo ID existente
            int maxId = 0;
            String checkSql = "SELECT MAX(idCentro) as maxId FROM CentroTrabajo";
            try (PreparedStatement ps = cn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && !rs.wasNull()) {
                    maxId = rs.getInt("maxId");
                }
            }

            // Obtener centros existentes para evitar duplicados
            Set<String> centrosExistentes = new HashSet<>();
            String existSql = "SELECT CONCAT(nombre, '-', ciudad) as clave FROM CentroTrabajo";
            try (PreparedStatement ps = cn.prepareStatement(existSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    centrosExistentes.add(rs.getString("clave"));
                }
            }

            String[] nombres = {
                "Centro Deportivo El Dorado", "Gimnasio PowerFit", "Club Atlético Nacional",
                "Centro de Alto Rendimiento", "Academia Deportiva Los Andes", "Instituto Deportivo Quindío",
                "Centro Deportivo La Colina", "Gimnasio Fitness Pro", "Club Deportivo Caldas",
                "Centro de Entrenamiento Elite", "Academia Deportiva Medellín", "Gimnasio BodyTech",
                "Centro Deportivo San Fernando", "Club Atlético Bucaramanga", "Instituto Deportivo Pereira",
                "Centro de Alto Rendimiento Bogotá", "Gimnasio CrossFit Zone", "Academia Deportiva Cartagena",
                "Centro Deportivo Manizales", "Club Deportivo Independiente"
            };

            String[] ciudades = {
                "Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena",
                "Bucaramanga", "Pereira", "Manizales", "Armenia", "Ibagué",
                "Santa Marta", "Villavicencio", "Pasto", "Valledupar", "Montería",
                "Sincelejo", "Popayán", "Tunja", "Riohacha", "Quibdó"
            };

            int insertados = 0;
            for (int i = 0; i < nombres.length; i++) {
                String ciudad = ciudades[i % ciudades.length];
                String clave = nombres[i] + "-" + ciudad;
                
                // Verificar si ya existe
                if (centrosExistentes.contains(clave)) {
                    continue;
                }
                
                maxId++;
                String sql = "INSERT INTO CentroTrabajo (idCentro, nombre, ciudad) VALUES (?, ?, ?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, maxId);
                    ps.setString(2, nombres[i]);
                    ps.setString(3, ciudad);
                    ps.executeUpdate();
                    insertados++;
                    centrosExistentes.add(clave);
                }
            }
            System.out.println("Centros de trabajo insertados: " + insertados + "\n");
        }
    }

    /**
     * Poblar tabla Especialidad
     */
    private static void poblarEspecialidades() throws SQLException {
        System.out.println("--- POBLANDO ESPECIALIDADES ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Obtener máximo ID existente
            int maxId = 0;
            String checkSql = "SELECT MAX(idEspecialidad) as maxId FROM Especialidad";
            try (PreparedStatement ps = cn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && !rs.wasNull()) {
                    maxId = rs.getInt("maxId");
                }
            }

            // Obtener códigos existentes para evitar duplicados
            Set<String> codigosExistentes = new HashSet<>();
            String existSql = "SELECT codigo FROM Especialidad";
            try (PreparedStatement ps = cn.prepareStatement(existSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    codigosExistentes.add(rs.getString("codigo"));
                }
            }

            String[] codigos = {"ESP001", "ESP002", "ESP003", "ESP004", "ESP005", "ESP006", "ESP007", "ESP008", "ESP009", "ESP010",
                                "ESP011", "ESP012", "ESP013", "ESP014", "ESP015", "ESP016", "ESP017", "ESP018", "ESP019", "ESP020"};
            
            String[] descripciones = {
                "Psicología Deportiva", "Preparación Física", "Nutrición Deportiva",
                "Fisioterapia Deportiva", "Entrenamiento de Fuerza", "Entrenamiento de Resistencia",
                "Entrenamiento de Velocidad", "Técnica Deportiva", "Táctica Deportiva",
                "Rehabilitación Deportiva", "Medicina Deportiva", "Biomecánica",
                "Fisiología del Ejercicio", "Entrenamiento Funcional", "Yoga y Mindfulness",
                "Pilates", "CrossFit", "Boxeo", "Ciclismo", "Atletismo"
            };

            int insertados = 0;
            for (int i = 0; i < codigos.length; i++) {
                // Verificar si el código ya existe
                if (codigosExistentes.contains(codigos[i])) {
                    continue;
                }
                
                maxId++;
                String sql = "INSERT INTO Especialidad (idEspecialidad, codigo, descripcion) VALUES (?, ?, ?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, maxId);
                    ps.setString(2, codigos[i]);
                    ps.setString(3, descripciones[i]);
                    ps.executeUpdate();
                    insertados++;
                    codigosExistentes.add(codigos[i]);
                }
            }
            System.out.println("Especialidades insertadas: " + insertados + "\n");
        }
    }

    /**
     * Poblar tablas Usuario, Atleta y Coach
     */
    private static void poblarUsuarios() throws SQLException {
        System.out.println("--- POBLANDO USUARIOS (ATLETAS Y COACHES) ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Obtener cédulas y correos existentes
            Set<String> cedulasExistentes = new HashSet<>();
            Set<String> correosExistentes = new HashSet<>();
            String checkSql = "SELECT cedula, correo FROM Usuario";
            try (PreparedStatement ps = cn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cedulasExistentes.add(rs.getString("cedula"));
                    String correo = rs.getString("correo");
                    if (correo != null) {
                        correosExistentes.add(correo.toLowerCase());
                    }
                }
            }

            // Obtener IDs de especialidades y centros
            List<Integer> especialidades = new ArrayList<>();
            String espSql = "SELECT idEspecialidad FROM Especialidad";
            try (PreparedStatement ps = cn.prepareStatement(espSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    especialidades.add(rs.getInt("idEspecialidad"));
                }
            }

            List<Integer> centros = new ArrayList<>();
            String centSql = "SELECT idCentro FROM CentroTrabajo";
            try (PreparedStatement ps = cn.prepareStatement(centSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    centros.add(rs.getInt("idCentro"));
                }
            }

            cn.setAutoCommit(false);
            int atletasInsertados = 0;
            int coachesInsertados = 0;

            // Insertar 20 atletas
            String[] nombresAtletas = {
                "Carlos", "María", "Juan", "Ana", "Luis", "Laura", "Pedro", "Sofía", "Diego", "Valentina",
                "Andrés", "Camila", "Javier", "Isabella", "Sebastián", "Mariana", "Daniel", "Natalia", "Felipe", "Gabriela"
            };
            String[] apellidosAtletas = {
                "García", "Rodríguez", "López", "Martínez", "González", "Pérez", "Sánchez", "Ramírez", "Torres", "Flores",
                "Rivera", "Gómez", "Díaz", "Cruz", "Morales", "Ortiz", "Gutiérrez", "Chávez", "Ramos", "Mendoza"
            };

            for (int i = 0; i < nombresAtletas.length; i++) {
                String cedula = generarCedulaUnica(cedulasExistentes);
                cedulasExistentes.add(cedula);
                
                String nombres = nombresAtletas[i];
                String apellidos = apellidosAtletas[i];
                // Generar correo único
                String correo = nombres.toLowerCase() + "." + apellidos.toLowerCase() + "@email.com";
                int contador = 1;
                while (correosExistentes.contains(correo.toLowerCase())) {
                    correo = nombres.toLowerCase() + "." + apellidos.toLowerCase() + contador + "@email.com";
                    contador++;
                }
                correosExistentes.add(correo.toLowerCase());
                Genero genero = (i % 2 == 0) ? Genero.MASCULINO : Genero.FEMENINO;
                String contrasena = "123456";
                int rol = 1; // ATLETA

                // Insertar Usuario
                String sqlUsuario = "INSERT INTO Usuario (cedula, nombres, apellidos, correo, genero, contrasena, rol) VALUES (?,?,?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlUsuario)) {
                    ps.setString(1, cedula);
                    ps.setString(2, nombres);
                    ps.setString(3, apellidos);
                    ps.setString(4, correo);
                    ps.setString(5, genero.name());
                    ps.setString(6, contrasena);
                    ps.setInt(7, rol);
                    ps.executeUpdate();
                }

                // Insertar Atleta
                TipoPerfil[] perfiles = TipoPerfil.values();
                TipoPerfil perfil = perfiles[random.nextInt(perfiles.length)];
                double peso = 60 + random.nextDouble() * 30; // 60-90 kg
                double altura = 1.60 + random.nextDouble() * 0.30; // 1.60-1.90 m
                LocalDate fechaNac = LocalDate.now().minusYears(18 + random.nextInt(20)); // 18-38 años

                String sqlAtleta = "INSERT INTO Atleta (cedula, perfil_deportivo, peso, altura, fecha_nacimiento) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlAtleta)) {
                    ps.setString(1, cedula);
                    ps.setInt(2, perfil.getId());
                    ps.setDouble(3, peso);
                    ps.setDouble(4, altura);
                    ps.setDate(5, java.sql.Date.valueOf(fechaNac));
                    ps.executeUpdate();
                }

                // Insertar teléfono
                String sqlTelefono = "INSERT INTO TelefonoUsuario (cedula, numero) VALUES (?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlTelefono)) {
                    ps.setString(1, cedula);
                    ps.setString(2, "3" + String.format("%09d", random.nextInt(1000000000)));
                    ps.executeUpdate();
                }

                atletasInsertados++;
            }

            // Insertar 20 coaches
            String[] nombresCoaches = {
                "Roberto", "Patricia", "Fernando", "Carmen", "Ricardo", "Elena", "Miguel", "Lucía", "Alejandro", "Rosa",
                "Héctor", "Mónica", "Óscar", "Claudia", "Raúl", "Adriana", "Víctor", "Beatriz", "Manuel", "Diana"
            };
            String[] apellidosCoaches = {
                "Vargas", "Herrera", "Jiménez", "Moreno", "Álvarez", "Romero", "Méndez", "Castro", "Ortega", "Delgado",
                "Guerrero", "Rojas", "Medina", "Aguilar", "Vega", "Silva", "Molina", "Navarro", "Campos", "Vázquez"
            };

            for (int i = 0; i < nombresCoaches.length; i++) {
                String cedula = generarCedulaUnica(cedulasExistentes);
                cedulasExistentes.add(cedula);
                
                String nombres = nombresCoaches[i];
                String apellidos = apellidosCoaches[i];
                // Generar correo único
                String correo = nombres.toLowerCase() + "." + apellidos.toLowerCase() + "@coach.com";
                int contador = 1;
                while (correosExistentes.contains(correo.toLowerCase())) {
                    correo = nombres.toLowerCase() + "." + apellidos.toLowerCase() + contador + "@coach.com";
                    contador++;
                }
                correosExistentes.add(correo.toLowerCase());
                Genero genero = (i % 2 == 0) ? Genero.MASCULINO : Genero.FEMENINO;
                String contrasena = "123456";
                int rol = 2; // COACH

                // Insertar Usuario
                String sqlUsuario = "INSERT INTO Usuario (cedula, nombres, apellidos, correo, genero, contrasena, rol) VALUES (?,?,?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlUsuario)) {
                    ps.setString(1, cedula);
                    ps.setString(2, nombres);
                    ps.setString(3, apellidos);
                    ps.setString(4, correo);
                    ps.setString(5, genero.name());
                    ps.setString(6, contrasena);
                    ps.setInt(7, rol);
                    ps.executeUpdate();
                }

                // Insertar Coach
                String idProfesional = "COACH" + String.format("%04d", i + 1);
                Integer especialidad = especialidades.isEmpty() ? null : especialidades.get(random.nextInt(especialidades.size()));
                Integer centro = centros.isEmpty() ? null : centros.get(random.nextInt(centros.size()));
                // disponibilidad es un entero en la BD y no puede ser NULL, usar un valor por defecto
                int disponibilidad = 1; // Valor por defecto (1 = disponible)

                String sqlCoach = "INSERT INTO Coach (cedula, id_profesional, especialidad, centro_trabajo, disponibilidad) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlCoach)) {
                    ps.setString(1, cedula);
                    ps.setString(2, idProfesional);
                    if (especialidad != null) ps.setInt(3, especialidad);
                    else ps.setNull(3, Types.INTEGER);
                    if (centro != null) ps.setInt(4, centro);
                    else ps.setNull(4, Types.INTEGER);
                    ps.setInt(5, disponibilidad);
                    ps.executeUpdate();
                }

                // Insertar teléfono
                String sqlTelefono = "INSERT INTO TelefonoUsuario (cedula, numero) VALUES (?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlTelefono)) {
                    ps.setString(1, cedula);
                    ps.setString(2, "3" + String.format("%09d", random.nextInt(1000000000)));
                    ps.executeUpdate();
                }

                coachesInsertados++;
            }

            cn.commit();
            cn.setAutoCommit(true);
            System.out.println("Atletas insertados: " + atletasInsertados);
            System.out.println("Coaches insertados: " + coachesInsertados + "\n");
        }
    }

    /**
     * Poblar tabla Ejercicio
     */
    private static void poblarEjercicios() throws SQLException {
        System.out.println("--- POBLANDO EJERCICIOS ---");
        try (Connection cn = DBUtil.getConnection()) {
            String[] titulos = {
                "Carrera Continua", "Estiramiento Dinámico", "Sentadillas", "Flexiones", "Abdominales",
                "Plancha", "Burpees", "Salto de Cuerda", "Mountain Climbers", "Zancadas",
                "Meditación Guiada", "Respiración Profunda", "Visualización", "Relajación Muscular", "Mindfulness",
                "Yoga Flow", "Pilates Básico", "Estiramiento Estático", "Movilidad Articular", "Calentamiento Cardio"
            };

            String[] descripciones = {
                "Carrera continua de 20 minutos a ritmo moderado",
                "Estiramientos dinámicos para calentar músculos",
                "Sentadillas 3 series de 15 repeticiones",
                "Flexiones 3 series de 12 repeticiones",
                "Abdominales 3 series de 20 repeticiones",
                "Plancha isométrica 3 series de 30 segundos",
                "Burpees completos 3 series de 10 repeticiones",
                "Salto de cuerda 3 series de 1 minuto",
                "Mountain climbers 3 series de 20 repeticiones",
                "Zancadas alternas 3 series de 12 por pierna",
                "Meditación guiada de 10 minutos",
                "Ejercicio de respiración profunda 5 minutos",
                "Visualización de objetivos deportivos",
                "Relajación muscular progresiva",
                "Práctica de mindfulness 15 minutos",
                "Secuencia de yoga flow 20 minutos",
                "Rutina básica de pilates 25 minutos",
                "Estiramientos estáticos post-ejercicio",
                "Movilidad articular completa 10 minutos",
                "Calentamiento cardiovascular 15 minutos"
            };

            FaseUso[] fases = FaseUso.values();
            TipoEjercicio[] tipos = TipoEjercicio.values();

            int insertados = 0;
            for (int i = 0; i < titulos.length; i++) {
                String sql = "INSERT INTO Ejercicio (faseUso, titulo, descripcion, duracion, tipoEjercicio) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    FaseUso fase = fases[random.nextInt(fases.length)];
                    TipoEjercicio tipo = tipos[random.nextInt(tipos.length)];
                    int duracion = 300 + random.nextInt(1200); // 5-25 minutos en segundos

                    ps.setString(1, fase.name());
                    ps.setString(2, titulos[i]);
                    ps.setString(3, descripciones[i]);
                    ps.setInt(4, duracion);
                    ps.setString(5, tipo.name());
                    ps.executeUpdate();
                    insertados++;
                }
            }
            System.out.println("Ejercicios insertados: " + insertados + "\n");
        }
    }

    /**
     * Poblar tabla Rutina
     */
    private static void poblarRutinas() throws SQLException {
        System.out.println("--- POBLANDO RUTINAS ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Obtener cédulas de coaches
            List<String> coaches = new ArrayList<>();
            String coachSql = "SELECT cedula FROM Coach";
            try (PreparedStatement ps = cn.prepareStatement(coachSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    coaches.add(rs.getString("cedula"));
                }
            }

            // Obtener IDs de ejercicios
            List<Integer> ejercicios = new ArrayList<>();
            String ejSql = "SELECT id FROM Ejercicio";
            try (PreparedStatement ps = cn.prepareStatement(ejSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ejercicios.add(rs.getInt("id"));
                }
            }

            String[] titulos = {
                "Rutina de Fuerza Básica", "Entrenamiento de Resistencia", "Rutina de Velocidad",
                "Entrenamiento Funcional", "Rutina de Hipertrofia", "Cardio Intenso",
                "Rutina de Recuperación", "Entrenamiento Mixto", "Rutina de Flexibilidad",
                "Entrenamiento de Potencia", "Rutina de Agilidad", "Entrenamiento Completo",
                "Rutina de Core", "Entrenamiento de Piernas", "Rutina de Brazos",
                "Entrenamiento Full Body", "Rutina de Alta Intensidad", "Entrenamiento de Estabilidad",
                "Rutina de Movilidad", "Entrenamiento Personalizado"
            };

            String[] descripciones = {
                "Rutina enfocada en desarrollo de fuerza muscular",
                "Entrenamiento para mejorar resistencia cardiovascular",
                "Rutina diseñada para aumentar velocidad",
                "Entrenamiento funcional completo",
                "Rutina para desarrollo de masa muscular",
                "Entrenamiento cardiovascular intenso",
                "Rutina suave para días de recuperación",
                "Entrenamiento combinando diferentes modalidades",
                "Rutina enfocada en flexibilidad y estiramiento",
                "Entrenamiento para desarrollar potencia",
                "Rutina para mejorar agilidad y coordinación",
                "Entrenamiento completo de todo el cuerpo",
                "Rutina específica para fortalecer core",
                "Entrenamiento enfocado en piernas",
                "Rutina específica para brazos y hombros",
                "Entrenamiento de cuerpo completo",
                "Rutina de alta intensidad interval training",
                "Entrenamiento para mejorar estabilidad",
                "Rutina enfocada en movilidad articular",
                "Entrenamiento personalizado según necesidades"
            };

            NivelDificultad[] niveles = NivelDificultad.values();

            cn.setAutoCommit(false);
            int insertados = 0;

            for (int i = 0; i < titulos.length; i++) {
                if (coaches.isEmpty() || ejercicios.isEmpty()) break;

                String cedulaCoach = coaches.get(random.nextInt(coaches.size()));
                String titulo = titulos[i];
                String descripcion = descripciones[i];
                int duracionEstimada = 1800 + random.nextInt(3600); // 30-90 minutos
                NivelDificultad nivel = niveles[random.nextInt(niveles.length)];
                boolean publicada = random.nextBoolean();

                String sql = "INSERT INTO Rutina (cedulaCoach, titulo, descripcion, duracionEstimada, nivelDificultad, publicada) VALUES (?,?,?,?,?,?)";
                int rutinaId = 0;
                try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, cedulaCoach);
                    ps.setString(2, titulo);
                    ps.setString(3, descripcion);
                    ps.setInt(4, duracionEstimada);
                    ps.setString(5, nivel.name());
                    ps.setBoolean(6, publicada);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            rutinaId = rs.getInt(1);
                        }
                    }
                }

                // Asociar 3-5 ejercicios aleatorios a la rutina con orden
                int numEjercicios = 3 + random.nextInt(3); // 3-5 ejercicios
                Collections.shuffle(ejercicios);
                // Intentar con orden primero
                String sqlRel = "INSERT INTO RutinaEjercicio (rutinaId, ejercicio_id, orden) VALUES (?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlRel)) {
                    for (int j = 0; j < numEjercicios && j < ejercicios.size(); j++) {
                        ps.setInt(1, rutinaId);
                        ps.setInt(2, ejercicios.get(j));
                        ps.setInt(3, j + 1); // Orden: 1, 2, 3, ...
                        ps.addBatch();
                    }
                    ps.executeBatch();
                } catch (SQLException e) {
                    // Si falla, intentar sin orden
                    try {
                        sqlRel = "INSERT INTO RutinaEjercicio (rutinaId, ejercicio_id) VALUES (?,?)";
                        try (PreparedStatement ps = cn.prepareStatement(sqlRel)) {
                            for (int j = 0; j < numEjercicios && j < ejercicios.size(); j++) {
                                ps.setInt(1, rutinaId);
                                ps.setInt(2, ejercicios.get(j));
                                ps.addBatch();
                            }
                            ps.executeBatch();
                        }
                    } catch (SQLException e2) {
                        // Intentar con nombres alternativos
                        try {
                            sqlRel = "INSERT INTO RutinaEjercicio (idRutina, idEjercicio, orden) VALUES (?,?,?)";
                            try (PreparedStatement ps = cn.prepareStatement(sqlRel)) {
                                for (int j = 0; j < numEjercicios && j < ejercicios.size(); j++) {
                                    ps.setInt(1, rutinaId);
                                    ps.setInt(2, ejercicios.get(j));
                                    ps.setInt(3, j + 1);
                                    ps.addBatch();
                                }
                                ps.executeBatch();
                            }
                        } catch (SQLException e3) {
                            System.err.println("Error al insertar relaciones RutinaEjercicio: " + e3.getMessage());
                        }
                    }
                }

                insertados++;
            }

            cn.commit();
            cn.setAutoCommit(true);
            System.out.println("Rutinas insertadas: " + insertados + "\n");
        }
    }

    /**
     * Poblar tabla Sesion
     */
    private static void poblarSesiones() throws SQLException {
        System.out.println("--- POBLANDO SESIONES ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Obtener cédulas de atletas
            List<String> atletas = new ArrayList<>();
            String atlSql = "SELECT cedula FROM Atleta";
            try (PreparedStatement ps = cn.prepareStatement(atlSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    atletas.add(rs.getString("cedula"));
                }
            }

            // Obtener IDs de rutinas
            List<Integer> rutinas = new ArrayList<>();
            String rutSql = "SELECT id FROM Rutina";
            try (PreparedStatement ps = cn.prepareStatement(rutSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rutinas.add(rs.getInt("id"));
                }
            }

            if (atletas.isEmpty() || rutinas.isEmpty()) {
                System.out.println("No hay atletas o rutinas para crear sesiones\n");
                return;
            }

            String[] observaciones = {
                "Excelente rendimiento", "Buen trabajo", "Necesita mejorar técnica",
                "Muy motivado", "Buen progreso", "Requiere más intensidad",
                "Técnica correcta", "Buen esfuerzo", "Necesita descanso",
                "Rendimiento óptimo", "Bien ejecutado", "Requiere práctica adicional",
                "Muy disciplinado", "Buen seguimiento", "Excelente actitud",
                "Progreso constante", "Bien realizado", "Necesita más concentración",
                "Trabajo destacado", "Buen desempeño", null
            };

            int insertados = 0;
            for (int i = 0; i < 20; i++) {
                String cedulaAtleta = atletas.get(random.nextInt(atletas.size()));
                Integer rutinaId = rutinas.get(random.nextInt(rutinas.size()));
                LocalDate fecha = LocalDate.now().minusDays(random.nextInt(90)); // Últimos 90 días
                int duracionReal = 1200 + random.nextInt(3600); // 20-80 minutos
                double puntuacion = 60 + random.nextDouble() * 40; // 60-100
                String observacion = observaciones[random.nextInt(observaciones.length)];

                String sql = "INSERT INTO Sesion (cedulaAtleta, rutinaId, fecha, duracionReal, puntuacion, observacionCoach) VALUES (?,?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, cedulaAtleta);
                    ps.setInt(2, rutinaId);
                    ps.setDate(3, java.sql.Date.valueOf(fecha));
                    ps.setInt(4, duracionReal);
                    ps.setDouble(5, puntuacion);
                    ps.setString(6, observacion);
                    ps.executeUpdate();
                    insertados++;
                }
            }
            System.out.println("Sesiones insertadas: " + insertados + "\n");
        }
    }

    /**
     * Poblar tabla Tecnica
     */
    private static void poblarTecnicas() throws SQLException {
        System.out.println("--- POBLANDO TÉCNICAS ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Verificar si la tabla existe
            DatabaseMetaData metaData = cn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "Tecnica", null);
            if (!tables.next()) {
                System.out.println("Tabla Tecnica no existe, omitiendo...\n");
                return;
            }
            String[] nombres = {
                "Visualización Mental", "Respiración 4-7-8", "Relajación Progresiva",
                "Mindfulness", "Meditación Activa", "Técnica de Anclaje",
                "Control de Ansiedad", "Gestión del Estrés", "Concentración Focalizada",
                "Técnica de Ritual", "Preparación Mental", "Recuperación Activa",
                "Técnica de Motivación", "Control de Nervios", "Enfoque en el Presente",
                "Técnica de Respiración", "Relajación Muscular", "Visualización de Éxito",
                "Técnica de Calma", "Preparación Pre-Competencia"
            };

            String[] descripciones = {
                "Visualización de movimientos y resultados exitosos",
                "Técnica de respiración para reducir ansiedad",
                "Relajación muscular progresiva de todo el cuerpo",
                "Práctica de atención plena en el momento presente",
                "Meditación durante actividad física",
                "Técnica de anclaje para mantener calma",
                "Control de ansiedad pre-competencia",
                "Gestión del estrés durante entrenamientos",
                "Concentración en un punto específico",
                "Rutina pre-ejercicio para preparación mental",
                "Preparación mental antes de competir",
                "Técnicas de recuperación mental post-ejercicio",
                "Técnicas para mantener motivación",
                "Control de nervios antes de eventos",
                "Enfoque en el momento presente",
                "Diversas técnicas de respiración",
                "Relajación de grupos musculares específicos",
                "Visualización de logros y éxitos",
                "Técnicas para mantener calma",
                "Preparación mental específica para competencias"
            };

            AplicabilidadTecnica[] aplicabilidades = AplicabilidadTecnica.values();

            int insertados = 0;
            for (int i = 0; i < nombres.length; i++) {
                String sql = "INSERT INTO Tecnica (nombre, descripcion, aplicabilidad) VALUES (?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    AplicabilidadTecnica aplicabilidad = aplicabilidades[random.nextInt(aplicabilidades.length)];
                    ps.setString(1, nombres[i]);
                    ps.setString(2, descripciones[i]);
                    ps.setString(3, aplicabilidad.name());
                    ps.executeUpdate();
                    insertados++;
                }
            }
            System.out.println("Técnicas insertadas: " + insertados + "\n");
        }
    }

    /**
     * Poblar tabla Logro
     */
    private static void poblarLogros() throws SQLException {
        System.out.println("--- POBLANDO LOGROS ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Obtener IDs existentes para evitar duplicados
            Set<Integer> idsExistentes = new HashSet<>();
            String checkSql = "SELECT id FROM Logro";
            try (PreparedStatement ps = cn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    idsExistentes.add(rs.getInt("id"));
                }
            }
            
            // Obtener máximo ID
            int maxId = 0;
            if (!idsExistentes.isEmpty()) {
                maxId = Collections.max(idsExistentes);
            }
            String[] nombres = {
                "Primera Sesión", "Semana Completa", "Mes de Entrenamiento",
                "100 Sesiones", "Mejor Puntuación", "Consistencia Perfecta",
                "Rutina Completa", "Objetivo Alcanzado", "Progreso Notable",
                "Dedicación Total", "Superación Personal", "Marca Personal",
                "Entrenamiento Diario", "Sin Faltas", "Esfuerzo Extra",
                "Recuperación Rápida", "Técnica Perfecta", "Motivación Constante",
                "Disciplina Ejemplar", "Logro Destacado"
            };

            String[] descripciones = {
                "Completar la primera sesión de entrenamiento",
                "Completar 7 días consecutivos de entrenamiento",
                "Completar 30 días de entrenamiento continuo",
                "Alcanzar 100 sesiones completadas",
                "Obtener la mejor puntuación en una sesión",
                "Mantener consistencia sin faltas",
                "Completar una rutina completa",
                "Alcanzar un objetivo personal",
                "Mostrar progreso notable",
                "Demostrar dedicación total",
                "Superar límites personales",
                "Establecer una nueva marca personal",
                "Mantener entrenamiento diario",
                "Completar período sin faltas",
                "Realizar esfuerzo extra",
                "Recuperarse rápidamente",
                "Ejecutar técnica perfecta",
                "Mantener motivación constante",
                "Demostrar disciplina ejemplar",
                "Alcanzar logro destacado"
            };

            String[] criterios = {
                "sesiones_completadas >= 1",
                "dias_consecutivos >= 7",
                "dias_consecutivos >= 30",
                "sesiones_completadas >= 100",
                "puntuacion_maxima >= 95",
                "consistencia >= 100",
                "rutinas_completadas >= 1",
                "objetivos_alcanzados >= 1",
                "progreso >= 20",
                "dedicacion >= 90",
                "superacion >= 1",
                "marca_personal >= 1",
                "dias_entrenamiento >= 30",
                "faltas == 0",
                "esfuerzo_extra >= 1",
                "recuperacion <= 24",
                "tecnica_perfecta >= 1",
                "motivacion >= 80",
                "disciplina >= 95",
                "logro_destacado >= 1"
            };

            int insertados = 0;
            for (int i = 0; i < nombres.length; i++) {
                maxId++;
                // Verificar si ya existe un logro con este ID
                if (idsExistentes.contains(maxId)) {
                    continue;
                }
                
                String sql = "INSERT INTO Logro (id, nombre, descripcion, criterio, puntos) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    int puntos = 10 + random.nextInt(40); // 10-50 puntos
                    ps.setInt(1, maxId);
                    ps.setString(2, nombres[i]);
                    ps.setString(3, descripciones[i]);
                    ps.setString(4, criterios[i]);
                    ps.setInt(5, puntos);
                    ps.executeUpdate();
                    insertados++;
                    idsExistentes.add(maxId);
                } catch (SQLException e) {
                    // Intentar sin ID (auto-increment)
                    try {
                        sql = "INSERT INTO Logro (nombre, descripcion, criterio, puntos) VALUES (?,?,?,?)";
                        try (PreparedStatement ps = cn.prepareStatement(sql)) {
                            int puntos = 10 + random.nextInt(40);
                            ps.setString(1, nombres[i]);
                            ps.setString(2, descripciones[i]);
                            ps.setString(3, criterios[i]);
                            ps.setInt(4, puntos);
                            ps.executeUpdate();
                            insertados++;
                        }
                    } catch (SQLException e2) {
                        System.err.println("Error al insertar logro: " + e2.getMessage());
                    }
                }
            }
            System.out.println("Logros insertados: " + insertados + "\n");
        }
    }

    /**
     * Poblar tabla Inscripcion
     */
    private static void poblarInscripciones() throws SQLException {
        System.out.println("--- POBLANDO INSCRIPCIONES ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Verificar si la tabla existe
            DatabaseMetaData metaData = cn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "Inscripcion", null);
            if (!tables.next()) {
                System.out.println("Tabla Inscripcion no existe, omitiendo...\n");
                return;
            }

            // Obtener atletas y planes/rutinas
            List<String> atletas = new ArrayList<>();
            String atlSql = "SELECT cedula FROM Atleta";
            try (PreparedStatement ps = cn.prepareStatement(atlSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    atletas.add(rs.getString("cedula"));
                }
            }

            List<Integer> rutinas = new ArrayList<>();
            String rutSql = "SELECT id FROM Rutina";
            try (PreparedStatement ps = cn.prepareStatement(rutSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rutinas.add(rs.getInt("id"));
                }
            }

            if (atletas.isEmpty() || rutinas.isEmpty()) {
                System.out.println("No hay atletas o rutinas para crear inscripciones\n");
                return;
            }

            int insertados = 0;
            for (int i = 0; i < 20; i++) {
                String cedulaAtleta = atletas.get(random.nextInt(atletas.size()));
                Integer rutinaId = rutinas.get(random.nextInt(rutinas.size()));
                LocalDate fechaInscripcion = LocalDate.now().minusDays(random.nextInt(60)); // Últimos 60 días
                boolean activa = random.nextBoolean();

                // Intentar diferentes estructuras posibles
                String sql = "INSERT INTO Inscripcion (cedulaAtleta, rutinaId, fechaInscripcion, activa) VALUES (?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, cedulaAtleta);
                    ps.setInt(2, rutinaId);
                    ps.setDate(3, java.sql.Date.valueOf(fechaInscripcion));
                    ps.setBoolean(4, activa);
                    ps.executeUpdate();
                    insertados++;
                } catch (SQLException e) {
                    // Intentar sin activa
                    try {
                        sql = "INSERT INTO Inscripcion (cedulaAtleta, rutinaId, fechaInscripcion) VALUES (?,?,?)";
                        try (PreparedStatement ps = cn.prepareStatement(sql)) {
                            ps.setString(1, cedulaAtleta);
                            ps.setInt(2, rutinaId);
                            ps.setDate(3, java.sql.Date.valueOf(fechaInscripcion));
                            ps.executeUpdate();
                            insertados++;
                        }
                    } catch (SQLException e2) {
                        System.err.println("Error al insertar inscripción: " + e2.getMessage());
                    }
                }
            }
            System.out.println("Inscripciones insertadas: " + insertados + "\n");
        }
    }

    /**
     * Poblar tabla Plan
     */
    private static void poblarPlanes() throws SQLException {
        System.out.println("--- POBLANDO PLANES ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Verificar si la tabla existe
            DatabaseMetaData metaData = cn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "Plan", null);
            if (!tables.next()) {
                System.out.println("Tabla Plan no existe, omitiendo...\n");
                return;
            }

            String[] nombres = {
                "Plan Básico", "Plan Intermedio", "Plan Avanzado", "Plan Premium",
                "Plan Mensual", "Plan Trimestral", "Plan Semestral", "Plan Anual",
                "Plan Personalizado", "Plan Grupal", "Plan Individual", "Plan Familiar",
                "Plan Deportivo", "Plan Fitness", "Plan Wellness", "Plan Competitivo",
                "Plan Recreativo", "Plan Profesional", "Plan Estudiante", "Plan Senior"
            };

            String[] descripciones = {
                "Plan básico de entrenamiento", "Plan intermedio con más opciones",
                "Plan avanzado para atletas experimentados", "Plan premium con todas las funciones",
                "Plan de un mes de duración", "Plan de tres meses",
                "Plan de seis meses", "Plan de un año completo",
                "Plan personalizado según necesidades", "Plan para grupos",
                "Plan individual", "Plan familiar con descuentos",
                "Plan enfocado en deportes", "Plan de fitness general",
                "Plan de bienestar integral", "Plan para competidores",
                "Plan recreativo", "Plan profesional", "Plan para estudiantes",
                "Plan para adultos mayores"
            };

            double[] precios = {29.99, 49.99, 79.99, 99.99, 39.99, 89.99, 149.99, 249.99,
                               119.99, 69.99, 59.99, 199.99, 89.99, 49.99, 79.99, 129.99,
                               34.99, 159.99, 24.99, 44.99};

            int insertados = 0;
            for (int i = 0; i < nombres.length; i++) {
                String sql = "INSERT INTO Plan (nombre, descripcion, precio) VALUES (?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, nombres[i]);
                    ps.setString(2, descripciones[i]);
                    ps.setDouble(3, precios[i]);
                    ps.executeUpdate();
                    insertados++;
                } catch (SQLException e) {
                    System.err.println("Error al insertar plan: " + e.getMessage());
                }
            }
            System.out.println("Planes insertados: " + insertados + "\n");
        }
    }

    /**
     * Poblar tabla Notificacion
     */
    private static void poblarNotificaciones() throws SQLException {
        System.out.println("--- POBLANDO NOTIFICACIONES ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Verificar si la tabla existe
            DatabaseMetaData metaData = cn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "Notificacion", null);
            if (!tables.next()) {
                System.out.println("Tabla Notificacion no existe, omitiendo...\n");
                return;
            }

            // Obtener usuarios
            List<String> usuarios = new ArrayList<>();
            String usrSql = "SELECT cedula FROM Usuario";
            try (PreparedStatement ps = cn.prepareStatement(usrSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(rs.getString("cedula"));
                }
            }

            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios para crear notificaciones\n");
                return;
            }

            String[] titulos = {
                "Nueva rutina disponible", "Recordatorio de sesión", "Logro desbloqueado",
                "Mensaje del coach", "Actualización del plan", "Nueva notificación",
                "Recordatorio importante", "Felicitaciones", "Nuevo logro alcanzado",
                "Actualización de perfil", "Nueva sesión programada", "Cambio de horario",
                "Recordatorio de pago", "Nueva oferta disponible", "Actualización del sistema",
                "Bienvenida", "Recordatorio de entrenamiento", "Nuevo contenido disponible",
                "Actualización de rutina", "Notificación importante"
            };

            String[] mensajes = {
                "Tienes una nueva rutina disponible para entrenar",
                "Recuerda tu sesión programada para hoy",
                "¡Felicidades! Has desbloqueado un nuevo logro",
                "Tu coach te ha enviado un mensaje",
                "Tu plan ha sido actualizado",
                "Tienes una nueva notificación",
                "Recordatorio importante sobre tu entrenamiento",
                "¡Felicitaciones por tu progreso!",
                "Has alcanzado un nuevo logro",
                "Tu perfil ha sido actualizado",
                "Tienes una nueva sesión programada",
                "Se ha cambiado el horario de tu sesión",
                "Recordatorio de pago pendiente",
                "Hay una nueva oferta disponible para ti",
                "El sistema ha sido actualizado",
                "¡Bienvenido a MindSport!",
                "Recuerda realizar tu entrenamiento de hoy",
                "Hay nuevo contenido disponible",
                "Tu rutina ha sido actualizada",
                "Tienes una notificación importante"
            };

            int insertados = 0;
            for (int i = 0; i < 20; i++) {
                String cedulaUsuario = usuarios.get(random.nextInt(usuarios.size()));
                LocalDate fecha = LocalDate.now().minusDays(random.nextInt(30)); // Últimos 30 días
                boolean leida = random.nextBoolean();

                String sql = "INSERT INTO Notificacion (cedulaUsuario, titulo, mensaje, fecha, leida) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, cedulaUsuario);
                    ps.setString(2, titulos[i]);
                    ps.setString(3, mensajes[i]);
                    ps.setDate(4, java.sql.Date.valueOf(fecha));
                    ps.setBoolean(5, leida);
                    ps.executeUpdate();
                    insertados++;
                } catch (SQLException e) {
                    // Intentar sin leida
                    try {
                        sql = "INSERT INTO Notificacion (cedulaUsuario, titulo, mensaje, fecha) VALUES (?,?,?,?)";
                        try (PreparedStatement ps = cn.prepareStatement(sql)) {
                            ps.setString(1, cedulaUsuario);
                            ps.setString(2, titulos[i]);
                            ps.setString(3, mensajes[i]);
                            ps.setDate(4, java.sql.Date.valueOf(fecha));
                            ps.executeUpdate();
                            insertados++;
                        }
                    } catch (SQLException e2) {
                        System.err.println("Error al insertar notificación: " + e2.getMessage());
                    }
                }
            }
            System.out.println("Notificaciones insertadas: " + insertados + "\n");
        }
    }

    /**
     * Poblar tabla UsuarioLogro
     */
    private static void poblarUsuarioLogros() throws SQLException {
        System.out.println("--- POBLANDO USUARIO LOGROS ---");
        try (Connection cn = DBUtil.getConnection()) {
            // Verificar si la tabla existe
            DatabaseMetaData metaData = cn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "UsuarioLogro", null);
            if (!tables.next()) {
                System.out.println("Tabla UsuarioLogro no existe, omitiendo...\n");
                return;
            }

            // Obtener usuarios (atletas)
            List<String> atletas = new ArrayList<>();
            String atlSql = "SELECT cedula FROM Atleta";
            try (PreparedStatement ps = cn.prepareStatement(atlSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    atletas.add(rs.getString("cedula"));
                }
            }

            // Obtener logros
            List<Integer> logros = new ArrayList<>();
            String logSql = "SELECT id FROM Logro";
            try (PreparedStatement ps = cn.prepareStatement(logSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logros.add(rs.getInt("id"));
                }
            }

            if (atletas.isEmpty() || logros.isEmpty()) {
                System.out.println("No hay atletas o logros para crear relaciones\n");
                return;
            }

            int insertados = 0;
            // Asignar 2-5 logros aleatorios a cada atleta
            for (String cedulaAtleta : atletas) {
                int numLogros = 2 + random.nextInt(4); // 2-5 logros
                Collections.shuffle(logros);
                
                for (int i = 0; i < numLogros && i < logros.size(); i++) {
                    Integer logroId = logros.get(i);
                    java.time.LocalDateTime fechaObtencion = java.time.LocalDateTime.now()
                            .minusDays(random.nextInt(90))
                            .minusHours(random.nextInt(24));

                    String sql = "INSERT INTO UsuarioLogro (cedulaUsuario, logroId, fechaObtencion) VALUES (?,?,?)";
                    try (PreparedStatement ps = cn.prepareStatement(sql)) {
                        ps.setString(1, cedulaAtleta);
                        ps.setInt(2, logroId);
                        ps.setTimestamp(3, java.sql.Timestamp.valueOf(fechaObtencion));
                        ps.executeUpdate();
                        insertados++;
                    } catch (SQLException e) {
                        // Intentar con nombres alternativos
                        try {
                            sql = "INSERT INTO UsuarioLogro (usuarioId, logroId, obtenidoEn) VALUES (?,?,?)";
                            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                                ps.setString(1, cedulaAtleta);
                                ps.setInt(2, logroId);
                                ps.setTimestamp(3, java.sql.Timestamp.valueOf(fechaObtencion));
                                ps.executeUpdate();
                                insertados++;
                            }
                        } catch (SQLException e2) {
                            System.err.println("Error al insertar usuario logro: " + e2.getMessage());
                        }
                    }
                }
            }
            System.out.println("Usuario logros insertados: " + insertados + "\n");
        }
    }

    /**
     * Genera una cédula única
     */
    private static String generarCedulaUnica(Set<String> existentes) {
        String cedula;
        do {
            cedula = String.format("%010d", random.nextInt(1000000000));
        } while (existentes.contains(cedula));
        return cedula;
    }
}

