/*flip flip creando el archivo del proyecto, a cameiar */
// importamos elementos necesarios para desarrollar el algoritmo
package edu.ada.nucleo;
import edu.ada.modelo.Agente;
import edu.ada.modelo.RedSocial;
import edu.ada.modelo.ResultadoModEx;
import java.util.ArrayList;
import java.util.List;

public class ModExPD {

    public static ResultadoModEx modexPD(RedSocial rs) {

        /* esta verificacion se hace al principio del desarrollo ya que antes daba error pues, 
        de ser rs nulo, al momento de llamar a getcantidadagntes() no funca. Igual solo es chicaneo XD*/ 
        // igual realmente desde el main siempre se crea la red antes de llamar entonces no es null jeje
        if (rs == null) {
            throw new IllegalArgumentException("OJO VIVO! La red social no puede ser nula");
        }

        // sacamos los datos de la red social en variables para un mejor manejo mas ordenado
        int n = rs.getCantidadAgentes();
        int presupuesto = rs.getRMax();

        int[] pesos = new int[n];
        double[] beneficios = new double[n];

        for (int i = 0; i < n; i++) {
            Agente agente = rs.getAgentes().get(i);
            pesos[i] = esfuerzoIndividual(agente);
            beneficios[i] = Math.pow(agente.getOpinion(), 2) * agente.getInfluencia();
        }

        // tabla[j][c]: mayor beneficio usando los primeros j agentes con presupuesto c
        double[][] tabla = new double[n + 1][presupuesto + 1];

        // decision[j][c]: true si en ese caso se modero al papuagente j
        boolean[][] decision = new boolean[n + 1][presupuesto + 1];

        llenarTabla(pesos, beneficios, tabla, decision);

        List<Integer> estrategia = reconstruirEstrategia(pesos, decision, presupuesto);

        return new ResultadoModEx(
                estrategia,
                FuncionesComunes.esfuerzo(rs, estrategia),
                FuncionesComunes.ext(rs, estrategia)
        );
    }


    private static void llenarTabla(
            int[] pesos,
            double[] beneficios,
            double[][] tabla,
            boolean[][] decision) {

        int n = pesos.length;
        int presupuesto = tabla[0].length - 1;

        for (int j = 1; j <= n; j++) {

            int peso = pesos[j - 1];
            double beneficio = beneficios[j - 1];

            for (int c = 0; c <= presupuesto; c++) {

                // primero se supone que no se modera al agente j
                tabla[j][c] = tabla[j - 1][c];

                if (peso <= c) {

                    double moderandolo = tabla[j - 1][c - peso] + beneficio;

                    // con empate se deja sin moderar
                    if (moderandolo > tabla[j][c]) {
                        tabla[j][c] = moderandolo;
                        decision[j][c] = true;
                    }
                }
            }
        }
    }


    //Se recorre desde el ultimo agente hasta el primero siguiendo las decisiones
    private static List<Integer> reconstruirEstrategia(
            int[] pesos,
            boolean[][] decision,
            int presupuesto) {

        int n = pesos.length;

        List<Integer> estrategia = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            estrategia.add(0);
        }

        int capacidad = presupuesto;

        for (int j = n; j >= 1; j--) {

            if (decision[j][capacidad]) {
                estrategia.set(j - 1, 1);
                capacidad -= pesos[j - 1];
            }
        }

        return estrategia;
    }


    //Mismo calculo que usa Funcionescomunes.esfuerxo, pero para un solo agente
    private static int esfuerzoIndividual(Agente agente) {

        RedSocial unitaria = new RedSocial(List.of(agente), 0);

        return FuncionesComunes.esfuerzo(unitaria, List.of(1));
    }
}