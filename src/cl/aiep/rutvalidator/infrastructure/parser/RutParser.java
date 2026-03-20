// Toma el RUT ya limpio y lo separa en número y dígito verificador.
package cl.aiep.rutvalidator.infrastructure.parser;

import cl.aiep.rutvalidator.domain.model.Rut;

public class RutParser {

    public Rut parse(String formattedRut) {
        if (formattedRut == null || formattedRut.length() < 2) {
            throw new IllegalArgumentException("El RUT debe tener al menos 2 caracteres.");
        }

        String number = formattedRut.substring(0, formattedRut.length() - 1);
        String dv = formattedRut.substring(formattedRut.length() - 1);

        return new Rut(number, dv);
    }
}