package negocio;

import excepciones.ParqueaderoException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import modelo.Convenio;
import modelo.Mensualidad;
import modelo.Tiquete;
import modelo.Vehiculo;

// fachada: la consola solo conoce esta interfaz
public interface IParqueadero {

    void agregarMensualidad(Mensualidad m) throws ParqueaderoException;

    Tiquete registrarEntrada(Vehiculo v, LocalDateTime entrada) throws ParqueaderoException;

    Tiquete registrarSalida(String placa, LocalDateTime salida, Convenio convenio) throws ParqueaderoException;

    List<String> reporteVehiculosAdentro();

    List<String> reporteCierre(LocalDate fecha);

    List<String> reportePlacasFrecuentes();

    List<Tiquete> getTiquetes();
}
