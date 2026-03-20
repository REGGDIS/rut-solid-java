// Clase principal que se encarga de pedir el RUT al usuario, crear las dependencias, ejecutar la validación y mostrar el resultado.
package cl.aiep.rutvalidator.app;

import java.util.Scanner;

import cl.aiep.rutvalidator.domain.ports.CheckDigitCalculator;
import cl.aiep.rutvalidator.domain.ports.DocumentFormatter;
import cl.aiep.rutvalidator.domain.ports.DocumentValidator;
import cl.aiep.rutvalidator.domain.service.RutValidator;
import cl.aiep.rutvalidator.infrastructure.calculator.Modulo11RutCalculator;
import cl.aiep.rutvalidator.infrastructure.formatter.RutFormatter;
import cl.aiep.rutvalidator.infrastructure.parser.RutParser;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        DocumentFormatter formatter = new RutFormatter();
        CheckDigitCalculator calculator = new Modulo11RutCalculator();
        RutParser parser = new RutParser();

        DocumentValidator validator = new RutValidator(formatter, calculator, parser);

        System.out.println("=== VALIDADOR DE RUT CHILENO ===");
        System.out.print("Ingrese un RUT (ej: 12.345.678-5): ");
        String rutIngresado = scanner.nextLine();

        boolean isValid = validator.isValid(rutIngresado);

        if (isValid) {
            System.out.println("Resultado: El RUT es válido.");
        } else {
            System.out.println("Resultado: El RUT es inválido.");
        }

        scanner.close();
    }
}