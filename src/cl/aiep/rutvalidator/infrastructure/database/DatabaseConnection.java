package cl.aiep.rutvalidator.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Clase encargada de gestionar la conexión con la base de datos SQLite e inicializar la tabla necesaria para guardar registros de RUT.
public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:rut_app.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de SQLite: ", e);
        }

        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String sql = """
                CREATE TABLE IF NOT EXISTS rut_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    number TEXT NOT NULL,
                    dv TEXT NOT NULL,
                    full_rut TEXT NOT NULL,
                    operation_type TEXT NOT NULL,
                    validation_result TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """;

        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}
