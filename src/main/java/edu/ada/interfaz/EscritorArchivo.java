package edu.ada.interfaz;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.ada.modelo.Agente;
import edu.ada.modelo.RedSocial;
import edu.ada.modelo.ResultadoModEx;

public class EscritorArchivo {

    /**
     * Devuelve la salida exactamente como la exige el enunciado:
     * 1) extremismo, 2) esfuerzo, 3) una línea por agente con 0 o 1.
     * No agrega encabezados ni información adicional.
     */
    public static String formatearSalida(ResultadoModEx resultado) {
        if (resultado == null) {
            throw new IllegalArgumentException("El resultado no puede ser nulo");
        }

        List<String> lineas = new ArrayList<>();
        lineas.add(String.format(Locale.ROOT, "%.4f", resultado.getExtremismo()));
        lineas.add(Integer.toString(resultado.getEsfuerzo()));

        for (Integer valor : resultado.getEstrategia()) {
            if (valor == null || (valor != 0 && valor != 1)) {
                throw new IllegalArgumentException("La estrategia solo puede contener 0 y 1");
            }
            lineas.add(Integer.toString(valor));
        }

        return String.join("\n", lineas) + "\n";
    }

    public static void escribirSalida(ResultadoModEx resultado, Path ruta) throws IOException {
        Files.writeString(ruta, formatearSalida(resultado), StandardCharsets.UTF_8);
    }

    public static void escribirEntrada(RedSocial red, Path ruta) throws IOException {
        if (red == null) {
            throw new IllegalArgumentException("La red social no puede ser nula");
        }

        List<String> lineas = new ArrayList<>();
        lineas.add(Integer.toString(red.getCantidadAgentes()));

        for (Agente agente : red.getAgentes()) {
            lineas.add(agente.getOpinion() + ", " + agente.getReceptividad() + ", " + agente.getInfluencia());
        }

        lineas.add(Integer.toString(red.getRMax()));
        Files.write(ruta, lineas, StandardCharsets.UTF_8);
    }
}
