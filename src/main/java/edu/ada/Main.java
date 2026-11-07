package edu.ada;

import edu.ada.modelo.RedSocial;
import edu.ada.modelo.ResultadoModEx;
import edu.ada.pruebas.GeneradorPruebas;
// importamos los metodos desarrollados para pruebis
import edu.ada.nucleo.ModExFB;
import edu.ada.nucleo.ModExPD;
import edu.ada.nucleo.ModExV;


public class Main {

    public static void main(String[] args) {

        // generamos una red con semilla
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

        // ejecutamos fuerza bruta
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

        // ejecutamos programacio voraz
        ResultadoModEx resultadoVoraz = ModExV.modexV(rs);

        System.out.println("\n=== RESULTADO VORAZ ===");
        System.out.println(
                "Estrategia: " + resultadoVoraz.getEstrategia()
        );
        System.out.println(
                "Esfuerzo: " + resultadoVoraz.getEsfuerzo()
        );
        System.out.println(
                "Extremismo: " + resultadoVoraz.getExtremismo()
        );

        // ejecutamos programacion dinamica
        ResultadoModEx resultado2 = ModExPD.modexPD(rs);

        System.out.println("\n=== RESULTADO PROGRAMACIÓN DINÁMICA ===");
        System.out.println(
                "Estrategia: " + resultado2.getEstrategia()
        );
        System.out.println(
                "Esfuerzo: " + resultado2.getEsfuerzo()
        );
        System.out.println(
                "Extremismo: " + resultado2.getExtremismo()
        );
    }
}
