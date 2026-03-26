package cl.aiep.rutvalidator.infrastructure.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cl.aiep.rutvalidator.domain.model.RutRecord;
import cl.aiep.rutvalidator.domain.ports.RutRecordRepository;
import cl.aiep.rutvalidator.infrastructure.database.DatabaseConnection;

// Implementación del repositorio de registros de RUT utilizando SQLite
// para almacenar la información en una base de datos real.
public class SQLiteRutRecordRepository implements RutRecordRepository {

    @Override
    public void save(RutRecord record) {
        String sql = """
                INSERT INTO rut_records (number, dv, full_rut, operation_type)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, record.getNumber());
            statement.setString(2, record.getDv());
            statement.setString(3, record.getFullRut());
            statement.setString(4, record.getOperationType());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al guardar registro: " + e.getMessage());
        }
    }

    @Override
    public List<RutRecord> findAll() {
        List<RutRecord> records = new ArrayList<>();

        String sql = """
                SELECT number, dv, full_rut, operation_type
                FROM rut_records
                ORDER BY id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                RutRecord record = new RutRecord(
                        resultSet.getString("number"),
                        resultSet.getString("dv"),
                        resultSet.getString("full_rut"),
                        resultSet.getString("operation_type"));

                records.add(record);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar registros: " + e.getMessage());
        }

        return records;
    }

    @Override
    public List<RutRecord> findByOperationType(String operationType) {
        List<RutRecord> records = new ArrayList<>();

        String sql = """
                SELECT number, dv, full_rut, operation_type
                FROM rut_records
                WHERE operation_type = ?
                ORDER BY id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, operationType);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RutRecord record = new RutRecord(
                            resultSet.getString("number"),
                            resultSet.getString("dv"),
                            resultSet.getString("full_rut"),
                            resultSet.getString("operation_type"));

                    records.add(record);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar registros por tipo de operación: " + e.getMessage());
        }

        return records;
    }

    @Override
    public List<RutRecord> findByFullRut(String fullRut) {
        List<RutRecord> records = new ArrayList<>();

        String sql = """
                SELECT number, dv, full_rut, operation_type
                FROM rut_records
                WHERE full_rut = ?
                ORDER BY id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, fullRut);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RutRecord record = new RutRecord(
                            resultSet.getString("number"),
                            resultSet.getString("dv"),
                            resultSet.getString("full_rut"),
                            resultSet.getString("operation_type"));

                    records.add(record);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar registro por RUT: " + e.getMessage());
        }

        return records;
    }

    @Override
    public List<RutRecord> findByNumberRange(int minNumber, int maxNumber) {
        List<RutRecord> records = new ArrayList<>();

        String sql = """
                SELECT number, dv, full_rut, operation_type
                FROM rut_records
                WHERE CAST(number AS INTEGER) BETWEEN ? AND ?
                ORDER BY CAST(number AS INTEGER) ASC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, minNumber);
            statement.setInt(2, maxNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RutRecord record = new RutRecord(
                            resultSet.getString("number"),
                            resultSet.getString("dv"),
                            resultSet.getString("full_rut"),
                            resultSet.getString("operation_type"));

                    records.add(record);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar registros por rango de numeración: " + e.getMessage());
        }

        return records;
    }

}