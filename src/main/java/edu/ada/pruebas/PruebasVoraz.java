package edu.ada.pruebas;

import edu.ada.modelo.Agente;
import edu.ada.modelo.RedSocial;
import edu.ada.modelo.ResultadoModEx;
import edu.ada.nucleo.FuncionesComunes;
import edu.ada.nucleo.ModExFB;
import edu.ada.nucleo.ModExPD;
import edu.ada.nucleo.ModExV;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// pruebas del voraz: los dos ejemplos del enunciado y otras entradas.
// se compara con fuerza bruat y con programacion dinamica para ver si es optimo

public class PruebasVoraz {

    private static final double TOLERANCIA = 1e-9;
    private static final StringBuilder salida = new StringBuilder();
    private static int violaciones = 0;

    private static final List<String[]> filasEntradas = new ArrayList<>();
    private static final List<String[]> filasFB = new ArrayList<>();
    private static final List<String[]> filasPD = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        tablaEntradas();

        linea("Resultado de las verificaciones");
        linea("");
        if (violaciones == 0) {
            linea("Todas las verificaciones pasaron");
        } else {
            linea("VERIFICACION FALLIDA: " + violaciones + " violaciones");
        }
        linea("");

        if (args.length > 0) {
            Files.write(Paths.get(args[0]), salida.toString().getBytes(StandardCharsets.UTF_8));
            System.out.println("(Resultados guardados en " + args[0] + ")");
        }

