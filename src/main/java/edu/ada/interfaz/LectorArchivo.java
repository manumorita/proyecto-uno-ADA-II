package edu.ada.interfaz;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import edu.ada.modelo.Agente;
import edu.ada.modelo.RedSocial;

public class LectorArchivo {

    public static RedSocial leerEntrada(Path ruta) throws IOException {
        List<String> lineas = leerLineas(ruta);

        if (lineas.isEmpty()) {
            throw new IllegalArgumentException("El archivo esta vacio");
        }

        int n = aEntero(lineas.get(0), "n");

        if (n < 0) {
            throw new IllegalArgumentException("n no puede ser negativo");
        }

        if ((long) lineas.size() != (long) n + 2) {
            throw new IllegalArgumentException("Se esperaban " + ((long) n + 2)
                    + " lineas (n, " + n + " agentes y R_max) pero el archivo tiene "
                    + lineas.size());
        }

        List<Agente> agentes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String[] partes = lineas.get(i + 1).split("[,;\\s]+");

            if (partes.length != 3) {
                throw new IllegalArgumentException("Agente " + i
                        + ": se esperaban 3 valores y hay " + partes.length);
            }

            try {
                int opinion = aEntero(partes[0], "opinion");
                double receptividad = aNumero(partes[1], "receptividad");
                double influencia = aNumero(partes[2], "influencia");

                agentes.add(new Agente(opinion, receptividad, influencia));

            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Agente " + i + ": " + ex.getMessage());
            }
        }

        int rMax = aEntero(lineas.get(n + 1), "R_max");

        return new RedSocial(agentes, rMax);
    }

    private static List<String> leerLineas(Path ruta) throws IOException {
        byte[] bytes = Files.readAllBytes(ruta);
        String texto;

        boolean esUtf16 = bytes.length >= 2
                && (((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xFE)
                || ((bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xFF) == 0xFF));

        if (esUtf16) {
            texto = new String(bytes, StandardCharsets.UTF_16);
        } else {
            texto = new String(bytes, StandardCharsets.UTF_8).replace("\uFEFF", "");
        }

        List<String> lineas = new ArrayList<>();

        for (String linea : texto.split("\\R")) {
            linea = linea.trim();

            if (!linea.isEmpty()) {
                lineas.add(linea);
            }
        }

        return lineas;
    }

    private static double aNumero(String texto, String campo) {
        double valor;

        try {
            valor = Double.parseDouble(texto.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("'" + texto.trim()
                    + "' no es un numero valido para " + campo);
        }

        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new IllegalArgumentException("'" + texto.trim()
                    + "' no es un numero valido para " + campo);
        }

        return valor;
    }

    private static int aEntero(String texto, String campo) {
        double valor = aNumero(texto, campo);

        if (valor != Math.rint(valor)
                || valor < Integer.MIN_VALUE
                || valor > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("'" + texto.trim()
                    + "' debe ser un entero (" + campo + ")");
        }

        return (int) valor;
    }
}