// Clase modelo que representa un registro de operación sobre un RUT, almacenando número, dígito verificador, RUT completo, tipo de operación y resultado de validación.
package cl.aiep.rutvalidator.domain.model;

public class RutRecord {
    private final String number;
    private final String dv;
    private final String fullRut;
    private final String operationType;
    private String validationResult;

    public RutRecord(String number, String dv, String fullRut, String operationType, String validationResult) {
        this.number = number;
        this.dv = dv;
        this.fullRut = fullRut;
        this.operationType = operationType;
        this.validationResult = validationResult;
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

    public String getValidationResult() {
        return validationResult;
    }

}