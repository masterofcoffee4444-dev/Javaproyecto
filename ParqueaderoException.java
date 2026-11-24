package excepciones;

// excepcion padre de todas las del parqueadero
public class ParqueaderoException extends Exception {

    public ParqueaderoException(String mensaje) {
        super(mensaje);
    }
}
