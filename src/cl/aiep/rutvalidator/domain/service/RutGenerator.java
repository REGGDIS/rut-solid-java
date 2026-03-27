// Servicio encargado de generar el dígito verificador de un número de RUT, construir el RUT completo y registrar la operación realizada.
package cl.aiep.rutvalidator.domain.service;

import cl.aiep.rutvalidator.domain.model.Rut;
import cl.aiep.rutvalidator.domain.model.RutRecord;
import cl.aiep.rutvalidator.domain.ports.CheckDigitCalculator;
import cl.aiep.rutvalidator.domain.ports.RutRecordRepository;

public class RutGenerator {

    public final CheckDigitCalculator calculator;
    private final RutRecordRepository repository;

    public RutGenerator(CheckDigitCalculator calculator, RutRecordRepository repository) {
        this.calculator = calculator;
        this.repository = repository;
    }

    public Rut generate(String number) {
        if (number == null || !number.matches("\\d+")) {
            throw new IllegalArgumentException("El número del RUT debe contener solo dígitos.");
        }

        String dv = calculator.calculate(number);
        Rut rut = new Rut(number, dv);

        repository.save(new RutRecord(
                rut.getNumber(),
                rut.getDv(),
                rut.getFullRut(),
                "GENERACION_DV",
                "DV=" + rut.getDv()));

        return rut;
    }
}
