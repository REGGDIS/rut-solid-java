// Clase modelo que representa un registro de operación sobre un RUT, almacenando número, dígito verificador, RUT completo y tipo de operación realizada.
package cl.aiep.rutvalidator.domain.model;

public class RutRecord {
    private final String number;
    private final String dv;
    private final String fullRut;
    private final String operationType;

    public RutRecord(String number, String dv, String fullRut, String operationType) {
        this.number = number;
        this.dv = dv;
        this.fullRut = fullRut;
        this.operationType = operationType;
    }

    public String getNumber() {
        return number;
    }

    public String getDv() {
        return dv;
    }

    public String getFullRut() {
        return fullRut;
    }

    public String getOperationType() {
        return operationType;
    }

}