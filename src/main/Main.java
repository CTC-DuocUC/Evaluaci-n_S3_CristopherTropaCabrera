package main;

import dao.ConexionDB;
import util.Mensajes;
import vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.Connection;
import java.sql.SQLException;

// Punto de entrada de la aplicación.
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, Swing usa su aspecto por defecto y la aplicación funciona igual.
        }

        // La ventana se crea en el hilo de eventos de Swing.
        SwingUtilities.invokeLater(() -> {
            if (!hayConexion()) {
                System.exit(1);
            }
            new VentanaPrincipal().setVisible(true);
        });
    }

    // Prueba la conexión antes de abrir la ventana. Si falla, explica qué revisar.
    private static boolean hayConexion() {
        try (Connection con = ConexionDB.conectar()) {
            return con.isValid(3);
        } catch (SQLException e) {
            Mensajes.errorSQL(null, "conectar con la base de datos", e);
            return false;
        }
    }
}
