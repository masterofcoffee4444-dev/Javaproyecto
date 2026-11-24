package excepciones;

public class PlacaInvalidaException extends ParqueaderoException {

    // el mensaje se arma aqui con la placa y el tipo de vehiculo
    public PlacaInvalidaException(String placa, String tipo) {
        super("La placa " + placa + " no es valida para un vehiculo " + tipo);
    }
}
