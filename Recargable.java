package modelo;

public interface Recargable {

    // en una interfaz esto ya es public static final
    long COSTO_MINUTO = 50;

    long costoRecarga(long minutos);
}
