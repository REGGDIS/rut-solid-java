// Interfaz que define las operaciones de persistencia para guardar y recuperar registros de RUT, dejando preparada la futura integración con una base de datos.
package cl.aiep.rutvalidator.domain.ports;

import java.util.List;
import cl.aiep.rutvalidator.domain.model.RutRecord;

public interface RutRecordRepository {
    void save(RutRecord record);

    List<RutRecord> findAll();
}
