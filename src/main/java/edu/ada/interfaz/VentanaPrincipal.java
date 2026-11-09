package edu.ada.interfaz;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import edu.ada.modelo.Agente;
import edu.ada.modelo.RedSocial;
import edu.ada.modelo.ResultadoModEx;
import edu.ada.nucleo.FuncionesComunes;
import edu.ada.nucleo.ModExFB;
import edu.ada.nucleo.ModExPD;
import edu.ada.nucleo.ModExV;
import edu.ada.pruebas.GeneradorPruebas;

public class VentanaPrincipal extends JFrame {

    // fuerza bruta es 2^n: con mas agentes que esto se avisa antes de correr (el usuario decide)
    private static final int MAX_AGENTES_FB = 20;

    private static final String[] NOMBRES = {"Fuerza bruta", "Voraz", "Programacion dinamica"};

    // ---------- paleta y fuentes ----------
    private static final Color FONDO = new Color(0xF1F5FB);
    private static final Color AZUL = new Color(0x2563EB);
    private static final Color AZUL_OSCURO = new Color(0x1E3A8A);
    private static final Color AZUL_SUAVE = new Color(0xDBEAFE);
    private static final Color AZUL_MUY_SUAVE = new Color(0xEFF6FF);
    private static final Color BORDE = new Color(0xE2E8F0);
    private static final Color TEXTO = new Color(0x1E293B);
    private static final Color TEXTO_SUAVE = new Color(0x64748B);
    private static final Color FILA_ALTERNA = new Color(0xF8FAFC);

    private static final int ARCO = 18;

    private static final Font FUENTE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_NEGRITA = new Font("Segoe UI", Font.BOLD, 13);
    // guarda lo que dio una corrida de un algoritmo
    private static class Ejecucion {
        String nombre;
        ResultadoModEx resultado;
        double milisegundos;
        String error;
        // corrida de PD usada como referencia de optimalidad (puede ser null)
        Ejecucion referencia;
    }

    // ---------- componentes con estilo propio ----------

    // panel con esquinas redondeadas
    private static class Tarjeta extends JPanel {

        private final Color relleno;
        private final Color borde;

        Tarjeta(LayoutManager layout) {
            this(layout, Color.WHITE, BORDE);
        }

        Tarjeta(LayoutManager layout, Color relleno, Color borde) {
            super(layout);
            this.relleno = relleno;
            this.borde = borde;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(relleno);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARCO, ARCO);
            g2.setColor(borde);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARCO, ARCO);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // boton con esquinas redondeadas
    private static class BotonRedondo extends JButton {

        enum Estilo {PRIMARIO, SECUNDARIO}

        private Estilo estilo;

        BotonRedondo(String texto, Estilo estilo) {
            super(texto);
            this.estilo = estilo;
            setFont(FUENTE_NEGRITA);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setBorder(new EmptyBorder(9, 16, 9, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setEstilo(Estilo nuevo) {
            estilo = nuevo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            ButtonModel modelo = getModel();
            Color fondo;
            Color texto;
            Color borde = null;

            if (estilo == Estilo.PRIMARIO) {
                texto = Color.WHITE;
                if (!isEnabled()) {
                    fondo = new Color(0xBFDBFE);
                } else if (modelo.isPressed()) {
                    fondo = new Color(0x1E40AF);
                } else if (modelo.isRollover()) {
                    fondo = new Color(0x1D4ED8);
                } else {
                    fondo = AZUL;
                }
            } else {
                if (!isEnabled()) {
                    fondo = new Color(0xF8FAFC);
                    texto = new Color(0xA3B1C6);
                    borde = BORDE;
                } else if (modelo.isPressed()) {
                    fondo = new Color(0xBFDBFE);
                    texto = AZUL;
                    borde = new Color(0x93C5FD);
                } else if (modelo.isRollover()) {
                    fondo = AZUL_MUY_SUAVE;
                    texto = AZUL;
                    borde = new Color(0x93C5FD);
                } else {
                    fondo = Color.WHITE;
                    texto = AZUL;
                    borde = new Color(0xBFDBFE);
                }
            }

            int arco = 14;
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arco, arco);

            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arco, arco);
            }

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(texto);
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    // borde redondeado de una sola linea (para el combo)
    private static class BordeRedondo extends AbstractBorder {

        private final Color color;
        private final int arco;
        private final Insets margenes;

        BordeRedondo(Color color, int arco, Insets margenes) {
            this.color = color;
            this.arco = arco;
            this.margenes = margenes;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int ancho, int alto) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, ancho - 1, alto - 1, arco, arco);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return margenes;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(margenes.top, margenes.left, margenes.bottom, margenes.right);
            return insets;
        }
    }

