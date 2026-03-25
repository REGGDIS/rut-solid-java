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
import cl.aiep.rutvalidator.infrastructure.database.DatabaseConnection;
import cl.aiep.rutvalidator.infrastructure.formatter.RutFormatter;
import cl.aiep.rutvalidator.infrastructure.parser.RutParser;
import cl.aiep.rutvalidator.infrastructure.repository.SQLiteRutRecordRepository;

// Clase principal que interactúa con el usuario por consola, muestra el menú de opciones,
// crea las dependencias del sistema y ejecuta la validación, generación de dígito verificador
// y listado de registros.
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        DatabaseConnection.initializeDatabase();

        DocumentFormatter formatter = new RutFormatter();
        CheckDigitCalculator calculator = new Modulo11RutCalculator();
        RutParser parser = new RutParser();
        RutRecordRepository repository = new SQLiteRutRecordRepository();

        DocumentValidator validator = new RutValidator(formatter, calculator, parser, repository);
        RutGenerator generator = new RutGenerator(calculator, repository);

        int option;

        do {
            System.out.println("\n=== SISTEMA RUT CHILENO ===");
            System.out.println("1. Validar RUT");
            System.out.println("2. Calcular dígito verificador desde número de RUT");
            System.out.println("3. Listar registros guardados");
            System.out.println("4. Buscar registros por tipo de operación");
            System.out.println("5. Buscar registro por RUT completo");
            System.out.println("0. Salir");

            String inputOption;
            boolean opcionValida;

            do {
                System.out.print("Seleccione una opción: ");
                inputOption = scanner.nextLine().trim();

                if (!inputOption.matches("[0-5]")) {
                    System.out.println("Opción inválida. Debe ingresar 0, 1, 2, 3, 4 o 5.");
                    opcionValida = false;
                } else {
                    opcionValida = true;
                }
            } while (!opcionValida);

            option = Integer.parseInt(inputOption);

            switch (option) {
                case 1:
                    System.out.print("Ingrese un RUT completo (ej: 12.345.678-5): ");
                    String rutIngresado = scanner.nextLine().trim();

                    if (rutIngresado.isEmpty()) {
                        System.out.println("Error: debe ingresar un RUT.");
                        break;
                    }

                    boolean isValid = validator.isValid(rutIngresado);

                    if (isValid) {
                        System.out.println("Resultado: El RUT es válido.");
                    } else {
                        System.out.println("Resultado: El RUT es inválido.");
                    }
                    break;

                case 2:
                    System.out.print("Ingrese solo el número del RUT, sin dígito verificador: ");
                    String rutNumber = scanner.nextLine().replace(".", "").replace("-", "").trim();

                    if (rutNumber.isEmpty()) {
                        System.out.println("Error: debe ingresar el número del RUT.");
                        break;
                    }

                    try {
                        Rut generatedRut = generator.generate(rutNumber);
                        System.out.println("Dígito verificador calculado: " + generatedRut.getDv());
                        System.out.println("RUT completo: " + generatedRut.getFullRut());
                    } catch (Exception e) {
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

                case 4:
                    System.out.print("Ingrese el tipo de operación a buscar (VALIDACION o GENERACION_DV): ");
                    String operationType = scanner.nextLine().trim().toUpperCase();

                    if (operationType.isEmpty()) {
                        System.out.println("Error: debe ingresar un tipo de operación.");
                        break;
                    }

                    mostrarRegistros(repository.findByOperationType(operationType));
                    break;

                case 5:
                    System.out.print("Ingrese el RUT completo a buscar (ej: 12.345.678-5 o 123456785): ");
                    String fullRutInput = scanner.nextLine().trim();

                    if (fullRutInput.isEmpty()) {
                        System.out.println("Error: debe ingresar un RUT completo.");
                        break;
                    }

                    String fullRut = normalizarRutCompleto(fullRutInput);

                    if (fullRut.isEmpty()) {
                        System.out.println("Error: el formato del RUT ingresado no es válido.");
                        break;
                    }

                    mostrarRegistros(repository.findByFullRut(fullRut));
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

    private static void mostrarRegistros(List<RutRecord> records) {
        if (records.isEmpty()) {
            System.out.println("No se encontraron registros.");
        } else {
            System.out.println("\n=== REGISTROS ===");
            for (RutRecord record : records) {
                System.out.println(
                        "Operación: " + record.getOperationType()
                                + " | RUT: " + record.getFullRut());
            }
        }
    }

    private static String normalizarRutCompleto(String input) {
        if (input == null) {
            return "";
        }

        String cleaned = input
                .replace(".", "")
                .replace("-", "")
                .replace(" ", "")
                .trim()
                .toUpperCase();

        if (cleaned.length() < 2) {
            return ""; // Un RUT completo debe tener al menos 2 caracteres (número + dígito
                       // verificador)
        }

        String number = cleaned.substring(0, cleaned.length() - 1);
        String dv = cleaned.substring(cleaned.length() - 1);

        return number + "-" + dv;
    }
}