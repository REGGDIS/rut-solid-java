// Define el contrato para cualquier validador de documentos.
package cl.aiep.rutvalidator.domain.ports;

public interface DocumentValidator {
    boolean isValid(String document);
}