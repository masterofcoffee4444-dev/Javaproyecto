package presentacion;

import excepciones.ParqueaderoException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import modelo.Carro;
import modelo.CarroElectrico;
import modelo.Convenio;
import modelo.Moto;
import modelo.Tiquete;
import modelo.Vehiculo;
import negocio.IParqueadero;
import negocio.Parqueadero;
import persistencia.ManejoArchivos;

// unica clase que lee del teclado y escribe en pantalla
public class Consola {

    private IParqueadero sistema;
    private Scanner lector;
    private ManejoArchivos archivos;

    public Consola() {
        sistema = new Parqueadero();
        lector = new Scanner(System.in);
        archivos = new ManejoArchivos();
    }

    public static void main(String[] args) {
        Consola consola = new Consola();
        consola.iniciar();
    }

    public void iniciar() {
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerOpcion();
            switch (opcion) {
                case 1:
                    cargarMensualidades();
                    break;
                case 2:
                    procesarMovimientos();
                    break;
                case 3:
                    registrarEntrada();
                    break;
                case 4:
                    registrarSalida();
                    break;
                case 5:
                    mostrarLineas(sistema.reporteVehiculosAdentro(), "No hay vehiculos adentro");
                    break;
                case 6:
                    cierreDeCaja();
                    break;
                case 7:
                    mostrarLineas(sistema.reportePlacasFrecuentes(), "No hay tiquetes cerrados");
                    break;
                case 8:
                    guardarSistema();
                    break;
                case 9:
                    cargarSistema();
                    break;
                case 0:
                    System.out.println("Hasta luego");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("===== PARQUEADERO PLAZA CENTRAL =====");
        System.out.println("1. Cargar mensualidades");
        System.out.println("2. Procesar movimientos");
        System.out.println("3. Registrar una entrada");
        System.out.println("4. Registrar una salida");
        System.out.println("5. Ver los vehiculos adentro");
        System.out.println("6. Cierre de caja de un dia");
        System.out.println("7. Placas frecuentes");
        System.out.println("8. Guardar el sistema");
        System.out.println("9. Cargar el sistema");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
    }

    private int leerOpcion() {
        String texto = lector.nextLine().trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void cargarMensualidades() {
        System.out.print("Carpeta de los archivos: ");
        String carpeta = lector.nextLine().trim();
        try {
            mostrarLineas(archivos.cargarMensualidades(carpeta + "/Mensualidades.txt", sistema), "");
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private void procesarMovimientos() {
        System.out.print("Carpeta de los archivos: ");
        String carpeta = lector.nextLine().trim();
        try {
            mostrarLineas(archivos.procesarMovimientos(carpeta + "/Movimientos.txt", sistema), "El archivo no tiene movimientos");
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private void registrarEntrada() {
        System.out.print("Placa: ");
        String placa = lector.nextLine().trim();
        System.out.print("Tipo (CARRO, MOTO, ELECTRICO): ");
        String tipo = lector.nextLine().trim().toUpperCase();
        System.out.print("Fecha y hora (aaaa-mm-dd hh:mm): ");
        String fecha = lector.nextLine().trim();

        Vehiculo v;
        if (tipo.equals("CARRO")) {
            v = new Carro(placa);
        } else if (tipo.equals("MOTO")) {
            v = new Moto(placa);
        } else if (tipo.equals("ELECTRICO")) {
            v = new CarroElectrico(placa);
        } else {
            System.out.println("Tipo de vehiculo desconocido: " + tipo);
            return;
        }

        try {
            LocalDateTime entrada = LocalDateTime.parse(fecha.replace(' ', 'T'));
            Tiquete t = sistema.registrarEntrada(v, entrada);
            System.out.println(t);
        } catch (DateTimeParseException e) {
            System.out.println("Fecha u hora mal escrita: " + fecha);
        } catch (ParqueaderoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarSalida() {
        System.out.print("Placa: ");
        String placa = lector.nextLine().trim();
        System.out.print("Fecha y hora (aaaa-mm-dd hh:mm): ");
        String fecha = lector.nextLine().trim();
        System.out.print("Convenio (NINGUNO, ALMACEN, RESTAURANTE, CINE): ");
        String textoConvenio = lector.nextLine().trim().toUpperCase();

        Convenio convenio = null;
        for (Convenio c : Convenio.values()) {
            if (c.name().equals(textoConvenio)) {
                convenio = c;
            }
        }
        if (convenio == null) {
            System.out.println("Convenio desconocido: " + textoConvenio);
            return;
        }

        try {
            LocalDateTime salida = LocalDateTime.parse(fecha.replace(' ', 'T'));
            Tiquete t = sistema.registrarSalida(placa, salida, convenio);
            System.out.println(t);
        } catch (DateTimeParseException e) {
            System.out.println("Fecha u hora mal escrita: " + fecha);
        } catch (ParqueaderoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cierreDeCaja() {
        System.out.print("Fecha del cierre (aaaa-mm-dd): ");
        String texto = lector.nextLine().trim();
        try {
            LocalDate fecha = LocalDate.parse(texto);
            List<String> lineas = sistema.reporteCierre(fecha);
            mostrarLineas(lineas, "");
            String nombre = "Cierre_" + fecha + ".txt";
            archivos.exportarReporte(lineas, nombre);
            System.out.println("Exportado en " + nombre);
        } catch (DateTimeParseException e) {
            System.out.println("Fecha mal escrita: " + texto);
        } catch (IOException e) {
            System.out.println("No se pudo exportar: " + e.getMessage());
        }
    }

    private void guardarSistema() {
        try {
            archivos.guardarSistema(sistema, "sistema.dat");
            System.out.println("Sistema guardado en sistema.dat");
        } catch (IOException e) {
            System.out.println("No se pudo guardar: " + e.getMessage());
        }
    }

    private void cargarSistema() {
        try {
            sistema = archivos.cargarSistema("sistema.dat");
            System.out.println("Sistema recuperado de sistema.dat");
        } catch (IOException e) {
            System.out.println("No se pudo cargar: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("El archivo no es valido: " + e.getMessage());
        }
    }

    private void mostrarLineas(List<String> lineas, String siVacio) {
        if (lineas.isEmpty()) {
            System.out.println(siVacio);
        }
        for (String l : lineas) {
            System.out.println(l);
        }
    }
}
