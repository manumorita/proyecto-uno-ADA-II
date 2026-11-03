package edu.ada;

import edu.ada.modelo.RedSocial;
import edu.ada.modelo.ResultadoModEx;
import edu.ada.pruebas.GeneradorPruebas;
import edu.ada.nucleo.ModExFB;

public class Main {

    public static void main(String[] args) {

        // Generamos una red reproducible.
        RedSocial rs = GeneradorPruebas.generarRed(
                5,
                100,
                12345L
        );

        System.out.println("=== RED SOCIAL ===");
        System.out.println(
                "Agentes: " + rs.getCantidadAgentes()
        );
        System.out.println(
                "Presupuesto: " + rs.getRMax()
        );

        // Ejecutamos fuerza bruta.
        ResultadoModEx resultado = ModExFB.modexFB(rs);

        System.out.println("\n=== RESULTADO FUERZA BRUTA ===");
        System.out.println(
                "Estrategia: " + resultado.getEstrategia()
        );
        System.out.println(
                "Esfuerzo: " + resultado.getEsfuerzo()
        );
        System.out.println(
                "Extremismo: " + resultado.getExtremismo()
        );
    }
}