    // barras de desplazamiento delgadas y redondeadas
    private static class ScrollModerno extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(0xCBD5E1);
            trackColor = Color.WHITE;
        }

        @Override
        protected JButton createDecreaseButton(int orientacion) {
            return botonVacio();
        }

        @Override
        protected JButton createIncreaseButton(int orientacion) {
            return botonVacio();
        }

        private JButton botonVacio() {
            JButton boton = new JButton();
            boton.setPreferredSize(new Dimension(0, 0));
            boton.setMinimumSize(new Dimension(0, 0));
            boton.setMaximumSize(new Dimension(0, 0));
            return boton;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle area) {
            // sin pista visible
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle area) {

            if (area.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isThumbRollover()) {
                g2.setColor(new Color(0x94A3B8));
            } else {
                g2.setColor(thumbColor);
            }

            g2.fillRoundRect(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 8, 8);
            g2.dispose();
        }
    }

    // celdas con espacio interno y filas alternadas
    private static class CeldaRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor,
                boolean seleccionada, boolean foco, int fila, int columna) {

            super.getTableCellRendererComponent(tabla, valor, seleccionada, false, fila, columna);

            setBorder(new EmptyBorder(0, 10, 0, 10));

            if (seleccionada) {
                setForeground(AZUL_OSCURO);
            } else {
                setForeground(TEXTO);
                if (fila % 2 == 0) {
                    setBackground(Color.WHITE);
                } else {
                    setBackground(FILA_ALTERNA);
                }
            }

            return this;
        }
    }

    private static class EncabezadoRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor,
                boolean seleccionada, boolean foco, int fila, int columna) {

            super.getTableCellRendererComponent(tabla, valor, false, false, fila, columna);

            setOpaque(true);
            setBackground(AZUL_MUY_SUAVE);
            setForeground(AZUL_OSCURO);
            setFont(FUENTE_NEGRITA);
            setHorizontalAlignment(LEFT);
            setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, BORDE),
                    new EmptyBorder(9, 10, 9, 10)));

            return this;
        }
    }

    // ---------- estado ----------

    private RedSocial red;
    private int[] costos;
    private ResultadoModEx resultadoActual;
    private File carpetaActual = new File(".");

    private DefaultTableModel modeloEntrada = crearModelo(
            "i", "Opinion", "Receptividad", "Influencia", "Costo de moderar");
    private DefaultTableModel modeloSalida = crearModelo(
            "i", "Moderado", "Opinion final", "Costo aportado");
    private DefaultTableModel modeloComparacion = crearModelo(
            "Algoritmo", "Esfuerzo", "Aplicable", "Extremismo", "Tiempo (ms)", "Optimo?", "Estrategia");

    private JLabel lblRed = new JLabel("Sin entrada cargada");
    private JTextArea areaResumen = new JTextArea();
    private JTextArea areaSalidaArchivo = new JTextArea();
    private JComboBox<String> cmbAlgoritmo = new JComboBox<>(NOMBRES);
    private JCheckBox chkOptimo = new JCheckBox("Verificar contra el optimo (PD)");

    private CardLayout cartas = new CardLayout();
    private JPanel contenidoDerecho = new JPanel(cartas);
    private BotonRedondo btnTabResultado = new BotonRedondo("Resultado", BotonRedondo.Estilo.PRIMARIO);
    private BotonRedondo btnTabComparacion = new BotonRedondo("Comparacion", BotonRedondo.Estilo.SECUNDARIO);
    private BotonRedondo btnTabArchivo = new BotonRedondo("Archivo de salida", BotonRedondo.Estilo.SECUNDARIO);

    private BotonRedondo btnAbrir = new BotonRedondo("Abrir entrada...", BotonRedondo.Estilo.SECUNDARIO);
    private BotonRedondo btnGenerar = new BotonRedondo("Generar caso...", BotonRedondo.Estilo.SECUNDARIO);
    private BotonRedondo btnGuardarEntrada = new BotonRedondo("Guardar entrada...", BotonRedondo.Estilo.SECUNDARIO);
    private BotonRedondo btnEjecutar = new BotonRedondo("Ejecutar", BotonRedondo.Estilo.PRIMARIO);
    private BotonRedondo btnComparar = new BotonRedondo("Comparar los 3", BotonRedondo.Estilo.PRIMARIO);
    private BotonRedondo btnGuardarSalida = new BotonRedondo("Guardar salida...", BotonRedondo.Estilo.SECUNDARIO);

    public VentanaPrincipal() {

        super("ModEx - Moderando el extremismo en una red social");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 720);
        setMinimumSize(new Dimension(1100, 600));
        setLocationRelativeTo(null);

        construirInterfaz();
        btnTabComparacion.setEnabled(false);
        habilitarBotones(false);
    }

    // modelo de tabla donde no se pueden editar las celdas
    private static DefaultTableModel crearModelo(String... columnas) {

        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private static void estilizarTabla(JTable tabla) {

        tabla.setFont(FUENTE);
        tabla.setForeground(TEXTO);
        tabla.setBackground(Color.WHITE);
        tabla.setRowHeight(32);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(AZUL_SUAVE);
        tabla.setSelectionForeground(AZUL_OSCURO);
        tabla.setFillsViewportHeight(true);
        tabla.setDefaultRenderer(Object.class, new CeldaRenderer());

        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setDefaultRenderer(new EncabezadoRenderer());
    }

    private static JScrollPane crearScroll(Component contenido) {

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        scroll.getVerticalScrollBar().setUI(new ScrollModerno());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scroll.getHorizontalScrollBar().setUI(new ScrollModerno());
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));

        return scroll;
    }

    private static JComponent separador() {

        JPanel linea = new JPanel();
        linea.setBackground(BORDE);
        linea.setPreferredSize(new Dimension(1, 26));
        return linea;
    }

    private void estilizarCombo() {

        cmbAlgoritmo.setUI(new BasicComboBoxUI() {

            @Override
            protected JButton createArrowButton() {

                JButton flecha = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(AZUL);
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        g2.fillPolygon(new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                        g2.dispose();
                    }
                };

                flecha.setBorder(null);
                flecha.setContentAreaFilled(false);
                flecha.setFocusPainted(false);
                flecha.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return flecha;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle limites, boolean foco) {
                // sin fondo propio, se ve el de la tarjeta
            }
        });

        cmbAlgoritmo.setOpaque(false);
        cmbAlgoritmo.setFont(FUENTE);
        cmbAlgoritmo.setBorder(new BordeRedondo(new Color(0xBFDBFE), 14, new Insets(2, 10, 2, 2)));
        cmbAlgoritmo.setPreferredSize(new Dimension(190, 36));
        cmbAlgoritmo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cmbAlgoritmo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor,
                    int indice, boolean seleccionado, boolean foco) {

                JLabel etiqueta = (JLabel) super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, false);

                etiqueta.setFont(FUENTE);
                etiqueta.setBorder(new EmptyBorder(6, 10, 6, 10));

                if (indice == -1) {
                    etiqueta.setOpaque(false);
                    etiqueta.setForeground(TEXTO);
                } else if (seleccionado) {
                    etiqueta.setBackground(AZUL_SUAVE);
                    etiqueta.setForeground(AZUL_OSCURO);
                } else {
                    etiqueta.setBackground(Color.WHITE);
                    etiqueta.setForeground(TEXTO);
                }

                return etiqueta;
            }
        });
    }

    private void construirInterfaz() {

        getContentPane().setBackground(FONDO);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(16, 18, 18, 18));

        JPanel titulos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titulos.setOpaque(false);

        lblRed.setFont(FUENTE);
        lblRed.setForeground(TEXTO_SUAVE);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(titulos, BorderLayout.WEST);
        encabezado.add(lblRed, BorderLayout.EAST);

        // ----- barra de acciones -----
        estilizarCombo();

        JLabel lblAlgoritmo = new JLabel("Algoritmo");
        lblAlgoritmo.setFont(FUENTE_NEGRITA);
        lblAlgoritmo.setForeground(TEXTO_SUAVE);

        Tarjeta barra = new Tarjeta(new FlowLayout(FlowLayout.LEFT, 10, 10));
        barra.add(btnAbrir);
        barra.add(btnGenerar);
        barra.add(btnGuardarEntrada);
        barra.add(separador());
        barra.add(lblAlgoritmo);
        barra.add(cmbAlgoritmo);
        barra.add(btnEjecutar);
        barra.add(btnComparar);
        barra.add(separador());
        barra.add(btnGuardarSalida);

        JPanel norte = new JPanel(new BorderLayout(0, 12));
        norte.setOpaque(false);
        norte.setBorder(new EmptyBorder(0, 0, 14, 0));
        chkOptimo.setFont(FUENTE);
        chkOptimo.setForeground(TEXTO);
        chkOptimo.setOpaque(false);
        chkOptimo.setFocusPainted(false);
        chkOptimo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chkOptimo.setToolTipText("Al ejecutar fuerza bruta o voraz, corre tambien PD y compara el extremismo");

        JPanel filaSuperior = new JPanel(new BorderLayout());
        filaSuperior.setOpaque(false);
        filaSuperior.add(lblRed, BorderLayout.WEST);
        filaSuperior.add(chkOptimo, BorderLayout.EAST);

        norte.add(filaSuperior, BorderLayout.NORTH);
        norte.add(barra, BorderLayout.CENTER);

        // ----- tarjeta de entrada -----
        JTable tablaEntrada = new JTable(modeloEntrada);
        estilizarTabla(tablaEntrada);

        JLabel tituloEntrada = new JLabel("Entrada (agentes)");
        tituloEntrada.setFont(FUENTE_NEGRITA);
        tituloEntrada.setForeground(TEXTO);

        Tarjeta tarjetaEntrada = new Tarjeta(new BorderLayout(0, 10));
        tarjetaEntrada.setBorder(new EmptyBorder(14, 14, 14, 14));
        tarjetaEntrada.add(tituloEntrada, BorderLayout.NORTH);
        tarjetaEntrada.add(crearScroll(tablaEntrada), BorderLayout.CENTER);

        // ----- tarjeta de resultados -----
        JTable tablaSalida = new JTable(modeloSalida);
        estilizarTabla(tablaSalida);

        areaResumen.setEditable(false);
        areaResumen.setOpaque(false);
        areaResumen.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaResumen.setForeground(TEXTO);
        areaResumen.setMargin(new Insets(4, 6, 4, 6));

        Tarjeta panelResumen = new Tarjeta(new BorderLayout(), new Color(0xF1F5F9), BORDE);
        panelResumen.setBorder(new EmptyBorder(8, 8, 8, 8));
        panelResumen.setPreferredSize(new Dimension(100, 190));
        panelResumen.add(crearScroll(areaResumen), BorderLayout.CENTER);

        JPanel pestanaResultado = new JPanel(new BorderLayout(0, 10));
        pestanaResultado.setOpaque(false);
        pestanaResultado.add(crearScroll(tablaSalida), BorderLayout.CENTER);
        pestanaResultado.add(panelResumen, BorderLayout.SOUTH);

        JTable tablaComparacion = new JTable(modeloComparacion);
        estilizarTabla(tablaComparacion);

        // Vista previa del archivo plano exigido por el enunciado.
        areaSalidaArchivo.setEditable(false);
        areaSalidaArchivo.setFocusable(true);
        areaSalidaArchivo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        areaSalidaArchivo.setForeground(TEXTO);
        areaSalidaArchivo.setBackground(new Color(0xF8FAFC));
        areaSalidaArchivo.setMargin(new Insets(16, 16, 16, 16));
        areaSalidaArchivo.setText("Ejecuta un algoritmo para generar la salida.");

        JLabel tituloArchivo = new JLabel("Contenido exacto de salida.txt");
        tituloArchivo.setFont(FUENTE_NEGRITA);
        tituloArchivo.setForeground(TEXTO);

        JLabel ayudaArchivo = new JLabel("Línea 1: extremismo | Línea 2: esfuerzo | Siguientes n líneas: estrategia (1 = moderado, 0 = no moderado)");
        ayudaArchivo.setFont(FUENTE);
        ayudaArchivo.setForeground(TEXTO_SUAVE);

        JPanel encabezadoArchivo = new JPanel(new BorderLayout(0, 6));
        encabezadoArchivo.setOpaque(false);
        encabezadoArchivo.add(tituloArchivo, BorderLayout.NORTH);
        encabezadoArchivo.add(ayudaArchivo, BorderLayout.SOUTH);

        Tarjeta tarjetaArchivo = new Tarjeta(new BorderLayout(0, 12));
        tarjetaArchivo.setBorder(new EmptyBorder(18, 18, 18, 18));
        tarjetaArchivo.add(encabezadoArchivo, BorderLayout.NORTH);
        tarjetaArchivo.add(crearScroll(areaSalidaArchivo), BorderLayout.CENTER);

        contenidoDerecho.setOpaque(false);
        contenidoDerecho.add(pestanaResultado, "res");
        contenidoDerecho.add(crearScroll(tablaComparacion), "cmp");
        contenidoDerecho.add(tarjetaArchivo, "archivo");

        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selector.setOpaque(false);
        selector.add(btnTabResultado);
        selector.add(btnTabComparacion);
        selector.add(btnTabArchivo);

        Tarjeta tarjetaDerecha = new Tarjeta(new BorderLayout(0, 12));
        tarjetaDerecha.setBorder(new EmptyBorder(14, 14, 14, 14));
        tarjetaDerecha.add(selector, BorderLayout.NORTH);
        tarjetaDerecha.add(contenidoDerecho, BorderLayout.CENTER);

        // entrada a la izquierda, resultado y comparacion a la derecha
        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tarjetaEntrada, tarjetaDerecha);
        division.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(FONDO);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        });
        division.setBorder(null);
        division.setOpaque(false);
        division.setDividerSize(14);
        division.setContinuousLayout(true);
        division.setResizeWeight(0.4);
        division.setDividerLocation(500);

        add(norte, BorderLayout.NORTH);
        add(division, BorderLayout.CENTER);

        btnAbrir.addActionListener(e -> abrirEntrada());
        btnGenerar.addActionListener(e -> generarCaso());
        btnGuardarEntrada.addActionListener(e -> guardarEntrada());
        btnEjecutar.addActionListener(e -> ejecutarSeleccionado());
        btnComparar.addActionListener(e -> compararTodos());
        btnGuardarSalida.addActionListener(e -> guardarSalida());
        btnTabResultado.addActionListener(e -> mostrarPestana(0));
        btnTabComparacion.addActionListener(e -> mostrarPestana(1));
        btnTabArchivo.addActionListener(e -> mostrarPestana(2));
    }

    // 0 = resultado, 1 = comparacion
    private void mostrarPestana(int indice) {

        if (indice == 0) {
            cartas.show(contenidoDerecho, "res");
        } else if (indice == 1) {
            cartas.show(contenidoDerecho, "cmp");
        } else {
            cartas.show(contenidoDerecho, "archivo");
        }

        btnTabResultado.setEstilo(indice == 0
                ? BotonRedondo.Estilo.PRIMARIO : BotonRedondo.Estilo.SECUNDARIO);
        btnTabComparacion.setEstilo(indice == 1
                ? BotonRedondo.Estilo.PRIMARIO : BotonRedondo.Estilo.SECUNDARIO);
        btnTabArchivo.setEstilo(indice == 2
                ? BotonRedondo.Estilo.PRIMARIO : BotonRedondo.Estilo.SECUNDARIO);
    }

    // mientras corre un algoritmo se desactivan los botones
    private void habilitarBotones(boolean ocupado) {

        boolean hayRed = red != null;

        btnAbrir.setEnabled(!ocupado);
        btnGenerar.setEnabled(!ocupado);
        btnGuardarEntrada.setEnabled(!ocupado && hayRed);
        cmbAlgoritmo.setEnabled(!ocupado && hayRed);
        chkOptimo.setEnabled(!ocupado && hayRed);
        btnEjecutar.setEnabled(!ocupado && hayRed);
        btnComparar.setEnabled(!ocupado && hayRed);
        btnGuardarSalida.setEnabled(!ocupado && resultadoActual != null);
    }

    private void abrirEntrada() {

        JFileChooser selector = new JFileChooser(carpetaActual);
        selector.setFileFilter(new FileNameExtensionFilter("Texto (*.txt)", "txt"));

        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();
        carpetaActual = archivo.getParentFile();

        try {
            RedSocial nueva = LectorArchivo.leerEntrada(archivo.toPath());
            cargarRed(nueva, archivo.getName());
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo leer la entrada:\n" + ex.getMessage(),
                    "Entrada invalida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarCaso() {

        JTextField campoN = new JTextField("10", 8);
        JTextField campoR = new JTextField("100", 8);
        JTextField campoSemilla = new JTextField("12345", 8);

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Agentes (n):"));
        panel.add(campoN);
        panel.add(new JLabel("Presupuesto R_max:"));
        panel.add(campoR);
        panel.add(new JLabel("Semilla:"));
        panel.add(campoSemilla);

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Generar caso de prueba",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int n = Integer.parseInt(campoN.getText().trim());
            int rMax = Integer.parseInt(campoR.getText().trim());
            long semilla = Long.parseLong(campoSemilla.getText().trim());

            RedSocial nueva = GeneradorPruebas.generarRed(n, rMax, semilla);
            cargarRed(nueva, "caso generado (semilla " + semilla + ")");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "n, R_max y la semilla deben ser enteros",
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos invalidos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarRed(RedSocial nueva, String origen) {

        red = nueva;
        resultadoActual = null;
        costos = new int[red.getCantidadAgentes()];

        modeloEntrada.setRowCount(0);

        for (int i = 0; i < red.getCantidadAgentes(); i++) {

            Agente a = red.getAgentes().get(i);

            // el costo de un agente se calcula con una red de un solo agente
            RedSocial unAgente = new RedSocial(List.of(a), 0);
            costos[i] = FuncionesComunes.esfuerzo(unAgente, List.of(1));

            modeloEntrada.addRow(new Object[]{
                    i, a.getOpinion(), a.getReceptividad(), a.getInfluencia(), costos[i]});
        }

        lblRed.setText("Entrada: " + origen + "   |   n = " + red.getCantidadAgentes()
                + "   |   R_max = " + red.getRMax());

        modeloSalida.setRowCount(0);
        modeloComparacion.setRowCount(0);
        areaResumen.setText("");
        areaSalidaArchivo.setText("Ejecuta un algoritmo para generar la salida.");
        habilitarBotones(false);
    }

    private void guardarEntrada() {

        File destino = elegirArchivoParaGuardar("entrada.txt");

        if (destino == null) {
            return;
        }

        try {
            EscritorArchivo.escribirEntrada(red, destino.toPath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarSalida() {

        File destino = elegirArchivoParaGuardar("salida.txt");

        if (destino == null) {
            return;
        }

        try {
            EscritorArchivo.escribirSalida(resultadoActual, destino.toPath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private File elegirArchivoParaGuardar(String nombreSugerido) {

        JFileChooser selector = new JFileChooser(carpetaActual);
        selector.setSelectedFile(new File(carpetaActual, nombreSugerido));

        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File archivo = selector.getSelectedFile();
        carpetaActual = archivo.getParentFile();

        if (archivo.exists()) {
            int opcion = JOptionPane.showConfirmDialog(this, "El archivo ya existe. Sobrescribir?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);

            if (opcion != JOptionPane.YES_OPTION) {
                return null;
            }
        }

        return archivo;
    }

    // corre un algoritmo (0 = FB, 1 = voraz, 2 = PD) y mide el tiempo
    private static Ejecucion correr(int indice, RedSocial rs) {

        Ejecucion ejecucion = new Ejecucion();
        ejecucion.nombre = NOMBRES[indice];

        long inicio = System.nanoTime();

        try {
            if (indice == 0) {
                ejecucion.resultado = ModExFB.modexFB(rs);
            } else if (indice == 1) {
                ejecucion.resultado = ModExV.modexV(rs);
            } else {
                ejecucion.resultado = ModExPD.modexPD(rs);
            }
        } catch (OutOfMemoryError ex) {
            ejecucion.error = "memoria insuficiente (la tabla de PD es de n x R_max)";
        } catch (Exception ex) {
            ejecucion.error = String.valueOf(ex.getMessage());
        }

        ejecucion.milisegundos = (System.nanoTime() - inicio) / 1e6;

        return ejecucion;
    }

    private void ejecutarSeleccionado() {

        int indice = cmbAlgoritmo.getSelectedIndex();

        if (indice == 0 && red.getCantidadAgentes() > MAX_AGENTES_FB) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "Fuerza bruta es O(2^n) y con n = " + red.getCantidadAgentes()
                            + " puede tardar muchisimo (la ventana quedara ocupada).\nContinuar?",
                    "Entrada grande", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        RedSocial rs = red;
        boolean verificar = chkOptimo.isSelected() && indice != 2;
        habilitarBotones(true);

        // se corre en otro hilo para que la ventana no se quede congelada
        new SwingWorker<Ejecucion, Void>() {
            @Override
            protected Ejecucion doInBackground() {

                Ejecucion ejecucion = correr(indice, rs);

                // PD es exacta: sirve de referencia para decir si el resultado es optimo
                if (verificar) {
                    ejecucion.referencia = correr(2, rs);
                }

                return ejecucion;
            }

            @Override
            protected void done() {
                try {
                    Ejecucion ejecucion = get();

                    if (ejecucion.error != null) {
                        JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                ejecucion.nombre + " fallo: " + ejecucion.error,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        mostrarResultado(ejecucion);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

                habilitarBotones(false);
            }
        }.execute();
    }

    private void compararTodos() {

        boolean incluirFB = true;

        if (red.getCantidadAgentes() > MAX_AGENTES_FB) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "Fuerza bruta es O(2^n) y con n = " + red.getCantidadAgentes()
                            + " puede tardar muchisimo.\nSi = incluir fuerza bruta\nNo = omitirla y comparar solo voraz y PD",
                    "Entrada grande", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if (opcion == JOptionPane.CANCEL_OPTION || opcion == JOptionPane.CLOSED_OPTION) {
                return;
            }

            incluirFB = opcion == JOptionPane.YES_OPTION;
        }

        RedSocial rs = red;
        boolean conFB = incluirFB;
        habilitarBotones(true);

        new SwingWorker<List<Ejecucion>, Void>() {
            @Override
            protected List<Ejecucion> doInBackground() {

                List<Ejecucion> ejecuciones = new ArrayList<>();

                for (int i = 0; i < NOMBRES.length; i++) {

                    if (i == 0 && !conFB) {
                        Ejecucion omitida = new Ejecucion();
                        omitida.nombre = NOMBRES[0];
                        omitida.error = "omitida por el usuario (n = " + rs.getCantidadAgentes() + ")";
                        ejecuciones.add(omitida);
                    } else {
                        ejecuciones.add(correr(i, rs));
                    }
                }

                return ejecuciones;
            }

            @Override
            protected void done() {
                try {
                    mostrarComparacion(get());
                    btnTabComparacion.setEnabled(true); 
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

                habilitarBotones(false);
            }
        }.execute();
    }

    private void mostrarResultado(Ejecucion ejecucion) {

        ResultadoModEx resultado = ejecucion.resultado;
        resultadoActual = resultado;

        modeloSalida.setRowCount(0);

        for (int i = 0; i < red.getCantidadAgentes(); i++) {

            boolean moderado = resultado.getEstrategia().get(i) == 1;
            Agente a = red.getAgentes().get(i);

            if (moderado) {
                modeloSalida.addRow(new Object[]{i, "Si", 0, costos[i]});
            } else {
                modeloSalida.addRow(new Object[]{i, "No", a.getOpinion(), 0});
            }
        }

        areaResumen.setText(explicar(ejecucion));
        areaResumen.setCaretPosition(0);

        // La vista previa se genera con el mismo método que escribe el archivo.
        areaSalidaArchivo.setText(EscritorArchivo.formatearSalida(resultado));
        areaSalidaArchivo.setCaretPosition(0);
        mostrarPestana(0);
    }

    // Desglosa el resultado usando los mismos costos que utiliza el algoritmo.
    private String explicar(Ejecucion ejecucion) {

        ResultadoModEx resultado = ejecucion.resultado;
        List<Integer> estrategia = resultado.getEstrategia();
        int n = red.getCantidadAgentes();

        List<String> sumandos = new ArrayList<>();
        int esfuerzoCalculado = 0;
        double suma = 0;

        for (int i = 0; i < n; i++) {
            Agente agente = red.getAgentes().get(i);
            int opinionFinal = estrategia.get(i) == 1 ? 0 : agente.getOpinion();

            if (estrategia.get(i) == 1) {
                sumandos.add(Integer.toString(costos[i]));
                esfuerzoCalculado += costos[i];
            }

            suma += opinionFinal * opinionFinal * agente.getInfluencia();
        }

        double extremismoCalculado = n == 0 ? 0.0 : Math.sqrt(suma) / n;
        int esfuerzo = resultado.getEsfuerzo();
        String cuenta = sumandos.isEmpty() ? "0" : String.join(" + ", sumandos);
        boolean aplicable = esfuerzo <= red.getRMax();

        StringBuilder texto = new StringBuilder();
        texto.append(ejecucion.nombre).append("  (")
                .append(formato(ejecucion.milisegundos, 3)).append(" ms)\n");
        texto.append("Estrategia E = ").append(estrategia.toString().replace(" ", "")).append("\n");
        texto.append("Esfuerzo = ").append(cuenta).append(" = ").append(esfuerzo)
                .append("   (R_max = ").append(red.getRMax()).append(")  ->  ")
                .append(aplicable ? "APLICABLE" : "NO APLICABLE").append("\n");
        texto.append("Extremismo = sqrt(").append(formato(suma, 4)).append(") / ")
                .append(n).append(" = ").append(formato(resultado.getExtremismo(), 4)).append("\n");

        if (esfuerzoCalculado != esfuerzo) {
            texto.append("ADVERTENCIA: la suma de costos por agente (")
                    .append(esfuerzoCalculado).append(") no coincide con el esfuerzo reportado (")
                    .append(esfuerzo).append(").\n");
        }

        if (Math.abs(extremismoCalculado - resultado.getExtremismo()) > 1e-9) {
            texto.append("ADVERTENCIA: el extremismo recalculado (")
                    .append(formato(extremismoCalculado, 4)).append(") no coincide con el reportado.\n");
        }

        Ejecucion referencia = ejecucion.referencia;
        if (referencia != null) {
            texto.append("\n");
            if (referencia.resultado == null) {
                texto.append("Verificacion contra PD no disponible: ")
                        .append(referencia.error).append("\n");
            } else {
                double optimo = referencia.resultado.getExtremismo();
                texto.append("Optimo (PD) = ").append(formato(optimo, 4))
                        .append("  (").append(formato(referencia.milisegundos, 3)).append(" ms)\n");

                if (!aplicable) {
                    texto.append("Veredicto: SOLUCION NO VALIDA (supera el presupuesto).\n");
                } else if (resultado.getExtremismo() <= optimo + 1e-9) {
                    texto.append("Veredicto: OPTIMA\n");
                } else {
                    texto.append("Veredicto: NO OPTIMA, diferencia = ")
                            .append(formato(resultado.getExtremismo() - optimo, 4));
                    if (optimo > 1e-12) {
                        texto.append(" (+")
                                .append(formato((resultado.getExtremismo() - optimo) / optimo * 100, 2))
                                .append("%)");
                    }
                    texto.append("\n");
                }
            }
        }

        return texto.toString();
    }

    private void mostrarComparacion(List<Ejecucion> ejecuciones) {

        // el optimo sale de los algoritmos exactos: fuerza bruta y PD
        double optimo = Double.MAX_VALUE;

        Ejecucion fb = ejecuciones.get(0);
        Ejecucion pd = ejecuciones.get(2);

        int rMax = red.getRMax();

        // solo cuentan soluciones aplicables
        if (fb.resultado != null && fb.resultado.getEsfuerzo() <= rMax) {
            optimo = Math.min(optimo, fb.resultado.getExtremismo());
        }
        if (pd.resultado != null && pd.resultado.getEsfuerzo() <= rMax) {
            optimo = Math.min(optimo, pd.resultado.getExtremismo());
        }

        modeloComparacion.setRowCount(0);

        for (Ejecucion ejecucion : ejecuciones) {

            if (ejecucion.resultado == null) {
                modeloComparacion.addRow(new Object[]{ejecucion.nombre, "-", "-", "-", "-", "-", ejecucion.error});
                continue;
            }

            ResultadoModEx r = ejecucion.resultado;

            boolean esAplicable = r.getEsfuerzo() <= rMax;

            String aplicable = "NO";
            if (esAplicable) {
                aplicable = "Si";
            }

            // para ser optima tiene que ser aplicable Y tener el menor extremismo
            String esOptimo = "-";
            if (optimo != Double.MAX_VALUE) {
                esOptimo = "NO";
                if (esAplicable && r.getExtremismo() <= optimo + 1e-9) {
                    esOptimo = "Si";
                }
            }

            modeloComparacion.addRow(new Object[]{
                    ejecucion.nombre, r.getEsfuerzo(), aplicable,
                    formato(r.getExtremismo(), 4), formato(ejecucion.milisegundos, 3),
                    esOptimo, r.getEstrategia().toString().replace(" ", "")});
        }

        mostrarPestana(1);
    }

    private static String formato(double valor, int decimales) {

        return String.format(Locale.ROOT, "%." + decimales + "f", valor);
    }
}