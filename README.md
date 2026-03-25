# Sistema de gestión de RUT chileno en Java aplicando principios SOLID

## Descripción

Este proyecto consiste en el desarrollo de un sistema de gestión de RUT chileno implementado en Java, utilizando el algoritmo de módulo 11 para calcular y verificar el dígito verificador.

El objetivo principal del proyecto no es solo validar correctamente un RUT, sino también demostrar la aplicación práctica de los principios **SOLID** en una arquitectura orientada a objetos. Para ello, el sistema fue diseñado separando responsabilidades, utilizando interfaces específicas y desacoplando la lógica de validación, generación y persistencia de datos de sus implementaciones concretas.

Actualmente, la aplicación permite validar RUT completos, calcular el dígito verificador a partir del número del RUT, guardar registros en una base de datos SQLite y realizar búsquedas filtradas sobre la información almacenada.

## Objetivo académico

Este trabajo fue desarrollado como ejercicio de aplicación de principios de diseño de software en el contexto de la asignatura **Taller de Ingeniería de Software**, con el propósito de demostrar:

- implementación de un algoritmo real en Java
- separación de responsabilidades
- uso de abstracciones mediante interfaces
- diseño orientado a objetos mantenible y extensible
- aplicación de los cinco principios SOLID
- integración de persistencia con base de datos
- consultas de información mediante parámetros específicos

## Funcionalidades actuales

El sistema permite:

- validar un RUT completo
- calcular el dígito verificador desde un número de RUT sin DV
- guardar automáticamente los registros generados en una base de datos SQLite
- listar todos los registros almacenados
- buscar registros por tipo de operación
- buscar registros por RUT completo sin depender estrictamente del formato ingresado

### Ejemplos de entradas aceptadas

El sistema puede trabajar con formatos como:

- `12.345.678-5`
- `12345678-5`
- `123456785`
- `12.345.678-k`
- `107558942`
- `10.755.894-2`

## Menú principal del sistema

```text
=== SISTEMA RUT CHILENO ===
1. Validar RUT
2. Calcular dígito verificador desde número de RUT
3. Listar registros guardados
4. Buscar registros por tipo de operación
5. Buscar registro por RUT completo
0. Salir
```

## Estructura del proyecto

```text
rut-solid-java/
├── lib/
│   └── sqlite-jdbc-3.50.3.0.jar
├── out/
├── src/
│   └── cl/
│       └── aiep/
│           └── rutvalidator/
│               ├── app/
│               │   └── Main.java
│               ├── domain/
│               │   ├── model/
│               │   │   ├── Rut.java
│               │   │   └── RutRecord.java
│               │   ├── ports/
│               │   │   ├── CheckDigitCalculator.java
│               │   │   ├── DocumentFormatter.java
│               │   │   ├── DocumentValidator.java
│               │   │   └── RutRecordRepository.java
│               │   └── service/
│               │       ├── RutGenerator.java
│               │       └── RutValidador.java
│               └── infrastructure/
│                   ├── calculator/
│                   │   └── Modulo11RutCalculator.java
│                   ├── database/
│                   │   └── DatabaseConnection.java
│                   ├── formatter/
│                   │   └── RutFormatter.java
│                   ├── parser/
│                   │   └── RutParser.java
│                   └── repository/
│                       ├── InMemoryRutRecordRepository.java
│                       └── SQLiteRutRecordRepository.java
├── .gitignore
└── README.md
```

## Descripción de componentes

### Main

Clase principal del programa. Se encarga de interactuar con el usuario por consola, mostrar el menú, crear las dependencias del sistema y ejecutar las opciones principales.

### Rut

Modelo que representa un RUT separado en número y dígito verificador.

### RutRecord

Modelo que representa un registro de operación realizado sobre un RUT, incluyendo número, dígito verificador, RUT completo y tipo de operación.

### CheckDigitCalculator

Interfaz que define el contrato para calcular un dígito verificador.

### DocumentFormatter

Interfaz que define el contrato para formatear o normalizar un documento.

### DocumentValidator

Interfaz que define el contrato general de validación.

### RutRecordRepository

Interfaz que define las operaciones de persistencia para guardar y recuperar registros de RUT.

### RutValidador

Servicio principal de validación del RUT. Coordina el flujo de formateo, parseo, cálculo, comparación y registro de la operación.

### RutGenerator

Servicio encargado de calcular el dígito verificador a partir del número del RUT, construir el RUT completo y registrar la operación.

### Modulo11RutCalculator

Implementación concreta del cálculo del dígito verificador mediante módulo 11.

### RutFormatter

Clase encargada de limpiar y normalizar el RUT ingresado por el usuario.

### RutParser

Clase encargada de separar la parte numérica y el dígito verificador.

