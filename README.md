 PROYECTO ModEx - Moderando el extremismo de opiniones en una red social
 Analisis y Diseno de Algoritmos II
 Escuela de Ingenieria de Sistemas y Computacion
 Profesor: Jesus Alexander Aranda
==============================================================================

Integrantes del grupo:
  - Valeria Zamudio Arevalo - 2415210
  - Samuel Banguero Ortega - 2418671
  - Juan Jose Rodriguez Lozano - 2419084
  - Manuela Martinez Moncada - 2375458


Repositorio: https://github.com/manumorita/proyecto-uno-ADA-II

------------------------------------------------------------------------------
1. DESCRIPCION GENERAL
------------------------------------------------------------------------------
Programa en Java que resuelve el problema ModEx (moderacion optima del
extremismo de una red social) con tres estrategias de diseno:

  - Fuerza bruta          -> funcion modexFB
  - Algoritmo voraz       -> funcion modexV
  - Programacion dinamica -> funcion modexPD

Cada funcion recibe una red social RS y devuelve la tripleta
(E, Esfuerzo(RS,E), Ext(Mod(RS,E))): la estrategia de moderacion, su esfuerzo
y el extremismo de la red ya moderada (clase ResultadoModEx).

El programa incluye una interfaz grafica (Swing) que permite leer entradas
desde archivo de texto, visualizarlas, ejecutar cualquiera de los tres
algoritmos (o compararlos), revisar la salida y guardarla en archivo, ademas
de generar casos de prueba.

------------------------------------------------------------------------------
2. REQUISITOS
------------------------------------------------------------------------------
  - Java JDK/JRE 21 o superior (el proyecto se compila con release 21).
  - (Solo si se desea recompilar) Apache Maven 3.8 o superior.
  - Un entorno con pantalla grafica (la interfaz usa Swing).

Verificar la version de Java con:   java -version

------------------------------------------------------------------------------
3. ESTRUCTURA DE ARCHIVOS ENTREGADOS
------------------------------------------------------------------------------
Readme.txt                      Este archivo.
Informe.pdf                     Informe del proyecto (analisis de complejidad,
                                correccion, comparacion y conclusiones).
pom.xml                         Configuracion de Maven (Java 21, jar ejecutable
                                con clase principal edu.ada.interfaz.AppModEx).
target/modex-1.0-SNAPSHOT.jar   EJECUTABLE listo para correr.
entradas_voraz/                 Casos de prueba en el formato de entrada
                                (ejemplo1, ejemplo2 del enunciado y
                                entrada1 ... entrada9 disenadas para el voraz).
src/main/java/edu/ada/          CODIGO FUENTE:

  Main.java                     Prueba rapida por consola: genera una red con
                                semilla y ejecuta FB, voraz y PD.

  modelo/
    Agente.java                 Agente <opinion, receptividad, influencia>.
    RedSocial.java              Red social <AG, R_max>.
    ResultadoModEx.java         Tripleta (estrategia, esfuerzo, extremismo).

  nucleo/
    FuncionesComunes.java       esfuerzo(), ext() y es_aplicable() segun las
                                definiciones del enunciado.
    ModExFB.java                modexFB: fuerza bruta (genera las 2^n
                                estrategias y escoge la mejor aplicable).
    ModExV.java                 modexV: voraz (ordena los agentes por la razon
                                beneficio/esfuerzo y los moderara mientras
                                alcance el presupuesto; compara ademas contra
                                la alternativa de iniciar con el mejor agente
                                individual y se queda con la mejor).
    ModExPD.java                modexPD: programacion dinamica tipo mochila
                                (tabla[j][c] y tabla de decisiones para
                                reconstruir la estrategia optima).

  interfaz/
    AppModEx.java               Clase principal de la interfaz grafica.
    VentanaPrincipal.java       Ventana: carga/visualizacion de entradas,
                                ejecucion, resultados, comparacion y archivo
                                de salida.
    LectorArchivo.java          Lectura de entradas desde archivo de texto.
    EscritorArchivo.java        Escritura de entradas y salidas en archivo.

  pruebas/
    GeneradorPruebas.java       Generador de redes aleatorias
                                (n, R_max, semilla).
    PruebasVoraz.java           Genera los archivos de entradas_voraz/.

