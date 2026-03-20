// Define el contrato para cualquier clase que calcule un dígito verificador.
package cl.aiep.rutvalidator.domain.ports;

public interface CheckDigitCalculator {
    String calculate(String number);
}