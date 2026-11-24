package excepciones;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FechaInvalidaException extends ParqueaderoException {

    // caso 1: una mensualidad que termina antes de empezar
    public FechaInvalidaException(String placa, LocalDate inicio, LocalDate fin) {
        super("La mensualidad de " + placa + " termina el " + fin
                + ", antes de su inicio el " + inicio);
    }

    // caso 2 (sobrecarga): una salida anterior a la entrada
    public FechaInvalidaException(String placa, LocalDateTime salida, LocalDateTime entrada) {
        super("La salida de " + placa + " (" + salida.toString().replace('T', ' ')
                + ") es anterior a su entrada (" + entrada.toString().replace('T', ' ') + ")");
    }
}
