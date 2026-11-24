package modelo;

public class Carro extends Vehiculo {

    private static final long TARIFA_MINUTO = 120;

    public Carro(String placa) {
        super(placa);
    }

    @Override
    public String getTipo() {
        return "CARRO";
    }

    // tres letras y tres digitos, ej: ABC123
    @Override
    public boolean placaValida() {
        String p = getPlaca();
        if (p == null || p.length() != 6) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            if (!esLetra(p.charAt(i))) {
                return false;
            }
        }
        for (int i = 3; i < 6; i++) {
            if (!esDigito(p.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long calcularTarifa(long minutos) {
        return minutos * TARIFA_MINUTO;
    }
}
