package edu.ada.modelo;

public class Agente {

    private final int opinion;
    private final double receptividad;
    private final double influencia;

    public Agente(int opinion, double receptividad, double influencia) {

        if (opinion < -100 || opinion > 100) {
            throw new IllegalArgumentException(
                    "La opinion debe estar entre -100 y 100"
            );
        }

        if (receptividad < 0 || receptividad > 1) {
            throw new IllegalArgumentException(
                    "La receptividad debe estar entre 0 y 1"
            );
        }

        if (influencia < 1 || influencia > 3) {
            throw new IllegalArgumentException(
                    "La influencia debe estar entre 1 y 3"
            );
        }

        this.opinion = opinion;
        this.receptividad = receptividad;
        this.influencia = influencia;
    }

    public int getOpinion() {
        return opinion;
    }

    public double getReceptividad() {
        return receptividad;
    }

    public double getInfluencia() {
        return influencia;
    }
}
