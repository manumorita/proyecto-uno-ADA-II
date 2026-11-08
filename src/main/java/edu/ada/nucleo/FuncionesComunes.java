package edu.ada.nucleo;

import edu.ada.modelo.Agente;
import edu.ada.modelo.RedSocial;

import java.util.List;

public class FuncionesComunes {

    public static int esfuerzo(RedSocial rs, List<Integer> e) {

        validarEstrategia(rs, e);

        int total = 0;

        for (int i = 0; i < rs.getCantidadAgentes(); i++) {

            if (e.get(i) == 1) {
                Agente agente = rs.getAgentes().get(i);

                double costo = Math.abs(agente.getOpinion())
                        * (1 - agente.getReceptividad())
                        * agente.getInfluencia();

                total += (int) Math.ceil(costo);
            }
        }

        return total;
    }

    public static double ext(RedSocial rs, List<Integer> e) {

        validarEstrategia(rs, e);

        int n = rs.getCantidadAgentes();

        if (n == 0) {
            return 0.0;
        }

        double suma = 0;

        for (int i = 0; i < n; i++) {

            Agente agente = rs.getAgentes().get(i);
            int opinionFinal = e.get(i) == 1 ? 0 : agente.getOpinion();

            suma += opinionFinal * opinionFinal * agente.getInfluencia();
        }

        return Math.sqrt(suma) / n;
    }

    public static boolean es_aplicable(RedSocial rs, List<Integer> e) {
        return esfuerzo(rs, e) <= rs.getRMax();
    }

    private static void validarEstrategia(RedSocial rs, List<Integer> e) {

        if (rs == null || e == null) {
            throw new IllegalArgumentException("La red y la estrategia no pueden ser nulas");
        }

        if (e.size() != rs.getCantidadAgentes()) {
            throw new IllegalArgumentException("La estrategia debe tener un valor por agente");
        }

        for (Integer valor : e) {
            if (valor == null || (valor != 0 && valor != 1)) {
                throw new IllegalArgumentException("La estrategia solo puede contener 0 y 1");
            }
        }
    }
}