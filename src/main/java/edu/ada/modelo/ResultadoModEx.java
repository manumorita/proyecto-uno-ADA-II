package edu.ada.modelo;

import java.util.List;

public class ResultadoModEx {

    private final List<Integer> estrategia;
    private final int esfuerzo;
    private final double extremismo;

    public ResultadoModEx(List<Integer> estrategia, int esfuerzo, double extremismo) {

        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia no puede ser nula");
        }

        this.estrategia = List.copyOf(estrategia);
        this.esfuerzo = esfuerzo;
        this.extremismo = extremismo;
    }

    public List<Integer> getEstrategia() {
        return estrategia;
    }

    public int getEsfuerzo() {
        return esfuerzo;
    }

    public double getExtremismo() {
        return extremismo;
    }
}