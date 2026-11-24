package modelo;

import java.io.Serializable;

public abstract class Vehiculo implements Serializable {

    private String placa;

    public Vehiculo(String placa) {
        this.placa = placa;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    // cada hijo dice que tipo es y como es su placa
    public abstract String getTipo();

    public abstract boolean placaValida();

    public abstract long calcularTarifa(long minutos);

    // por defecto no hay cobro extra, solo el electrico lo cambia
    public long cargoAdicional(long minutos) {
        return 0;
    }

    // metodos de ayuda para revisar la placa letra por letra
    protected boolean esLetra(char c) {
        return c >= 'A' && c <= 'Z';
    }

    protected boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    @Override
    public String toString() {
        return getTipo() + " " + placa;
    }
}
