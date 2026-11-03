package edu.ada.pruebas;

import edu.ada.modelo.Agente;
import edu.ada.modelo.RedSocial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneradorPruebas {

    public static RedSocial generarRed(
            int n,
            int rMax,
            long semilla) {

        if (n < 0) {
            throw new IllegalArgumentException(
                    "La cantidad de agentes no puede ser negativa"
            );
        }

        if (rMax < 0) {
            throw new IllegalArgumentException(
                    "El presupuesto no puede ser negativo"
            );
        }

        Random random = new Random(semilla);

        List<Agente> agentes = new ArrayList<>();

        for (int i = 0; i < n; i++) {

            // Opinión entera entre -100 y 100.
            int opinion = random.nextInt(201) - 100;

            // Receptividad real entre 0 y 1.
            double receptividad = random.nextDouble();

            // Influencia real entre 1 y 3.
            double influencia = 1 + 2 * random.nextDouble();

            Agente agente = new Agente(
                    opinion,
                    receptividad,
                    influencia
            );

            agentes.add(agente);
        }

        return new RedSocial(agentes, rMax);
    }
}
