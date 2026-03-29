package cl.aiep.rutvalidator.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;

import cl.aiep.rutvalidator.domain.model.RutRecord;
import cl.aiep.rutvalidator.domain.ports.RutRecordRepository;

// Implementación en memoria del repositorio de registros de RUT.
public class InMemoryRutRecordRepository implements RutRecordRepository {

    private final List<RutRecord> records = new ArrayList<>();

    @Override
    public void save(RutRecord record) {
        records.add(record);
    }

    @Override
    public List<RutRecord> findAll() {
        return new ArrayList<>(records);
    }

    @Override
    public List<RutRecord> findByOperationType(String operationType) {
        List<RutRecord> results = new ArrayList<>();

        for (RutRecord record : records) {
            if (record.getOperationType().equalsIgnoreCase(operationType)) {
                results.add(record);
            }
        }

        return results;
    }

    @Override
    public List<RutRecord> findByFullRut(String fullRut) {
        List<RutRecord> results = new ArrayList<>();

        for (RutRecord record : records) {
            if (record.getFullRut().equalsIgnoreCase(fullRut)) {
                results.add(record);
            }
        }

        return results;
    }

    @Override
    public List<RutRecord> findByNumberRange(int minNumber, int maxNumber) {
        List<RutRecord> results = new ArrayList<>();

        for (RutRecord record : records) {
            int number = Integer.parseInt(record.getNumber());
            if (number >= minNumber && number <= maxNumber) {
                results.add(record);
            }
        }

        return results;
    }

    @Override
    public List<RutRecord> findByCreatedAtDateRange(String startDate, String endDate) {
        List<RutRecord> results = new ArrayList<>();

        for (RutRecord record : records) {
            if (record.getCreatedAt() != null && record.getCreatedAt().length() >= 10) {
                String recordDate = record.getCreatedAt().substring(0, 10);

                if (recordDate.compareTo(startDate) >= 0 && recordDate.compareTo(endDate) <= 0) {
                    results.add(record);
                }
            }
        }

        return results;
    }
}