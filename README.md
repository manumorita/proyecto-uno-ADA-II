# ModEx — Moderación del Extremismo de Opiniones en Redes Sociales

**Asignatura:** Análisis y Diseño de Algoritmos II  
**Escuela:** Escuela de Ingeniería de Sistemas y Computación  
**Repositorio:** [github.com/manumorita/proyecto-uno-ADA-II](https://github.com/manumorita/proyecto-uno-ADA-II)

---

## Integrantes del Grupo

| Nombre | Código |
| :--- | :--- |
| Valeria Zamudio Arévalo | 2415210 |
| Samuel Banguero Ortega | 2418671 |
| Juan José Rodríguez Lozano | 2419084 |
| Manuela Martínez Moncada | 2375458 |

---

## 1. Descripción General

Programa en Java que resuelve el problema **ModEx** (moderación óptima del extremismo en una red social) utilizando tres estrategias de diseño algorítmico:

* **Fuerza Bruta:** Función `modexFB` — Complejidad $O(2^n)$.
* **Algoritmo Voraz:** Función `modexV`.
* **Programación Dinámica:** Función `modexPD`.

Cada función recibe una red social $RS$ y devuelve la tripleta $(E, \text{Esfuerzo}(RS,E), \text{Ext}(\text{Mod}(RS,E)))$: la estrategia de moderación, su esfuerzo y el extremismo de la red ya moderada (instancia de la clase `ResultadoModEx`).

El programa incluye una interfaz gráfica construida con **Swing** que permite:
* Leer entradas desde archivos de texto y visualizarlas.
* Ejecutar cualquiera de los tres algoritmos o compararlos simultáneamente.
* Revisar la salida y exportarla a un archivo de texto.
* Generar casos de prueba aleatorios y reproducibles.

---

## 2. Requisitos del Sistema

* **Java JDK/JRE 21** o superior (compilado con *target/release* 21).
* **Apache Maven 3.8** o superior (requerido únicamente si se desea recompilar el código).
* Entorno con pantalla gráfica (la interfaz requiere soporte para Swing).

Para comprobar la versión instalada de Java, ejecute:

```bash
java -version
```

---

## 3. Estructura de la Entrega

La entrega se compone de cuatro elementos, empaquetados en un único archivo comprimido:

```text
.
├── Informe ADA II.pdf              # Informe del proyecto (complejidad, corrección, comparación y conclusiones).
├── Readme.txt                      # Descripción de los archivos entregados e instrucciones de ejecución.
├── ModExApp.jar                    # EJECUTABLE listo para correr.
└── CodigoFuente/                   # Código fuente del proyecto:
    ├── pom.xml                     # Configuración de Maven (Java 21, clase principal edu.ada.interfaz.AppModEx).
    ├── entradas_voraz/             # Casos de prueba (ejemplo1, ejemplo2 del enunciado y entrada1...entrada9).
    ├── pruebas/                    # Casos de prueba adicionales (prueba1...prueba9).
    └── src/main/java/edu/ada/
        ├── Main.java                   # Prueba rápida por consola con semilla aleatoria.
        ├── modelo/
        │   ├── Agente.java             # Estructura del agente: <opinión, receptividad, influencia>.
        │   ├── RedSocial.java          # Estructura de la red: <AG, R_max>.
        │   └── ResultadoModEx.java     # Tripleta: (estrategia, esfuerzo, extremismo).
        ├── nucleo/
        │   ├── FuncionesComunes.java   # Funciones de esfuerzo(), ext() y es_aplicable().
        │   ├── ModExFB.java            # Fuerza bruta: evalúa las 2^n estrategias posibles.
        │   ├── ModExV.java             # Algoritmo voraz: ordena agentes por razón beneficio/esfuerzo.
        │   └── ModExPD.java            # Programación dinámica tipo mochila (tabla dp y reconstrucción).
        ├── interfaz/
        │   ├── AppModEx.java           # Clase principal de la aplicación GUI.
        │   ├── VentanaPrincipal.java   # Ventana Swing con visualización y controles.
        │   ├── LectorArchivo.java      # Parser para lectura de archivos de entrada.
        │   └── EscritorArchivo.java    # Exportador de resultados a archivo plano.
        └── pruebas/
            ├── GeneradorPruebas.java   # Generador de redes aleatorias parametrizadas.
            └── PruebasVoraz.java       # Script generador de los casos en entradas_voraz/.
```

---

## 4. Instrucciones de Ejecución

### 4.1 Ejecutar la aplicación gráfica (`ModExApp.jar`)

Desde la carpeta donde se encuentra `ModExApp.jar`, ejecute:

```bash
java -jar ModExApp.jar
```

*(En la mayoría de sistemas operativos también puede ejecutarlo haciendo doble clic sobre el archivo `ModExApp.jar`).*

### 4.2 Ejecutar desde IntelliJ IDEA

1. Abra IntelliJ IDEA y seleccione **File > Open...**, eligiendo la carpeta `CodigoFuente` (donde se encuentra `pom.xml`). Confirme en **Open as Project** y espere a que Maven termine de importar.
2. Verifique que el SDK del proyecto esté configurado en **Java 21** (`File > Project Structure > Project > SDK`).
3. Navegue hasta el archivo:  
   `src/main/java/edu/ada/interfaz/AppModEx.java`
4. Haga clic en el botón verde de ejecución (**Run**) situado al lado del método `main`.
5. Se desplegará la interfaz gráfica de la aplicación.

### 4.3 Recompilar el ejecutable (opcional)

Desde la carpeta `CodigoFuente`:

```bash
mvn clean package
```

Maven genera el archivo `target/modex-1.0-SNAPSHOT.jar`, que es equivalente a `ModExApp.jar` (solo cambia el nombre) y se ejecuta con `java -jar`.

---

## 5. Formato de Archivos

### 5.1 Estructura del Archivo de Entrada (`.txt`)

El archivo consta de $n + 2$ líneas:

```text
n
op0, recep0, inf0
op1, recep1, inf1
...
op(n-1), recep(n-1), inf(n-1)
R_max
```

* $n$: Número de agentes (entero).
* $op$: Opinión del agente (entero entre $-100$ y $100$).
* $recep$: Receptividad (real entre $0.0$ y $1.0$).
* $inf$: Influencia (real entre $1.0$ y $3.0$).
* $R_{max}$: Esfuerzo máximo disponible (entero $\ge 0$).

#### Ejemplo (`CodigoFuente/entradas_voraz/ejemplo1.txt`):

```text
3
100, 0.5, 1.0
100, 0.1, 1.0
-10, 0.1, 1.0
55
```

### 5.2 Estructura del Archivo de Salida (`.txt`)

El archivo consta de $n + 2$ líneas:

```text
Ext
Esf
mod0
mod1
...
mod(n-1)
```

* **Ext:** Extremismo de la red social una vez moderada.
* **Esf:** Esfuerzo total utilizado por la estrategia ($\le R_{max}$).
* **mod_i:** Indicador binario si el agente $i$ fue moderado (`1`) o no (`0`).
