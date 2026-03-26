// Interfaz que define las operaciones de persistencia para guardar y recuperar registros de RUT.
package cl.aiep.rutvalidator.domain.ports;

import java.util.List;
import cl.aiep.rutvalidator.domain.model.RutRecord;

public interface RutRecordRepository {
    void save(RutRecord record);

    List<RutRecord> findAll();

    List<RutRecord> findByOperationType(String operationType);

    List<RutRecord> findByFullRut(String fullRut);

    List<RutRecord> findByNumberRange(int minNumber, int maxNumber);
}
