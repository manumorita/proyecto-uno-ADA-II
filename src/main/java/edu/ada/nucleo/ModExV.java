package edu.ada.nucleo;

import edu.ada.modelo.Agente;
import edu.ada.modelo.RedSocial;
import edu.ada.modelo.ResultadoModEx;

import java.util.ArrayList;
import java.util.List;


public class ModExV {

    // agente que vale la pena moderar, con sus datos ya calculados
    private static class Candidato {

        private final int indice;       // posicion en la red
        private final int peso;         // lo que cuesta moderarlo
        private final double beneficio; // lo que baja el extremismo si lo moderamos
        private final double razon;     // beneficio / peso

        public Candidato(int indice, int peso, double beneficio, double razon) {
            this.indice = indice;
            this.peso = peso;
            this.beneficio = beneficio;
            this.razon = razon;
        }

        public int getIndice() {
            return indice;
        }

        public int getPeso() {
            return peso;
        }

        public double getBeneficio() {
            return beneficio;
        }

        public double getRazon() {
            return razon;
        }
    }

    public static ResultadoModEx modexV(RedSocial rs) {

        if (rs == null) {
            throw new IllegalArgumentException("La red social no puede ser nula");
        }

        int n = rs.getCantidadAgentes();
        int presupuesto = rs.getRMax();

        List<Candidato> candidatos = crearCandidatos(rs);
        ordenarPorRazon(candidatos);

        boolean[] solucionVoraz = new boolean[n];
        double beneficioVoraz = completarPorRazon(candidatos, presupuesto, solucionVoraz);

        boolean[] solucionRescate = new boolean[n];
        double beneficioRescate = 0;

        Candidato mejor = mejorIndividual(candidatos);

        if (mejor != null) {
            solucionRescate[mejor.getIndice()] = true;
            beneficioRescate = mejor.getBeneficio() + completarPorRazon(candidatos, presupuesto - mejor.getPeso(), solucionRescate);
        }


        boolean[] elegida = solucionVoraz;
        if (beneficioRescate > beneficioVoraz) {
            elegida = solucionRescate;
        }

        return construirResultado(rs, elegida);
    }


    private static List<Candidato> crearCandidatos(RedSocial rs) {

        List<Candidato> candidatos = new ArrayList<>();
        List<Agente> agentes = rs.getAgentes();

        for (int i = 0; i < agentes.size(); i++) {

            Agente agente = agentes.get(i);

            int peso = esfuerzoIndividual(agente);
            double beneficio = beneficioIndividual(agente);

            if (beneficio <= 0 || peso > rs.getRMax()) {
                continue;
            }

            candidatos.add(new Candidato(i, peso, beneficio, razonDe(peso, beneficio)));
        }

        return candidatos;
    }


    private static double razonDe(int peso, double beneficio) {

        if (peso == 0) {
            return 1000000;
        }

        return beneficio / peso;
    }


    private static void ordenarPorRazon(List<Candidato> candidatos) {

        candidatos.sort((a, b) -> {

            if (a.getRazon() > b.getRazon()) {
                return -1;
            }
            if (a.getRazon() < b.getRazon()) {
                return 1;
            }

            if (a.getBeneficio() > b.getBeneficio()) {
                return -1;
            }
            if (a.getBeneficio() < b.getBeneficio()) {
                return 1;
            }

            if (a.getIndice() < b.getIndice()) {
                return -1;
            }
            if (a.getIndice() > b.getIndice()) {
                return 1;
            }

            return 0;
        });
    }


    private static double completarPorRazon(List<Candidato> candidatos, int presupuestoDisponible, boolean[] elegidos) {

        double beneficioAgregado = 0;

        for (int i = 0; i < candidatos.size(); i++) {

            Candidato c = candidatos.get(i);

            if (elegidos[c.getIndice()]) {
                continue;
            }

            if (c.getPeso() <= presupuestoDisponible) {
                elegidos[c.getIndice()] = true;
                presupuestoDisponible = presupuestoDisponible - c.getPeso();
                beneficioAgregado = beneficioAgregado + c.getBeneficio();
            }
        }

        return beneficioAgregado;
    }


    private static Candidato mejorIndividual(List<Candidato> candidatos) {

        Candidato mejor = null;

        for (int i = 0; i < candidatos.size(); i++) {

            Candidato c = candidatos.get(i);

            if (c.getPeso() == 0) {
                continue;
            }

            if (mejor == null || c.getBeneficio() > mejor.getBeneficio() || (c.getBeneficio() == mejor.getBeneficio() && c.getPeso() < mejor.getPeso())) {
                mejor = c;
            }
        }

        return mejor;
    }


    private static ResultadoModEx construirResultado(RedSocial rs, boolean[] elegidos) {

        List<Integer> estrategia = new ArrayList<>();

        for (int i = 0; i < elegidos.length; i++) {

            if (elegidos[i] == true) {
                estrategia.add(1);
            } else {
                estrategia.add(0);
            }
        }

        return new ResultadoModEx(estrategia, FuncionesComunes.esfuerzo(rs, estrategia), FuncionesComunes.ext(rs, estrategia));
    }


    private static int esfuerzoIndividual(Agente agente) {

        RedSocial unitaria = new RedSocial(List.of(agente), 0);

        return FuncionesComunes.esfuerzo(unitaria, List.of(1));
    }


    private static double beneficioIndividual(Agente agente) {

        return Math.pow(agente.getOpinion(), 2) * agente.getInfluencia();
    }
}