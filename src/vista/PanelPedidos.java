package vista;

import dao.PedidoDAO;
import dao.impl.PedidoDAOImpl;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;
import util.Interfaz;
import util.Mensajes;
import util.Validador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.SQLException;

// Pestaña para administrar pedidos: registrar, listar con filtros, editar y eliminar.
public class PanelPedidos extends JPanel implements Refrescable {

    private static final String TODOS = "Todos";

    private final PedidoDAO dao = new PedidoDAOImpl();

    private final JTextField txtDireccion = new JTextField(34);
    private final JComboBox<TipoPedido> cmbTipo = new JComboBox<>(TipoPedido.values());
    private final JComboBox<EstadoPedido> cmbEstado = new JComboBox<>(EstadoPedido.values());

    private final JComboBox<String> cmbFiltroTipo = new JComboBox<>(opcionesConTodos(TipoPedido.values()));
    private final JComboBox<String> cmbFiltroEstado = new JComboBox<>(opcionesConTodos(EstadoPedido.values()));

    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private final DefaultTableModel modeloTabla = Interfaz.modeloNoEditable("ID", "Dirección", "Tipo", "Estado");
    private final JTable tabla = new JTable(modeloTabla);

    // Id del pedido elegido en la tabla. Vale -1 cuando no hay ninguno.
    private int idSeleccionado = -1;

    public PanelPedidos() {
        construirInterfaz();
        configurarEventos();
        refrescar();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.add(Interfaz.seccion("Datos del pedido",
                Interfaz.fila(new JLabel("Dirección:"), txtDireccion,
                        new JLabel("Tipo:"), cmbTipo,
                        new JLabel("Estado:"), cmbEstado),
                Interfaz.fila(btnGuardar, btnActualizar, btnEliminar, btnLimpiar)), BorderLayout.NORTH);
        norte.add(Interfaz.seccion("Filtros opcionales",
                Interfaz.fila(new JLabel("Tipo:"), cmbFiltroTipo,
                        new JLabel("Estado:"), cmbFiltroEstado)), BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

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

        // Cada vez que cambia un filtro se vuelve a consultar la base.
        cmbFiltroTipo.addActionListener(e -> cargarTabla());
        cmbFiltroEstado.addActionListener(e -> cargarTabla());

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
            txtDireccion.setText(modeloTabla.getValueAt(fila, 1).toString());
            cmbTipo.setSelectedItem(modeloTabla.getValueAt(fila, 2));
            cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 3));
            activarEdicion(true);
        });
    }

    // Create: valida los datos y registra el pedido.
    private void guardar() {
        try {
            Pedido pedido = leerFormulario(0);

            if (dao.create(pedido)) {
                Mensajes.info(this, "Pedido registrado correctamente.");
                limpiarFormulario();
                cargarTabla();
            }
        } catch (IllegalArgumentException ex) {
            Mensajes.advertencia(this, ex.getMessage());
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "registrar el pedido", ex);
        }
    }

    // Update: cambia los datos del pedido elegido en la tabla.
    private void actualizar() {
        try {
            Pedido pedido = leerFormulario(idSeleccionado);

            if (dao.update(pedido)) {
                Mensajes.info(this, "Pedido actualizado correctamente.");
                limpiarFormulario();
            } else {
                Mensajes.advertencia(this, "No se encontró el pedido. Es posible que ya haya sido eliminado.");
            }
            cargarTabla();
        } catch (IllegalArgumentException ex) {
            Mensajes.advertencia(this, ex.getMessage());
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "actualizar el pedido", ex);
        }
    }

    // Delete: pide confirmación y elimina el pedido elegido.
    private void eliminar() {
        if (!Mensajes.confirmar(this, "¿Desea eliminar el pedido seleccionado?")) {
            return;
        }
        try {
            if (dao.delete(idSeleccionado)) {
                Mensajes.info(this, "Pedido eliminado correctamente.");
                limpiarFormulario();
            } else {
                Mensajes.advertencia(this, "No se encontró el pedido. Es posible que ya haya sido eliminado.");
            }
            cargarTabla();
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "eliminar el pedido", ex);
        }
    }

    // Read: llena la tabla respetando los filtros que el usuario haya elegido.
    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            for (Pedido pedido : dao.readAll(filtroEstado(), filtroTipo())) {
                modeloTabla.addRow(new Object[]{
                        pedido.getId(), pedido.getDireccion(), pedido.getTipo(), pedido.getEstado()});
            }
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "listar los pedidos", ex);
        }
    }

    // Valida lo escrito en el formulario y arma el Pedido. El id es 0 cuando el pedido es nuevo.
    private Pedido leerFormulario(int id) {
        String direccion = Validador.textoObligatorio(txtDireccion.getText(), "Dirección");
        TipoPedido tipo = Validador.seleccion((TipoPedido) cmbTipo.getSelectedItem(), "tipo");
        EstadoPedido estado = Validador.seleccion((EstadoPedido) cmbEstado.getSelectedItem(), "estado");
        return new Pedido(id, direccion, tipo, estado);
    }

    // Devuelve null cuando el filtro está en "Todos", y el DAO entiende que no debe filtrar.
    private TipoPedido filtroTipo() {
        String elegido = (String) cmbFiltroTipo.getSelectedItem();
        return TODOS.equals(elegido) ? null : TipoPedido.valueOf(elegido);
    }

    private EstadoPedido filtroEstado() {
        String elegido = (String) cmbFiltroEstado.getSelectedItem();
        return TODOS.equals(elegido) ? null : EstadoPedido.valueOf(elegido);
    }

    private void limpiarFormulario() {
        txtDireccion.setText("");
        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        tabla.clearSelection();
        txtDireccion.requestFocus();
    }

    // Actualizar y Eliminar solo tienen sentido cuando hay una fila elegida.
    private void activarEdicion(boolean activa) {
        btnActualizar.setEnabled(activa);
        btnEliminar.setEnabled(activa);
    }

    // Arma la lista del combo de filtro: primero "Todos" y después los valores del enum.
    private static String[] opcionesConTodos(Object[] valores) {
        String[] opciones = new String[valores.length + 1];
        opciones[0] = TODOS;
        for (int i = 0; i < valores.length; i++) {
            opciones[i + 1] = valores[i].toString();
        }
        return opciones;
    }

    @Override
    public void refrescar() {
        cargarTabla();
    }
}
