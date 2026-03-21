// Clase principal que se encarga de pedir el RUT al usuario, crear las dependencias, ejecutar la validación y mostrar el resultado.
package cl.aiep.rutvalidator.app;

import java.util.List;
import java.util.Scanner;

import cl.aiep.rutvalidator.domain.model.Rut;
import cl.aiep.rutvalidator.domain.model.RutRecord;
import cl.aiep.rutvalidator.domain.ports.CheckDigitCalculator;
import cl.aiep.rutvalidator.domain.ports.DocumentFormatter;
import cl.aiep.rutvalidator.domain.ports.DocumentValidator;
import cl.aiep.rutvalidator.domain.ports.RutRecordRepository;
import cl.aiep.rutvalidator.domain.service.RutGenerator;
import cl.aiep.rutvalidator.domain.service.RutValidator;
import cl.aiep.rutvalidator.infrastructure.calculator.Modulo11RutCalculator;
import cl.aiep.rutvalidator.infrastructure.formatter.RutFormatter;
import cl.aiep.rutvalidator.infrastructure.parser.RutParser;
import cl.aiep.rutvalidator.infrastructure.repository.InMemoryRutRecordRepository;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        DocumentFormatter formatter = new RutFormatter();
        CheckDigitCalculator calculator = new Modulo11RutCalculator();
        RutParser parser = new RutParser();
        RutRecordRepository repository = new InMemoryRutRecordRepository(); // Implementación en memoria del repositorio

        DocumentValidator validator = new RutValidator(formatter, calculator, parser, repository);
        RutGenerator generator = new RutGenerator(calculator, repository);

        int option;

        do {
            System.out.println("\n=== SISTEMA RUT CHILENO ===");
            System.out.println("1. Validar RUT");
            System.out.println("2. Calcular dígito verificador desde número de RUT");
            System.out.println("3. Listar registros guardados");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1:
                    System.out.print("Ingrese un RUT (ej: 12.345.678-5): ");
                    String rutIngresado = scanner.nextLine();

                    boolean isValid = validator.isValid(rutIngresado);

                    if (isValid) {
                        System.out.println("Resultado: El RUT es válido.");
                    } else {
                        System.out.println("Resultado: El RUT es inválido.");
                    }
                    break;

                case 2:
                    System.out.print("Ingrese solo el número de RUT, sin dígito verificador): ");
                    String rutNumber = scanner.nextLine().replace(".", "").replace("-", "").trim();

                    try {
                        Rut generatedRut = generator.generate(rutNumber);
                        System.out.println("Dígito verificador calculado: " + generatedRut.getDv());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    List<RutRecord> records = repository.findAll();

                    if (records.isEmpty()) {
                        System.out.println("No hay registros guardados.");
                    } else {
                        System.out.println("\n=== REGISTROS ===");
                        for (RutRecord record : records) {
                            System.out.println(
                                    "Operación: " + record.getOperationType()
                                            + " | RUT: " + record.getFullRut());
                        }
                    }
                    break;

                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        } while (option != 0);

        scanner.close();
    }
}