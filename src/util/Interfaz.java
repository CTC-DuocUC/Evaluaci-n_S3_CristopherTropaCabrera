package util;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;

// Pequeñas ayudas para armar las pestañas sin repetir el mismo código tres veces.
public final class Interfaz {

    private Interfaz() {
    }

    // Una fila de componentes alineada a la izquierda.
    public static JPanel fila(Component... componentes) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        for (Component componente : componentes) {
            fila.add(componente);
        }
        return fila;
    }

    // Un recuadro con título que apila varias filas.
    public static JPanel seccion(String titulo, Component... filas) {
        JPanel seccion = new JPanel(new GridLayout(0, 1));
        seccion.setBorder(BorderFactory.createTitledBorder(titulo));
        for (Component fila : filas) {
            seccion.add(fila);
        }
        return seccion;
    }

    // Modelo de tabla donde el usuario no puede escribir sobre las celdas.
    public static DefaultTableModel modeloNoEditable(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    // Ajustes comunes de todas las tablas: una fila a la vez y filas más cómodas de leer.
    public static void configurarTabla(JTable tabla) {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);
    }
}
