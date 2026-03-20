// Limpia el RUT: elimina puntos, elimina guion, elimina espacios y convierte a mayúsculas.
package cl.aiep.rutvalidator.infrastructure.formatter;

import cl.aiep.rutvalidator.domain.ports.DocumentFormatter;

public class RutFormatter implements DocumentFormatter {

    @Override
    public String format(String rawValue) {
        if (rawValue == null) {
            return null;
        }

        return rawValue
                .replace(".", "")
                .replace("-", "")
                .replace(" ", "")
                .trim()
                .toUpperCase();
    }
}