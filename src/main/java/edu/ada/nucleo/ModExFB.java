package edu.ada.nucleo;

import edu.ada.modelo.RedSocial;
import edu.ada.modelo.ResultadoModEx;

import java.util.ArrayList;
import java.util.List;

public class ModExFB {

    public static ResultadoModEx modexFB(RedSocial rs) {

        if (rs == null) {
            throw new IllegalArgumentException("La red social no puede ser nula");
        }

        List<Integer> estrategia = new ArrayList<>();
        ResultadoModEx[] mejorResultado = new ResultadoModEx[1];

        generarEstrategias(rs, 0, estrategia, mejorResultado);

        return mejorResultado[0];
    }

    private static void generarEstrategias(
            RedSocial rs,
            int posicion,
            List<Integer> estrategia,
            ResultadoModEx[] mejorResultado) {

        if (posicion == rs.getCantidadAgentes()) {

            int esfuerzo = FuncionesComunes.esfuerzo(rs, estrategia);

            if (esfuerzo <= rs.getRMax()) {

                double extremismo = FuncionesComunes.ext(rs, estrategia);

                if (mejorResultado[0] == null ||
                        extremismo < mejorResultado[0].getExtremismo()) {

                    mejorResultado[0] = new ResultadoModEx(
                            estrategia,
                            esfuerzo,
                            extremismo
                    );
                }
            }

            return;
        }

        estrategia.add(0);
        generarEstrategias(rs, posicion + 1, estrategia, mejorResultado);
        estrategia.remove(estrategia.size() - 1);

        estrategia.add(1);
        generarEstrategias(rs, posicion + 1, estrategia, mejorResultado);
        estrategia.remove(estrategia.size() - 1);
    }
}