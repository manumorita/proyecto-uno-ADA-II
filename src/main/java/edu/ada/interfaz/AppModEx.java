package edu.ada.interfaz;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AppModEx {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // si falla se queda con el aspecto por defecto
        }

        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}