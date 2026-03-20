# Validador de RUT chileno en Java aplicando principios SOLID

## Descripción

Este proyecto consiste en el desarrollo de un validador de RUT chileno implementado en Java, utilizando el algoritmo de módulo 11 para calcular y verificar el dígito verificador.

El objetivo principal del proyecto no es solo validar correctamente un RUT, sino también demostrar la aplicación práctica de los principios **SOLID** en una arquitectura orientada a objetos. Para ello, el sistema fue diseñado separando responsabilidades, utilizando interfaces específicas y desacoplando la lógica de validación de sus implementaciones concretas.

## Objetivo académico

Este trabajo fue desarrollado como ejercicio de aplicación de principios de diseño de software en el contexto de la asignatura **Taller de Ingeniería de Software**, con el propósito de demostrar:

- implementación de un algoritmo real en Java
- separación de responsabilidades
- uso de abstracciones mediante interfaces
- diseño orientado a objetos mantenible y extensible
- aplicación de los cinco principios SOLID

## Funcionalidad principal

El sistema permite:

- ingresar un RUT chileno en distintos formatos comunes
- limpiar y normalizar la entrada
- separar la parte numérica del dígito verificador
- calcular el dígito verificador correcto usando módulo 11
- comparar el valor calculado con el valor ingresado
- determinar si el RUT es válido o inválido

Ejemplos de entrada válidos para el sistema:

- `12.345.678-5`
- `12345678-5`
- `123456785`
- `12.345.678-k`

## Estructura del proyecto

```text
src/
└── cl/
    └── aiep/
        └── rutvalidator/
            ├── app/
            │   └── Main.java
            ├── domain/
            │   ├── model/
            │   │   └── Rut.java
            │   ├── ports/
            │   │   ├── CheckDigitCalculator.java
            │   │   ├── DocumentFormatter.java
            │   │   └── DocumentValidator.java
            │   └── service/
            │       └── RutValidador.java
            └── infrastructure/
                ├── calculator/
                │   └── Modulo11RutCalculator.java
                ├── formatter/
                │   └── RutFormatter.java
                └── parser/
                    └── RutParser.java
```

## Descripción de componentes

### Main

Clase principal del programa. Se encarga de interactuar con el usuario desde consola.

### Rut

Modelo que representa un RUT separado en número y dígito verificador.

### CheckDigitCalculator

Interfaz que define el contrato para calcular un dígito verificador.

### DocumentFormatter

Interfaz que define el contrato para formatear o normalizar un documento.

### DocumentValidator

Interfaz que define el contrato general de validación.

### RutValidador

Servicio principal de validación del RUT. Coordina el flujo de formateo, parseo, cálculo y comparación.

### Modulo11RutCalculator

Implementación concreta del cálculo del dígito verificador mediante módulo 11.

### RutFormatter

Clase encargada de limpiar el RUT ingresado por el usuario.

### RutParser

Clase encargada de separar la parte numérica y el dígito verificador.

## Aplicación de principios SOLID

### 1. Single Responsibility Principle (SRP)

Cada clase tiene una sola responsabilidad:

- `RutFormatter` formatea
- `RutParser` separa
- `Modulo11RutCalculator` calcula
- `RutValidador` valida
- `Main` maneja la interacción con el usuario

### 2. Open/Closed Principle (OCP)

El sistema puede extenderse agregando nuevas implementaciones sin modificar la lógica principal. Por ejemplo, se podría incorporar otro algoritmo de verificación implementando `CheckDigitCalculator`.

### 3. Liskov Substitution Principle (LSP)

Cualquier implementación de `CheckDigitCalculator` puede sustituir a otra mientras respete el contrato definido por la interfaz.

### 4. Interface Segregation Principle (ISP)

Las interfaces fueron diseñadas de manera pequeña y específica, evitando contratos excesivos o innecesarios.

### 5. Dependency Inversion Principle (DIP)

La clase `RutValidador` depende de abstracciones (`DocumentFormatter`, `CheckDigitCalculator`) y no de implementaciones concretas, lo que favorece el desacoplamiento del sistema.

## Requisitos

- Java JDK 17 o superior recomendado
- Visual Studio Code o cualquier IDE compatible con Java

## Compilación y ejecución

### Compilar desde PowerShell

```powershell
$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
```

### Ejecutar

```powershell
java -cp out cl.aiep.rutvalidator.app.Main
```

## Ejemplo de uso

```text
=== VALIDADOR DE RUT CHILENO ===
Ingrese un RUT (ej: 12.345.678-5): 12.345.678-5
Resultado: El RUT es válido.
```

## Posibles mejoras futuras

- agregar pruebas unitarias con JUnit
- incorporar validación de otros documentos
- agregar interfaz gráfica
- generar reportes de validación
- incorporar manejo de excepciones más detallado

## Autor

**Roberto Emilio González Guzmán**  
Ingeniería Informática  
AIEP

## Contexto

Proyecto desarrollado con fines académicos para demostrar la implementación del algoritmo de validación de RUT chileno en Java bajo una arquitectura basada en principios SOLID.
