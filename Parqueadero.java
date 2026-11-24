package negocio;

import excepciones.FechaInvalidaException;
import excepciones.ParqueaderoException;
import excepciones.PlacaInvalidaException;
import excepciones.VehiculoNoEncontradoException;
import excepciones.VehiculoYaAdentroException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import modelo.Convenio;
import modelo.Mensualidad;
import modelo.Tiquete;
import modelo.Vehiculo;

public class Parqueadero implements IParqueadero, Serializable {

    // no es static para que se guarde al serializar
    private int siguienteNumero;
    private ArrayList<Mensualidad> mensualidades;
    private ArrayList<Tiquete> tiquetes;

    public Parqueadero() {
        siguienteNumero = 1;
        mensualidades = new ArrayList<>();
        tiquetes = new ArrayList<>();
    }

    @Override
    public void agregarMensualidad(Mensualidad m) throws ParqueaderoException {
        if (m.getFin().isBefore(m.getInicio())) {
            throw new FechaInvalidaException(m.getPlaca(), m.getInicio(), m.getFin());
        }
        mensualidades.add(m);
    }

    @Override
    public Tiquete registrarEntrada(Vehiculo v, LocalDateTime entrada) throws ParqueaderoException {
        // primero la placa, despues si ya esta adentro
        if (!v.placaValida()) {
            throw new PlacaInvalidaException(v.getPlaca(), v.getTipo());
        }
        if (buscarAbierto(v.getPlaca()) != null) {
            throw new VehiculoYaAdentroException(v.getPlaca());
        }
        // el numero solo sube si todo salio bien
        Tiquete t = new Tiquete(siguienteNumero, v, entrada);
        siguienteNumero++;
        tiquetes.add(t);
        return t;
    }

    @Override
    public Tiquete registrarSalida(String placa, LocalDateTime salida, Convenio convenio) throws ParqueaderoException {
        Tiquete t = buscarAbierto(placa);
        if (t == null) {
            throw new VehiculoNoEncontradoException(placa);
        }
        if (salida.isBefore(t.getEntrada())) {
            throw new FechaInvalidaException(placa, salida, t.getEntrada());
        }
        // la mensualidad se mira con el dia de la entrada
        boolean mensualidad = tieneMensualidad(placa, t.getEntrada().toLocalDate());
        t.cerrar(salida, convenio, mensualidad);
        return t;
    }

    @Override
    public List<String> reporteVehiculosAdentro() {
        ArrayList<Tiquete> abiertos = new ArrayList<>();
        for (Tiquete t : tiquetes) {
            if (t.estaAbierto()) {
                abiertos.add(t);
            }
        }

        // burbuja: primero por hora de entrada y si empatan por placa
        for (int i = 0; i < abiertos.size() - 1; i++) {
            for (int j = 0; j < abiertos.size() - 1 - i; j++) {
                Tiquete a = abiertos.get(j);
                Tiquete b = abiertos.get(j + 1);
                boolean cambiar = false;
                if (a.getEntrada().isAfter(b.getEntrada())) {
                    cambiar = true;
                } else if (a.getEntrada().isEqual(b.getEntrada())
                        && a.getVehiculo().getPlaca().compareTo(b.getVehiculo().getPlaca()) > 0) {
                    cambiar = true;
                }
                if (cambiar) {
                    abiertos.set(j, b);
                    abiertos.set(j + 1, a);
                }
            }
        }

        ArrayList<String> lineas = new ArrayList<>();
        for (Tiquete t : abiertos) {
            lineas.add(t.getNumero() + " " + t.getVehiculo().getPlaca() + " "
                    + t.getVehiculo().getTipo() + " " + textoFecha(t.getEntrada()));
        }
        return lineas;
    }