### DatabaseConnection

Clase encargada de gestionar la conexión con la base de datos SQLite e inicializar la tabla necesaria para almacenar registros.

### InMemoryRutRecordRepository

Implementación en memoria del repositorio de registros, útil como alternativa temporal de persistencia.

### SQLiteRutRecordRepository

Implementación del repositorio de registros utilizando SQLite para almacenar la información en una base de datos real.

## Aplicación de principios SOLID

### 1. Single Responsibility Principle (SRP)

Cada clase tiene una sola responsabilidad:

- `RutFormatter` formatea la entrada
- `RutParser` separa número y dígito verificador
- `Modulo11RutCalculator` calcula el DV
- `RutValidador` valida un RUT completo
- `RutGenerator` genera el RUT completo desde un número
- `DatabaseConnection` gestiona la conexión a la base de datos
- `Main` maneja la interacción con el usuario

### 2. Open/Closed Principle (OCP)

El sistema puede extenderse agregando nuevas implementaciones sin modificar la lógica principal. Por ejemplo:

- otro algoritmo de cálculo implementando `CheckDigitCalculator`
- otro tipo de repositorio implementando `RutRecordRepository`
- nuevas búsquedas o filtros sobre los registros

### 3. Liskov Substitution Principle (LSP)

Cualquier implementación de `CheckDigitCalculator` o `RutRecordRepository` puede sustituir a otra mientras respete el contrato definido por la interfaz.

### 4. Interface Segregation Principle (ISP)

Las interfaces fueron diseñadas de manera pequeña y específica, evitando contratos excesivos o innecesarios.

### 5. Dependency Inversion Principle (DIP)

Las clases `RutValidador` y `RutGenerator` dependen de abstracciones (`CheckDigitCalculator`, `RutRecordRepository`, `DocumentFormatter`) y no de implementaciones concretas, lo que favorece el desacoplamiento del sistema.

## Persistencia de datos

La aplicación utiliza **SQLite** como base de datos local para guardar los registros generados por el sistema. Esto permite mantener un historial de operaciones incluso después de cerrar la aplicación.

Cada registro almacenado contiene información como:

- número del RUT
- dígito verificador
- RUT completo
- tipo de operación
- fecha y hora de creación

## Consultas implementadas

El sistema permite listar información con parámetros específicos, por ejemplo:

- buscar registros por tipo de operación:
  - `VALIDACION`
  - `GENERACION_DV`
- buscar registros por RUT completo

Además, la búsqueda por RUT completo fue normalizada para reconocer entradas con o sin puntos y guion.

## Requisitos

- Java JDK 17 o superior recomendado
- Visual Studio Code o cualquier IDE compatible con Java
- driver JDBC de SQLite dentro de la carpeta `lib`

## Compilación y ejecución

### Compilar desde PowerShell

```powershell
$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp "lib/sqlite-jdbc-3.50.3.0.jar" -d out $files
```

### Ejecutar

```powershell
java -cp "out;lib/sqlite-jdbc-3.50.3.0.jar" cl.aiep.rutvalidator.app.Main
```

## Ejemplos de uso

### Validar un RUT

```text
=== SISTEMA RUT CHILENO ===
1. Validar RUT
2. Calcular dígito verificador desde número de RUT
3. Listar registros guardados
4. Buscar registros por tipo de operación
5. Buscar registro por RUT completo
0. Salir
Seleccione una opción: 1
Ingrese un RUT completo (ej: 12.345.678-5): 74.761.650-7
Resultado: El RUT es válido.
```

### Calcular dígito verificador

```text
Seleccione una opción: 2
Ingrese solo el número del RUT, sin dígito verificador: 5101345
Dígito verificador calculado: K
RUT completo: 5101345-K
```

### Buscar por tipo de operación

```text
Seleccione una opción: 4
Ingrese el tipo de operación a buscar (VALIDACION o GENERACION_DV): GENERACION_DV
```

### Buscar por RUT completo

```text
Seleccione una opción: 5
Ingrese el RUT completo a buscar (ej: 12.345.678-5): 10.755.894-2
```

## Posibles mejoras futuras

- agregar pruebas unitarias con JUnit
- incorporar validación de otros documentos
- agregar interfaz gráfica
- generar reportes de validación
- incorporar más filtros de consulta
- migrar a una base de datos como MySQL o PostgreSQL
- aplicar patrón Decorator para agregar trazabilidad o logging

## Autor

**Roberto Emilio González Guzmán**  
Ingeniería Informática  
AIEP

## Contexto

Proyecto desarrollado con fines académicos para demostrar la implementación del algoritmo de validación y generación de RUT chileno en Java bajo una arquitectura basada en principios SOLID, incorporando persistencia de datos y consultas filtradas.
