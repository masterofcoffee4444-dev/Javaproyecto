package excepciones;

public class VehiculoYaAdentroException extends ParqueaderoException {

    // el mensaje se arma aqui con la placa
    public VehiculoYaAdentroException(String placa) {
        super("El vehiculo " + placa + " ya esta dentro del parqueadero");
    }
}
