package cl.aiep.rutvalidator.domain.ports;

import java.util.List;
import cl.aiep.rutvalidator.domain.model.RutRecord;

public interface RutRecordRepository {
    void save(RutRecord record);

    List<RutRecord> findAll();
}
