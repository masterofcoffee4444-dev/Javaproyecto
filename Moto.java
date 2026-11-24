package modelo;

public class Moto extends Vehiculo {

    private static final long TARIFA_MINUTO = 80;

    public Moto(String placa) {
        super(placa);
    }

    @Override
    public String getTipo() {
        return "MOTO";
    }

    // tres letras, dos digitos y una letra, ej: KLM45D
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
        if (!esDigito(p.charAt(3)) || !esDigito(p.charAt(4))) {
            return false;
        }
        return esLetra(p.charAt(5));
    }

    @Override
    public long calcularTarifa(long minutos) {
        return minutos * TARIFA_MINUTO;
    }
}
