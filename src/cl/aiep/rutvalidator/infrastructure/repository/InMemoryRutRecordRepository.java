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

}
