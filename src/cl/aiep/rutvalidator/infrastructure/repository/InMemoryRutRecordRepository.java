// Implementación en memoria del repositorio de registros de RUT, utilizada como almacenamiento temporal.
package cl.aiep.rutvalidator.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;

import cl.aiep.rutvalidator.domain.model.RutRecord;
import cl.aiep.rutvalidator.domain.ports.RutRecordRepository;

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
    public List<RutRecord> findByOperationType(String operatioType) {
        List<RutRecord> filteredRecords = new ArrayList<>();

        for (RutRecord record : records) {
            if (record.getOperationType().equalsIgnoreCase(operatioType)) {
                filteredRecords.add(record);
            }
        }

        return filteredRecords;
    }

    @Override
    public List<RutRecord> findByFullRut(String fullRut) {
        List<RutRecord> filteredRecords = new ArrayList<>();

        for (RutRecord record : records) {
            if (record.getFullRut().equalsIgnoreCase(fullRut)) {
                filteredRecords.add(record);
            }
        }

        return filteredRecords;
    }

}
