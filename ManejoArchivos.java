package persistencia;

import excepciones.ParqueaderoException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import modelo.Carro;
import modelo.CarroElectrico;
import modelo.Convenio;
import modelo.Mensualidad;
import modelo.Moto;
import modelo.Tiquete;
import modelo.Vehiculo;
import negocio.IParqueadero;

public class ManejoArchivos {

    // devuelve los mensajes para que la consola los muestre
    public List<String> cargarMensualidades(String ruta, IParqueadero sistema) throws IOException {
        ArrayList<String> mensajes = new ArrayList<>();
        BufferedReader lector = new BufferedReader(new FileReader(ruta));
        String linea = lector.readLine();
        int numero = 1;
        int guardadas = 0;

        while (linea != null) {
            if (!ignorar(linea)) {
                try {
                    ArrayList<String> campos = separar(linea);
                    if (campos.size() != 3) {
                        throw new ParqueaderoException("Una mensualidad lleva tres campos");
                    }
                    LocalDate inicio = leerFecha(campos.get(1));
                    LocalDate fin = leerFecha(campos.get(2));
                    sistema.agregarMensualidad(new Mensualidad(campos.get(0), inicio, fin));
                    guardadas++;
                } catch (ParqueaderoException e) {
                    mensajes.add("Linea " + numero + ": " + e.getMessage());
                }
            }
            linea = lector.readLine();
            numero++;
        }
        lector.close();
        mensajes.add("Mensualidades guardadas: " + guardadas);
        return mensajes;
    }

    public List<String> procesarMovimientos(String ruta, IParqueadero sistema) throws IOException {
        ArrayList<String> mensajes = new ArrayList<>();
        BufferedReader lector = new BufferedReader(new FileReader(ruta));
        String linea = lector.readLine();
        int numero = 1;

        while (linea != null) {
            if (!ignorar(linea)) {
                try {
                    ArrayList<String> campos = separar(linea);
                    String movimiento = campos.get(0);

                    if (movimiento.equals("E")) {
                        if (campos.size() != 4) {
                            throw new ParqueaderoException("Una entrada lleva cuatro campos");
                        }
                        Vehiculo v = crearVehiculo(campos.get(2), campos.get(1));
                        LocalDateTime entrada = leerFechaHora(campos.get(3));
                        Tiquete t = sistema.registrarEntrada(v, entrada);
                        mensajes.add("Linea " + numero + ": tiquete " + t.getNumero() + " abierto para " + v.getPlaca());

                    } else if (movimiento.equals("S")) {
                        if (campos.size() != 4) {
                            throw new ParqueaderoException("Una salida lleva cuatro campos");
                        }
                        LocalDateTime salida = leerFechaHora(campos.get(2));
                        Convenio convenio = leerConvenio(campos.get(3));
                        Tiquete t = sistema.registrarSalida(campos.get(1), salida, convenio);
                        mensajes.add("Linea " + numero + ": tiquete " + t.getNumero() + " cerrado, total " + t.getTotal());

                    } else {
                        throw new ParqueaderoException("Movimiento desconocido: " + movimiento);
                    }
                } catch (ParqueaderoException e) {
                    mensajes.add("Linea " + numero + ": " + e.getMessage());
                }
            }
            linea = lector.readLine();
            numero++;
        }
        lector.close();
        return mensajes;
    }

    public void exportarReporte(List<String> lineas, String ruta) throws IOException {
        PrintWriter escritor = new PrintWriter(new FileWriter(ruta));
        for (String l : lineas) {
            escritor.println(l);
        }
        escritor.close();
    }

    public void guardarSistema(IParqueadero sistema, String ruta) throws IOException {
        ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ruta));
        salida.writeObject(sistema);
        salida.close();
    }

    public IParqueadero cargarSistema(String ruta) throws IOException, ClassNotFoundException {
        ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ruta));
        // readObject devuelve Object, se castea a la interfaz
        IParqueadero sistema = (IParqueadero) entrada.readObject();
        entrada.close();
        return sistema;
    }

    // ---------- metodos de ayuda ----------

    private boolean ignorar(String linea) {
        String limpia = linea.trim();
        return limpia.isEmpty() || limpia.charAt(0) == '#';
    }

    // parte la linea por cada * (igual que recorrer un string en C++)
    private ArrayList<String> separar(String linea) {
        ArrayList<String> campos = new ArrayList<>();
        String actual = "";
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '*') {
                campos.add(actual.trim());
                actual = "";
            } else {
                actual = actual + c;
            }
        }
        campos.add(actual.trim());
        return campos;
    }

    // aqui si se mira el tipo, pero solo para crear el objeto, no para cobrar
    private Vehiculo crearVehiculo(String tipo, String placa) throws ParqueaderoException {
        if (tipo.equals("CARRO")) {
            return new Carro(placa);
        } else if (tipo.equals("MOTO")) {
            return new Moto(placa);
        } else if (tipo.equals("ELECTRICO")) {
            return new CarroElectrico(placa);
        }
        throw new ParqueaderoException("Tipo de vehiculo desconocido: " + tipo);
    }

    private Convenio leerConvenio(String texto) throws ParqueaderoException {
        for (Convenio c : Convenio.values()) {
            if (c.name().equals(texto)) {
                return c;
            }
        }
        throw new ParqueaderoException("Convenio desconocido: " + texto);
    }

    private LocalDate leerFecha(String texto) throws ParqueaderoException {
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new ParqueaderoException("Fecha mal escrita: " + texto);
        }
    }

    // el archivo trae "2026-03-02 07:15" y LocalDateTime espera "2026-03-02T07:15"
    private LocalDateTime leerFechaHora(String texto) throws ParqueaderoException {
        try {
            return LocalDateTime.parse(texto.replace(' ', 'T'));
        } catch (DateTimeParseException e) {
            throw new ParqueaderoException("Fecha u hora mal escrita: " + texto);
        }
    }
}
