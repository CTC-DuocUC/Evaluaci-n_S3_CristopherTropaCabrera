package util;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;

// Todos los mensajes al usuario pasan por aquí, así la aplicación habla siempre igual.
public final class Mensajes {

    private Mensajes() {
    }

    public static void info(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertencia(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Datos no válidos", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String texto) {
        int opcion = JOptionPane.showConfirmDialog(padre, texto, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }

    // Muestra un error de base de datos con un texto claro y deja el detalle técnico en la consola.
    public static void errorSQL(Component padre, String accion, SQLException e) {
        System.err.println("[SpeedFast] Error SQL al " + accion + " (código " + e.getErrorCode()
                + ", estado " + e.getSQLState() + "): " + e.getMessage());

        // Códigos de MySQL más comunes traducidos a un lenguaje normal.
        String detalle = switch (e.getErrorCode()) {
            case 1451 -> "El registro está asociado a otros datos (por ejemplo, entregas) y no se puede eliminar.";
            case 1452 -> "El pedido o el repartidor elegido ya no existe.";
            case 1045 -> "MySQL rechazó el usuario o la contraseña. Revise la clase ConexionDB.";
            case 1049 -> "La base de datos speedfast_db no existe. Ejecute primero el script bd/speedfast_db.sql.";
            case 1146 -> "Falta alguna tabla. Ejecute el script bd/speedfast_db.sql.";
            default -> esErrorDeConexion(e)
                    ? "No se pudo conectar con MySQL. Verifique que el servicio esté activo en localhost:3306."
                    : e.getMessage();
        };

        error(padre, "No se pudo " + accion + ".\n" + detalle);
    }

    // El estado SQL que empieza con 08 indica un problema de conexión.
    private static boolean esErrorDeConexion(SQLException e) {
        return e.getSQLState() != null && e.getSQLState().startsWith("08");
    }
}
