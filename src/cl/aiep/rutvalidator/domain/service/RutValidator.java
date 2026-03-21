// Valida el RUT completo: formatea, parsea, verifica que el número tenga solo dígitos, calcula el DV correcto y lo compara con el DV ingresado.
package cl.aiep.rutvalidator.domain.service;

import cl.aiep.rutvalidator.domain.model.Rut;
import cl.aiep.rutvalidator.domain.model.RutRecord;
import cl.aiep.rutvalidator.domain.ports.CheckDigitCalculator;
import cl.aiep.rutvalidator.domain.ports.DocumentFormatter;
import cl.aiep.rutvalidator.domain.ports.DocumentValidator;
import cl.aiep.rutvalidator.domain.ports.RutRecordRepository;
import cl.aiep.rutvalidator.infrastructure.parser.RutParser;

public class RutValidator implements DocumentValidator {

    private final DocumentFormatter formatter;
    private final CheckDigitCalculator calculator;
    private final RutParser parser;
    private final RutRecordRepository repository;

    public RutValidator(
            DocumentFormatter formatter,
            CheckDigitCalculator calculator,
            RutParser parser,
            RutRecordRepository repository) {
        this.formatter = formatter;
        this.calculator = calculator;
        this.parser = parser;
        this.repository = repository;
    }

    @Override
    public boolean isValid(String document) {
        try {
            String formattedRut = formatter.format(document);

            if (formattedRut.length() < 2) {
                return false;
            }

            Rut rut = parser.parse(formattedRut);

            if (!rut.getNumber().matches("\\d+")) {
                return false;
            }

            String calculatedDv = calculator.calculate(rut.getNumber());
            boolean result = calculatedDv.equalsIgnoreCase(rut.getDv());

            repository.save(new RutRecord(
                    rut.getNumber(),
                    rut.getDv(),
                    rut.getFullRut(),
                    "VALIDACIÓN"));

            return result;
        } catch (Exception e) {
            return false;
        }
    }
}