package edu.ada.pruebas;

import edu.ada.modelo.Agente;
import edu.ada.modelo.RedSocial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneradorPruebas {

    public static RedSocial generarRed(int n, int rMax, long semilla) {

        if (n < 0) {
            throw new IllegalArgumentException("La cantidad de agentes no puede ser negativa");
        }

        if (rMax < 0) {
            throw new IllegalArgumentException("El presupuesto no puede ser negativo");
        }

        Random random = new Random(semilla);
        List<Agente> agentes = new ArrayList<>();

        for (int i = 0; i < n; i++) {

            int opinion = random.nextInt(201) - 100;
            double receptividad = random.nextDouble();
            double influencia = 1 + 2 * random.nextDouble();

            agentes.add(new Agente(opinion, receptividad, influencia));
        }

        return new RedSocial(agentes, rMax);
    }
}