------------------------------------------------------------------------------
4. INSTRUCCIONES DE EJECUCION
------------------------------------------------------------------------------
4.1 Ejecutar la aplicacion (interfaz grafica) con el jar incluido

    java -jar target/modex-1.0-SNAPSHOT.jar

    (Tambien funciona haciendo doble clic sobre el .jar en la mayoria de
    sistemas.)
    
4.2 Ejecutar desde un IDE con el boton "Run" (IntelliJ IDEA)
 
    1. Abrir IntelliJ IDEA -> File > Open... y seleccionar la carpeta del
       proyecto (la que contiene pom.xml). Aceptar "Open as Project" y
       esperar a que Maven termine de importar.
    2. Verificar que el SDK del proyecto sea Java 21
       (File > Project Structure > Project > SDK).
    3. Abrir el archivo:
         src/main/java/edu/ada/interfaz/AppModEx.java
    4. Dar clic en el boton verde de ejecutar (triangulo "Run") que aparece
       junto al metodo main (o en la barra superior), y elegir
       "Run 'AppModEx.main()'".
    5. Se abre la ventana de la aplicacion. Desde la segunda vez basta con
       dar clic en el boton Run de la barra superior.

------------------------------------------------------------------------------
5. USO DE LA INTERFAZ
------------------------------------------------------------------------------
Botones principales:

  Abrir entrada...   Lee un archivo de texto con una red social (formato 6.1)
                     y la muestra en la tabla de entrada.
  Generar caso...    Genera una red aleatoria indicando numero de agentes,
                     R_max y semilla (reproducible).
  Guardar entrada... Guarda la red cargada/generada en un archivo de texto.
  Ejecutar           Corre el algoritmo elegido en la lista desplegable
                     (Fuerza bruta, Voraz o Programacion dinamica).
  Comparar los 3     Corre los tres algoritmos sobre la misma red y los
                     compara (extremismo, esfuerzo, tiempo).
  Guardar salida...  Guarda la ultima salida calculada en archivo de texto
                     (formato 6.2).

Pestanas de resultados:

  Resultado          Estrategia obtenida, esfuerzo y extremismo, con el
                     desglose del esfuerzo por agente para poder comprobar a
                     mano la correccion del costo.
  Comparacion        Tabla con los resultados de los tres algoritmos.
  Archivo de salida  Vista previa del archivo plano que se escribira,
                     generada con el mismo metodo que lo escribe.

Nota: la fuerza bruta es O(2^n). La interfaz avisa cuando la red tiene mas de
20 agentes y deja que el usuario decida si continua o no.

------------------------------------------------------------------------------
6. FORMATO DE LOS ARCHIVOS
------------------------------------------------------------------------------
6.1 Entrada (archivo de texto de n + 2 lineas)

    n
    op0, recep0, inf0
    op1, recep1, inf1
    ...
    op(n-1), recep(n-1), inf(n-1)
    R_max

  - n        : numero de agentes (entero).
  - op       : opinion, entero entre -100 y 100.
  - recep    : receptividad, real entre 0 y 1.
  - inf      : influencia, real entre 1 y 3.
  - R_max    : esfuerzo maximo disponible (entero >= 0).

  Ejemplo (Ejemplo 1 del enunciado, entradas_voraz/ejemplo1.txt):

    3
    100, 0.5, 1.0
    100, 0.1, 1.0
    -10, 0.1, 1.0
    55

6.2 Salida (archivo de texto de n + 2 lineas)

    Ext
    Esf
    mod0
    mod1
    ...
    mod(n-1)

  - Ext  : extremismo de la red una vez moderada con la estrategia escogida.
  - Esf  : esfuerzo de esa estrategia (<= R_max).
  - mod_i: indica si el agente i fue moderado (1) o no (0).

