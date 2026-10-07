package vista;

import dao.RepartidorDAO;
import dao.impl.RepartidorDAOImpl;
import modelo.Repartidor;
import util.Interfaz;
import util.Mensajes;
import util.Validador;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.SQLException;

// Pestaña para administrar repartidores: registrar, listar, editar y eliminar.
public class PanelRepartidores extends JPanel implements Refrescable {

    private final RepartidorDAO dao = new RepartidorDAOImpl();

    private final JTextField txtNombre = new JTextField(28);
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private final DefaultTableModel modeloTabla = Interfaz.modeloNoEditable("ID", "Nombre");
    private final JTable tabla = new JTable(modeloTabla);

    // Id del repartidor elegido en la tabla. Vale -1 cuando no hay ninguno.
    private int idSeleccionado = -1;

    public PanelRepartidores() {
        construirInterfaz();
        configurarEventos();
        refrescar();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(Interfaz.seccion("Datos del repartidor",
                Interfaz.fila(new JLabel("Nombre:"), txtNombre),
                Interfaz.fila(btnGuardar, btnActualizar, btnEliminar, btnLimpiar)), BorderLayout.NORTH);

        Interfaz.configurarTabla(tabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        activarEdicion(false);
    }

    private void configurarEventos() {
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Al elegir una fila se llena el formulario para poder editarla o eliminarla.
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                idSeleccionado = -1;
                activarEdicion(false);
                return;
            }
            idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
            activarEdicion(true);
        });
    }

    // Create: valida el nombre y registra al repartidor.
    private void guardar() {
        try {
            Repartidor repartidor = new Repartidor(Validador.nombre(txtNombre.getText()));

            if (dao.create(repartidor)) {
                Mensajes.info(this, "Repartidor registrado correctamente.");
                limpiarFormulario();
                cargarTabla();
            }
        } catch (IllegalArgumentException ex) {
            Mensajes.advertencia(this, ex.getMessage());
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "registrar el repartidor", ex);
        }
    }

    // Update: cambia el nombre del repartidor elegido en la tabla.
    private void actualizar() {
        try {
            Repartidor repartidor = new Repartidor(idSeleccionado, Validador.nombre(txtNombre.getText()));

            if (dao.update(repartidor)) {
                Mensajes.info(this, "Repartidor actualizado correctamente.");
                limpiarFormulario();
            } else {
                Mensajes.advertencia(this, "No se encontró el repartidor. Es posible que ya haya sido eliminado.");
            }
            cargarTabla();
        } catch (IllegalArgumentException ex) {
            Mensajes.advertencia(this, ex.getMessage());
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "actualizar el repartidor", ex);
        }
    }

    // Delete: pide confirmación y elimina al repartidor elegido.
    private void eliminar() {
        if (!Mensajes.confirmar(this, "¿Desea eliminar al repartidor seleccionado?")) {
            return;
        }
        try {
            if (dao.delete(idSeleccionado)) {
                Mensajes.info(this, "Repartidor eliminado correctamente.");
                limpiarFormulario();
            } else {
                Mensajes.advertencia(this, "No se encontró el repartidor. Es posible que ya haya sido eliminado.");
            }
            cargarTabla();
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "eliminar el repartidor", ex);
        }
    }

    // Read: vuelve a llenar la tabla con lo que hay en la base de datos.
    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            for (Repartidor repartidor : dao.readAll()) {
                modeloTabla.addRow(new Object[]{repartidor.getId(), repartidor.getNombre()});
            }
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "listar los repartidores", ex);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        tabla.clearSelection();
        txtNombre.requestFocus();
    }

    // Actualizar y Eliminar solo tienen sentido cuando hay una fila elegida.
    private void activarEdicion(boolean activa) {
        btnActualizar.setEnabled(activa);
        btnEliminar.setEnabled(activa);
    }

    @Override
    public void refrescar() {
        cargarTabla();
    }
}
