// Define el contrato para cualquier clase que limpie o normalice un documento.
package cl.aiep.rutvalidator.domain.ports;

public interface DocumentFormatter {
    String format(String rawValue);
}