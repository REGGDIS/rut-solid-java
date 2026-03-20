// Calcula el dígito verificador con el algoritmo módulo 11 para RUT.
package cl.aiep.rutvalidator.infrastructure.calculator;

import cl.aiep.rutvalidator.domain.ports.CheckDigitCalculator;

public class Modulo11RutCalculator implements CheckDigitCalculator {

    @Override
    public String calculate(String number) {
        if (number == null || number.isEmpty()) {
            throw new IllegalArgumentException("El número no puede estar vacío.");
        }

        if (!number.matches("\\d+")) {
            throw new IllegalArgumentException("El número del RUT debe contener solo dígitos.");
        }

        int sum = 0;
        int multiplier = 2;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            sum += digit * multiplier;
            multiplier++;

            if (multiplier > 7) {
                multiplier = 2;
            }
        }

        int remainder = sum % 11;
        int dv = 11 - remainder;

        if (dv == 11) {
            return "0";
        } else if (dv == 10) {
            return "K";
        } else {
            return String.valueOf(dv);
        }
    }
}