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
            mostrarMenuPrincipal();

            String inputOption;
            boolean opcionValida;

            do {
                System.out.print("Seleccione una opción del menú principal: ");
                inputOption = scanner.nextLine().trim();

                if (!inputOption.matches("[0-4]")) {
                    mostrarError("Opción inválida. Debe ingresar 0, 1, 2, 3 o 4.");
                    opcionValida = false;
                } else {
                    opcionValida = true;
                }
            } while (!opcionValida);

            option = Integer.parseInt(inputOption);

            switch (option) {
                case 1:
                    mostrarEncabezado("VALIDAR RUT");

                    System.out.println("Ejemplo: 12.345.678-5");
                    System.out.print("Ingrese un RUT completo: ");
                    String rutIngresado = scanner.nextLine().trim();

                    if (rutIngresado.isEmpty()) {
                        mostrarError("Debe ingresar un RUT.");
                        esperarEnter(scanner);
                        break;
                    }

                    boolean isValid = validator.isValid(rutIngresado);

                    if (isValid) {
                        mostrarOk("Resultado: El RUT es válido.");
                    } else {
                        mostrarError("Resultado: El RUT es inválido.");
                    }

                    manejarFlujoPostInsercion(scanner, repository);
                    break;

                case 2:
                    mostrarEncabezado("CALCULAR DÍGITO VERIFICADOR");

                    System.out.println("Ejemplo: 12345678");
                    System.out.print("Ingrese solo el número del RUT, sin dígito verificador: ");
                    String rutNumber = scanner.nextLine().replace(".", "").replace("-", "").trim();

                    if (rutNumber.isEmpty()) {
                        mostrarError("Debe ingresar el número del RUT.");
                        esperarEnter(scanner);
                        break;
                    }

                    try {
                        Rut generatedRut = generator.generate(rutNumber);
                        mostrarOk("Dígito verificador calculado: " + generatedRut.getDv());
                        mostrarInfo("RUT completo: " + generatedRut.getFullRut());

                        manejarFlujoPostInsercion(scanner, repository);
                    } catch (Exception e) {
                        mostrarError(e.getMessage());
                        esperarEnter(scanner);
                    }
                    break;

                case 3:
                    mostrarEncabezado("LISTADO DE REGISTROS");
                    mostrarRegistros(repository.findAll());
                    esperarEnter(scanner);
                    break;

                case 4:
                    mostrarMenuBusqueda(scanner, repository);
                    break;

                case 0:
                    mostrarInfo("Saliendo del sistema...");
                    break;

                default:
                    mostrarError("Opción no válida.");
                    esperarEnter(scanner);
            }

        } while (option != 0);

        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        mostrarEncabezado("SISTEMA RUT CHILENO");

        System.out.println("[ Operaciones ]");
        System.out.println("1. Validar RUT");
        System.out.println("2. Calcular dígito verificador desde número de RUT");
        System.out.println();

        System.out.println("[ Consultas ]");
        System.out.println("3. Listar registros guardados");
        System.out.println("4. Buscar registros");
        System.out.println();

        System.out.println("[ Sistema ]");
        System.out.println("0. Salir");
        System.out.println();
    }

    private static void mostrarRegistros(List<RutRecord> records) {
        mostrarSubEncabezado("RESULTADOS");

        if (records.isEmpty()) {
            mostrarInfo("No se encontraron registros.");
            return;
        }

        mostrarInfo("Se encontraron " + records.size() + " registro(s).");
        System.out.println();

        for (RutRecord record : records) {
            System.out.println("Operación : " + record.getOperationType());
            System.out.println("Resultado : " + record.getValidationResult());
            System.out.println("RUT       : " + record.getFullRut());
            System.out.println("Fecha     : " + extraerFecha(record.getCreatedAt()));
            System.out.println("----------------------------------------");
        }
    }

    private static void manejarFlujoPostInsercion(Scanner scanner, RutRecordRepository repository) {
        while (true) {
            System.out.println();
            System.out.print("¿Desea ingresar un nuevo número de RUT para operar? (S/N): ");
            String respuesta = scanner.nextLine().trim().toUpperCase();

            if (respuesta.equals("S")) {
                mostrarInfo("Volviendo al menú principal...");
                esperarEnter(scanner);
                break;
            } else if (respuesta.equals("N")) {
                mostrarEncabezado("LISTADO DE REGISTROS INGRESADOS");
                mostrarRegistros(repository.findAll());
                esperarEnter(scanner);
                break;
            } else {
                mostrarError("Respuesta inválida. Debe ingresar S o N.");
            }
        }
    }

    private static void mostrarMenuBusqueda(Scanner scanner, RutRecordRepository repository) {
        int opcionBusqueda = -1;

        do {
            mostrarEncabezado("BÚSQUEDA DE REGISTROS");
            System.out.println("Ruta actual: Menú principal > Búsqueda de registros");
            System.out.println();

            System.out.println("1. Buscar por tipo de operación");
            System.out.println("2. Buscar por RUT completo");
            System.out.println("3. Buscar por rango de numeración");
            System.out.println("4. Buscar por rango de fecha de ingreso");
            System.out.println("0. Volver al menú principal");
            System.out.println();

            System.out.print("Seleccione una opción de búsqueda: ");
            String input = scanner.nextLine().trim();

            if (!input.matches("[0-4]")) {
                mostrarError("Opción inválida. Debe ingresar 0, 1, 2, 3 o 4.");
                esperarEnter(scanner);
                continue;
            }

            opcionBusqueda = Integer.parseInt(input);

            switch (opcionBusqueda) {
                case 1:
                    mostrarSubEncabezado("BÚSQUEDA POR TIPO DE OPERACIÓN");

                    System.out.println("Valores permitidos:");
                    System.out.println("- VALIDACION");
                    System.out.println("- GENERACION_DV");
                    System.out.print("Ingrese el tipo de operación a buscar: ");
                    String operationType = scanner.nextLine().trim().toUpperCase();

                    if (operationType.isEmpty()) {
                        mostrarError("Debe ingresar un tipo de operación.");
                        esperarEnter(scanner);
                        break;
                    }

                    mostrarRegistros(repository.findByOperationType(operationType));
                    esperarEnter(scanner);
                    break;

                case 2:
                    mostrarSubEncabezado("BÚSQUEDA POR RUT COMPLETO");

                    System.out.println("Ejemplos válidos:");
                    System.out.println("- 12.345.678-5");
                    System.out.println("- 123456785");
                    System.out.print("Ingrese el RUT completo a buscar: ");
                    String fullRutInput = scanner.nextLine().trim();

                    if (fullRutInput.isEmpty()) {
                        mostrarError("Debe ingresar un RUT completo.");
                        esperarEnter(scanner);
                        break;
                    }

                    String fullRut = normalizarRutCompleto(fullRutInput);

                    if (fullRut.isEmpty()) {
                        mostrarError("El formato del RUT ingresado no es válido.");
                        esperarEnter(scanner);
                        break;
                    }

                    mostrarRegistros(repository.findByFullRut(fullRut));
                    esperarEnter(scanner);
                    break;

                case 3:
                    mostrarSubEncabezado("BÚSQUEDA POR RANGO DE NUMERACIÓN");

                    System.out.println("Ejemplo:");
                    System.out.println("Número mínimo: 10000000");
                    System.out.println("Número máximo: 20000000");
                    System.out.print("Ingrese el número mínimo del rango: ");
                    String minInput = scanner.nextLine().trim();

                    System.out.print("Ingrese el número máximo del rango: ");
                    String maxInput = scanner.nextLine().trim();

                    if (minInput.isEmpty() || maxInput.isEmpty()) {
                        mostrarError("Debe ingresar ambos valores para el rango.");
                        esperarEnter(scanner);
                        break;
                    }

                    if (!minInput.matches("\\d+") || !maxInput.matches("\\d+")) {
                        mostrarError("Los valores del rango deben contener solo dígitos.");
                        esperarEnter(scanner);
                        break;
                    }

                    int minNumber = Integer.parseInt(minInput);
                    int maxNumber = Integer.parseInt(maxInput);

                    if (minNumber > maxNumber) {
                        mostrarError("El número mínimo no puede ser mayor que el máximo.");
                        esperarEnter(scanner);
                        break;
                    }

                    mostrarRegistros(repository.findByNumberRange(minNumber, maxNumber));
                    esperarEnter(scanner);
                    break;

                case 4:
                    mostrarSubEncabezado("BÚSQUEDA POR RANGO DE FECHA");

                    System.out.println("Formato requerido: yyyy-MM-dd");
                    System.out.println("Ejemplo: 2026-03-28");
                    System.out.print("Ingrese la fecha inicial: ");
                    String startDate = scanner.nextLine().trim();

                    System.out.print("Ingrese la fecha final: ");
                    String endDate = scanner.nextLine().trim();

                    if (startDate.isEmpty() || endDate.isEmpty()) {
                        mostrarError("Debe ingresar ambas fechas.");
                        esperarEnter(scanner);
                        break;
                    }

                    if (!esFechaValida(startDate) || !esFechaValida(endDate)) {
                        mostrarError("El formato debe ser yyyy-MM-dd.");
                        esperarEnter(scanner);
                        break;
                    }

                    mostrarRegistros(repository.findByCreatedAtDateRange(startDate, endDate));
                    esperarEnter(scanner);
                    break;

                case 0:
                    mostrarInfo("Volviendo al menú principal...");
                    esperarEnter(scanner);
                    break;

                default:
                    mostrarError("Opción no válida.");
                    esperarEnter(scanner);
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

    private static void mostrarEncabezado(String titulo) {
        System.out.println();
        System.out.println("========================================");
        System.out.println(" " + titulo);
        System.out.println("========================================");
    }

    private static void mostrarSubEncabezado(String titulo) {
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println(" " + titulo);
        System.out.println("----------------------------------------");
    }

    private static void mostrarOk(String mensaje) {
        System.out.println("[OK] " + mensaje);
    }

    private static void mostrarError(String mensaje) {
        System.out.println("[ERROR] " + mensaje);
    }

    private static void mostrarInfo(String mensaje) {
        System.out.println("[INFO] " + mensaje);
    }

    private static void esperarEnter(Scanner scanner) {
        System.out.println();
        System.out.print("Presione Enter para continuar...");
        scanner.nextLine();
    }
}