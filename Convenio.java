package modelo;

public enum Convenio {
    NINGUNO(0),
    ALMACEN(30),
    RESTAURANTE(60),
    CINE(120);

    private int minutosGratis;

    // el constructor de un enum es private
    private Convenio(int minutosGratis) {
        this.minutosGratis = minutosGratis;
    }

    public int getMinutosGratis() {
        return minutosGratis;
    }
}