        if (violaciones > 0) {
            System.exit(1);
        }
    }

    // imprime y ademas guarda la linea para el archivo
    private static void linea(String texto) {

        System.out.println(texto);
        salida.append(texto).append('\n');
    }

    // Locale.ROOT para que los decimales salgan con punto
    private static String decimales(double valor) {

        return String.format(Locale.ROOT, "%.4f", valor);
    }

    private static void verificar(boolean condicion, String mensaje) {

        if (!condicion) {
            violaciones++;
            linea("VIOLACION " + mensaje);
        }
    }

    private static boolean iguales(double a, double b) {

        double margen = TOLERANCIA * Math.max(1.0, Math.max(Math.abs(a), Math.abs(b)));

        return Math.abs(a - b) <= margen;
    }

    // el voraz nunca puede tener menos extremismo que el optimo
    private static boolean noMejorQue(double extVoraz, double extOptimo) {

        return extVoraz >= extOptimo - TOLERANCIA * Math.max(1.0, extOptimo);
    }

    private static String siONo(boolean valor) {

        if (valor) {
            return "Si";
        }

        return "NO";
    }

    private static Agente ag(int opinion, double receptividad, double influencia) {

        return new Agente(opinion, receptividad, influencia);
    }

    private static RedSocial crearRed(int rMax, Agente... agentes) {

        return new RedSocial(List.of(agentes), rMax);
    }

    // la estrategia sin espacios, por ejemplo [1,0,1]
    private static String texto(List<Integer> estrategia) {

        return estrategia.toString().replace(" ", "");
    }

    private static void imprimirTabla(String[] titulos, List<String[]> filas) {

        // cada columna mide lo que mida su celda mas larga
        int[] anchos = new int[titulos.length];

        for (int c = 0; c < titulos.length; c++) {
            anchos[c] = titulos[c].length();

            for (int f = 0; f < filas.size(); f++) {
                anchos[c] = Math.max(anchos[c], filas.get(f)[c].length());
            }
        }

        String[] guiones = new String[titulos.length];
        for (int c = 0; c < titulos.length; c++) {
            guiones[c] = "-".repeat(anchos[c]);
        }

        linea(armarFila(titulos, anchos));
        linea(armarFila(guiones, anchos));

        for (int f = 0; f < filas.size(); f++) {
            linea(armarFila(filas.get(f), anchos));
        }
    }

    // "%-5s" rellena con espacios a la derecha hasta 5 caracteres
    private static String armarFila(String[] celdas, int[] anchos) {

        String fila = "|";

        for (int c = 0; c < celdas.length; c++) {
            fila += " " + String.format("%-" + anchos[c] + "s", celdas[c]) + " |";
        }

        return fila;
    }

    private static void tablaEntradas() {

        linea("1. Ejemplos del enunciado y entradas adicionales");
        linea("");
        linea("Cada agente se escribe como (opinion, receptividad, influencia)");
        linea("");

        probarEntrada("Ejemplo 1", "(100,.5,1) (100,.1,1) (-10,.1,1); R=55",
                crearRed(55, ag(100, .5, 1), ag(100, .1, 1), ag(-10, .1, 1)));

        probarEntrada("Ejemplo 2", "(-30,.9,1) (20,.1,2) (50,.5,1); R=35",
                crearRed(35, ag(-30, .9, 1), ag(20, .1, 2), ag(50, .5, 1)));

        probarEntrada("Entrada 1", "(60,0,1) (50,0,1) (50,0,1); R=100",
                crearRed(100, ag(60, 0, 1), ag(50, 0, 1), ag(50, 0, 1)));

        probarEntrada("Entrada 2", "(30,.9,1) (100,0,1); R=100",
                crearRed(100, ag(30, .9, 1), ag(100, 0, 1)));

        probarEntrada("Entrada 3", "(100,1,1) (30,.9,1) (60,0,1); R=60",
                crearRed(60, ag(100, 1, 1), ag(30, .9, 1), ag(60, 0, 1)));

        probarEntrada("Entrada 4", "(60,0,1) (50,0,1) (10,0,1); R=70",
                crearRed(70, ag(60, 0, 1), ag(50, 0, 1), ag(10, 0, 1)));

        probarEntrada("Entrada 5", "(40,.5,2) (-70,.3,1) (90,.8,3) (10,.1,1); R=500",
                crearRed(500, ag(40, .5, 2), ag(-70, .3, 1), ag(90, .8, 3), ag(10, .1, 1)));

        probarEntrada("Entrada 6", "(90,1,3) (50,.5,1) (-70,1,1); R=0",
                crearRed(0, ag(90, 1, 3), ag(50, .5, 1), ag(-70, 1, 1)));

        probarEntrada("Entrada 7",
                "(80,.3,2) (-65,.6,1) (40,.2,3) (-95,.1,2) (25,.7,1) (55,.4,2) (-10,.5,1) (70,.8,3); R=150",
                crearRed(150, ag(80, .3, 2), ag(-65, .6, 1), ag(40, .2, 3), ag(-95, .1, 2),
                        ag(25, .7, 1), ag(55, .4, 2), ag(-10, .5, 1), ag(70, .8, 3)));

        probarEntrada("Entrada 8",
                "(90,.2,3) (-80,.4,2) (60,.1,1) (-45,.6,2) (35,.3,3) (95,.5,1) (-20,.9,2) (75,.0,2) (50,.7,1) (-100,.35,3); R=220",
                crearRed(220, ag(90, .2, 3), ag(-80, .4, 2), ag(60, .1, 1), ag(-45, .6, 2),
                        ag(35, .3, 3), ag(95, .5, 1), ag(-20, .9, 2), ag(75, .0, 2),
                        ag(50, .7, 1), ag(-100, .35, 3)));

        probarEntrada("Entrada 9", "(-100,.35,3) (95,.5,1) (-20,.9,2) (40,.5,1); R=220",
                crearRed(220, ag(-100, .35, 3), ag(95, .5, 1), ag(-20, .9, 2), ag(40, .5, 1)));

        linea("Tabla 1. Entradas");
        linea("");
        imprimirTabla(new String[]{"Caso", "Entrada"}, filasEntradas);
        linea("");

        linea("Tabla 2. Voraz vs fuerza bruta");
        linea("");
        imprimirTabla(new String[]{"Caso", "Voraz E", "Esf.", "Ext.", "Optimo (FB) E", "Esf.", "Ext.", "Voraz optimo?"}, filasFB);
        linea("");

        linea("Tabla 3. Voraz vs programacion dinamica");
        linea("");
        imprimirTabla(new String[]{"Caso", "Voraz E", "Esf.", "Ext.", "PD E", "Esf.", "Ext.", "Voraz optimo?"}, filasPD);

        linea("");
    }

    private static void probarEntrada(String nombre, String descripcion, RedSocial rs) {

        ResultadoModEx voraz = ModExV.modexV(rs);
        ResultadoModEx fb = ModExFB.modexFB(rs);
        ResultadoModEx pd = ModExPD.modexPD(rs);

        verificar(FuncionesComunes.es_aplicable(rs, voraz.getEstrategia()),
                nombre + ": voraz no aplicable");

        verificar(noMejorQue(voraz.getExtremismo(), fb.getExtremismo()),
                nombre + ": voraz mejor que el optimo");

        verificar(noMejorQue(voraz.getExtremismo(), pd.getExtremismo()),
                nombre + ": voraz mejor que PD");

        verificar(voraz.getEsfuerzo() == FuncionesComunes.esfuerzo(rs, voraz.getEstrategia())
                        && iguales(voraz.getExtremismo(), FuncionesComunes.ext(rs, voraz.getEstrategia())),
                nombre + ": valores reportados inconsistentes");

        filasEntradas.add(new String[]{nombre, descripcion});
        filasFB.add(crearFila(nombre, voraz, fb));
        filasPD.add(crearFila(nombre, voraz, pd));
    }

    // fila que compara el voraz con un algoritmo que da el optimo
    private static String[] crearFila(String nombre, ResultadoModEx voraz, ResultadoModEx optimo) {

        boolean esOptimo = iguales(voraz.getExtremismo(), optimo.getExtremismo());

        return new String[]{
                nombre,
                texto(voraz.getEstrategia()),
                String.valueOf(voraz.getEsfuerzo()),
                decimales(voraz.getExtremismo()),
                texto(optimo.getEstrategia()),
                String.valueOf(optimo.getEsfuerzo()),
                decimales(optimo.getExtremismo()),
                siONo(esOptimo)
        };
    }
}