    @Override
    public List<String> reporteCierre(LocalDate fecha) {
        String[] tipos = {"CARRO", "MOTO", "ELECTRICO"};
        int[] cantidadPorTipo = new int[3];
        long[] totalPorTipo = new long[3];

        int cantidad = 0;
        long parqueo = 0;
        long recarga = 0;

        for (Tiquete t : tiquetes) {
            // solo los cerrados que salieron ese dia
            if (!t.estaAbierto() && t.getSalida().toLocalDate().isEqual(fecha)) {
                cantidad++;
                parqueo = parqueo + t.getValorParqueo();
                recarga = recarga + t.getValorRecarga();
                for (int i = 0; i < tipos.length; i++) {
                    if (tipos[i].equals(t.getVehiculo().getTipo())) {
                        cantidadPorTipo[i]++;
                        totalPorTipo[i] = totalPorTipo[i] + t.getTotal();
                    }
                }
            }
        }

        ArrayList<String> lineas = new ArrayList<>();
        lineas.add("CIERRE " + fecha);
        lineas.add("tiquetes=" + cantidad);
        lineas.add("parqueo=" + parqueo);
        lineas.add("recarga=" + recarga);
        lineas.add("total=" + (parqueo + recarga));
        for (int i = 0; i < tipos.length; i++) {
            lineas.add(tipos[i] + " tiquetes=" + cantidadPorTipo[i] + " total=" + totalPorTipo[i]);
        }
        return lineas;
    }

    @Override
    public List<String> reportePlacasFrecuentes() {
        // tres listas en paralelo: la placa, cuantas veces y cuanto pago
        ArrayList<String> placas = new ArrayList<>();
        ArrayList<Integer> visitas = new ArrayList<>();
        ArrayList<Long> pagado = new ArrayList<>();

        for (Tiquete t : tiquetes) {
            if (!t.estaAbierto()) {
                String placa = t.getVehiculo().getPlaca();
                int pos = placas.indexOf(placa);
                if (pos == -1) {
                    placas.add(placa);
                    visitas.add(1);
                    pagado.add(t.getTotal());
                } else {
                    visitas.set(pos, visitas.get(pos) + 1);
                    pagado.set(pos, pagado.get(pos) + t.getTotal());
                }
            }
        }

        // burbuja: mas visitas, si empatan mas pagado, si empatan placa menor
        for (int i = 0; i < placas.size() - 1; i++) {
            for (int j = 0; j < placas.size() - 1 - i; j++) {
                boolean cambiar = false;
                if (visitas.get(j) < visitas.get(j + 1)) {
                    cambiar = true;
                } else if (visitas.get(j).equals(visitas.get(j + 1))) {
                    if (pagado.get(j) < pagado.get(j + 1)) {
                        cambiar = true;
                    } else if (pagado.get(j).equals(pagado.get(j + 1))
                            && placas.get(j).compareTo(placas.get(j + 1)) > 0) {
                        cambiar = true;
                    }
                }
                if (cambiar) {
                    String auxPlaca = placas.get(j);
                    placas.set(j, placas.get(j + 1));
                    placas.set(j + 1, auxPlaca);

                    Integer auxVisitas = visitas.get(j);
                    visitas.set(j, visitas.get(j + 1));
                    visitas.set(j + 1, auxVisitas);

                    Long auxPagado = pagado.get(j);
                    pagado.set(j, pagado.get(j + 1));
                    pagado.set(j + 1, auxPagado);
                }
            }
        }

        ArrayList<String> lineas = new ArrayList<>();
        for (int i = 0; i < placas.size() && i < 3; i++) {
            lineas.add((i + 1) + ". " + placas.get(i) + " visitas=" + visitas.get(i)
                    + " pagado=" + pagado.get(i));
        }
        return lineas;
    }

    @Override
    public List<Tiquete> getTiquetes() {
        return tiquetes;
    }

    public ArrayList<Mensualidad> getMensualidades() {
        return mensualidades;
    }

    public int getSiguienteNumero() {
        return siguienteNumero;
    }

    private Tiquete buscarAbierto(String placa) {
        for (Tiquete t : tiquetes) {
            if (t.estaAbierto() && t.getVehiculo().getPlaca().equals(placa)) {
                return t;
            }
        }
        return null;
    }

    private boolean tieneMensualidad(String placa, LocalDate fecha) {
        for (Mensualidad m : mensualidades) {
            if (m.getPlaca().equals(placa) && m.cubre(fecha)) {
                return true;
            }
        }
        return false;
    }

    // LocalDateTime se imprime con una T en la mitad, aqui se cambia por espacio
    private String textoFecha(LocalDateTime fecha) {
        return fecha.toString().replace('T', ' ');
    }
}
