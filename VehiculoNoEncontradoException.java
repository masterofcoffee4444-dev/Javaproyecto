package excepciones;

public class VehiculoNoEncontradoException extends ParqueaderoException {

    // el mensaje se arma aqui con la placa
    public VehiculoNoEncontradoException(String placa) {
        super("No hay un tiquete abierto para la placa " + placa);
    }
}
