package edu.ada.pruebas;

import edu.ada.interfaz.EscritorArchivo;
import edu.ada.modelo.Agente;
import edu.ada.modelo.RedSocial;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PruebasVoraz {

    private static int archivosGenerados = 0;

    public static void main(String[] args) throws IOException {

        String carpeta = "entradas_voraz";

        if (args.length > 0) {
            carpeta = args[0];
        }

        Path destino = Paths.get(carpeta);
        Files.createDirectories(destino);

        generarEntradas(destino);

        System.out.println();
        System.out.println(archivosGenerados + " archivos generados en " + destino.toAbsolutePath());
    }

    private static Agente ag(int opinion, double receptividad, double influencia) {

        return new Agente(opinion, receptividad, influencia);
    }

    private static void escribir(Path carpeta, String nombre, int rMax, Agente... agentes) throws IOException {

        RedSocial red = new RedSocial(List.of(agentes), rMax);

        Path archivo = carpeta.resolve(nombre + ".txt");
        EscritorArchivo.escribirEntrada(red, archivo);

        archivosGenerados++;
        System.out.println("Generado " + archivo.getFileName());
    }

    private static void generarEntradas(Path c) throws IOException {

        escribir(c, "ejemplo1", 55, ag(100, .5, 1), ag(100, .1, 1), ag(-10, .1, 1));

        escribir(c, "ejemplo2", 35, ag(-30, .9, 1), ag(20, .1, 2), ag(50, .5, 1));

        escribir(c, "entrada1", 100, ag(60, 0, 1), ag(50, 0, 1), ag(50, 0, 1));

        escribir(c, "entrada2", 100, ag(30, .9, 1), ag(100, 0, 1));

        escribir(c, "entrada3", 60, ag(100, 1, 1), ag(30, .9, 1), ag(60, 0, 1));

        escribir(c, "entrada4", 70, ag(60, 0, 1), ag(50, 0, 1), ag(10, 0, 1));

        escribir(c, "entrada5", 500, ag(40, .5, 2), ag(-70, .3, 1), ag(90, .8, 3), ag(10, .1, 1));

        escribir(c, "entrada6", 0, ag(90, 1, 3), ag(50, .5, 1), ag(-70, 1, 1));

        escribir(c, "entrada7", 150, ag(80, .3, 2), ag(-65, .6, 1), ag(40, .2, 3), ag(-95, .1, 2),
                ag(25, .7, 1), ag(55, .4, 2), ag(-10, .5, 1), ag(70, .8, 3));

        escribir(c, "entrada8", 220, ag(90, .2, 3), ag(-80, .4, 2), ag(60, .1, 1), ag(-45, .6, 2),
                ag(35, .3, 3), ag(95, .5, 1), ag(-20, .9, 2), ag(75, .0, 2),
                ag(50, .7, 1), ag(-100, .35, 3));

        escribir(c, "entrada9", 220, ag(-100, .35, 3), ag(95, .5, 1), ag(-20, .9, 2), ag(40, .5, 1));
    }
}