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
            System.out.println("4. Buscar registros");
            System.out.println("0. Salir");

            String inputOption;
            boolean opcionValida;

            do {
                System.out.print("Seleccione una opción: ");
                inputOption = scanner.nextLine().trim();

                if (!inputOption.matches("[0-4]")) {
                    System.out.println("Opción inválida. Debe ingresar 0, 1, 2, 3 o 4.");
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

                    manejarFlujoPostInsercion(scanner, repository);
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

                        manejarFlujoPostInsercion(scanner, repository);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    mostrarRegistros(repository.findAll());
                    break;

                case 4:
                    mostrarMenuBusqueda(scanner, repository);
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
                                + " | Resultado: " + record.getValidationResult()
                                + " | RUT: " + record.getFullRut()
                                + " | Fecha: " + extraerFecha(record.getCreatedAt()));
            }
        }
    }

    private static void manejarFlujoPostInsercion(Scanner scanner, RutRecordRepository repository) {
        while (true) {
            System.out.print("\n¿Desea ingresar un nuevo número de RUT para operar? (S/N): ");
            String respuesta = scanner.nextLine().trim().toUpperCase();

            if (respuesta.equals("S")) {
                break;
            } else if (respuesta.equals("N")) {
                mostrarRegistros(repository.findAll());
                break;
            } else {
                System.out.println("Respuesta inválida. Debe ingresar S o N.");
            }
        }
    }

    private static void mostrarMenuBusqueda(Scanner scanner, RutRecordRepository repository) {
        int opcionBusqueda = -1;

        do {
            System.out.println("\n=== BÚSQUEDA DE REGISTROS ===");
            System.out.println("1. Buscar por tipo de operación");
            System.out.println("2. Buscar por RUT completo");
            System.out.println("3. Buscar por rango de numeración");
            System.out.println("4. Buscar por rango de fecha de ingreso");
            System.out.println("0. Volver al menú principal");

            System.out.print("Seleccione una opción de búsqueda: ");
            String input = scanner.nextLine().trim();

            if (!input.matches("[0-4]")) {
                System.out.println("Opción inválida. Debe ingresar 0, 1, 2, 3 o 4.");
                continue;
            }

            opcionBusqueda = Integer.parseInt(input);

            switch (opcionBusqueda) {
                case 1:
                    System.out.print("Ingrese el tipo de operación a buscar (VALIDACION o GENERACION_DV): ");
                    String operationType = scanner.nextLine().trim().toUpperCase();

                    if (operationType.isEmpty()) {
                        System.out.println("Error: debe ingresar un tipo de operación.");
                        break;
                    }

                    mostrarRegistros(repository.findByOperationType(operationType));
                    break;

                case 2:
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

                case 3:
                    System.out.print("Ingrese el número mínimo del rango: ");
                    String minInput = scanner.nextLine().trim();

                    System.out.print("Ingrese el número máximo del rango: ");
                    String maxInput = scanner.nextLine().trim();

                    if (minInput.isEmpty() || maxInput.isEmpty()) {
                        System.out.println("Error: debe ingresar ambos valores para el rango.");
                        break;
                    }

                    if (!minInput.matches("\\d+") || !maxInput.matches("\\d+")) {
                        System.out.println("Error: los valores del rango deben contener solo dígitos.");
                        break;
                    }

                    int minNumber = Integer.parseInt(minInput);
                    int maxNumber = Integer.parseInt(maxInput);

                    if (minNumber > maxNumber) {
                        System.out.println("Error: el número mínimo no puede ser mayor que el máximo.");
                        break;
                    }

                    mostrarRegistros(repository.findByNumberRange(minNumber, maxNumber));
                    break;

                case 4:
                    System.out.print("Ingrese la fecha inicial (yyyy-MM-dd): ");
                    String startDate = scanner.nextLine().trim();

                    System.out.print("Ingrese la fecha final (yyyy-MM-dd): ");
                    String endDate = scanner.nextLine().trim();

                    if (startDate.isEmpty() || endDate.isEmpty()) {
                        System.out.println("Error: debe ingresar ambas fechas.");
                        break;
                    }

                    if (!esFechaValida(startDate) || !esFechaValida(endDate)) {
                        System.out.println("Error: el formato debe ser yyyy-MM-dd");
                        break;
                    }

                    mostrarRegistros(repository.findByCreatedAtDateRange(startDate, endDate));
                    break;

                case 0:
                    System.out.println("Volviendo al menú principal...");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcionBusqueda != 0);
    }

    private static boolean esFechaValida(String fecha) {
        return fecha.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private static String extraerFecha(String createdAt) {
        if (createdAt == null || createdAt.length() < 10) {
            return "Sin fecha";
        }
        return createdAt.substring(0, 10);
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
            return "";
        }

        String number = cleaned.substring(0, cleaned.length() - 1);
        String dv = cleaned.substring(cleaned.length() - 1);

        return number + "-" + dv;
    }
}