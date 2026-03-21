// Representa un RUT ya separado en número y dígito verificador.
package cl.aiep.rutvalidator.domain.model;

public class Rut {
    private final String number;
    private final String dv;

    public Rut(String number, String dv) {
        this.number = number;
        this.dv = dv;
    }

    public String getNumber() {
        return number;
    }

    public String getDv() {
        return dv;
    }

    public String getFullRut() {
        return number + "-" + dv;
    }
}