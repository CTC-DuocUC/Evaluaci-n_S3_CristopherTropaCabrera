package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import dao.impl.EntregaDAOImpl;
import dao.impl.PedidoDAOImpl;
import dao.impl.RepartidorDAOImpl;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;
import util.Interfaz;
import util.Mensajes;
import util.Formato;
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
import java.awt.Dimension;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Pestaña para administrar entregas: une un pedido con un repartidor, con fecha y hora.
public class PanelEntregas extends JPanel implements Refrescable {

    private static final String TODOS = "Todos";

    private final EntregaDAO entregaDAO = new EntregaDAOImpl();
    private final PedidoDAO pedidoDAO = new PedidoDAOImpl();
    private final RepartidorDAO repartidorDAO = new RepartidorDAOImpl();

    // Los combos guardan el objeto completo: se ve "id - texto" y el id viaja adentro.
    private final JComboBox<Pedido> cmbPedido = new JComboBox<>();
    private final JComboBox<Repartidor> cmbRepartidor = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(10);
    private final JTextField txtHora = new JTextField(6);

    private final JComboBox<Object> cmbFiltroPedido = new JComboBox<>();
    private final JComboBox<Object> cmbFiltroRepartidor = new JComboBox<>();

    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private final DefaultTableModel modeloTabla =
            Interfaz.modeloNoEditable("ID", "Pedido", "Repartidor", "Fecha", "Hora");
    private final JTable tabla = new JTable(modeloTabla);

    // Entregas que muestra la tabla ahora mismo, en el mismo orden de las filas.
    private List<Entrega> entregasEnTabla = new ArrayList<>();

    // Id de la entrega elegida en la tabla. Vale -1 cuando no hay ninguna.
    private int idSeleccionado = -1;

    // Evita recargar la tabla varias veces mientras se vuelven a llenar los combos.
    private boolean cargandoCombos = false;

    public PanelEntregas() {
        construirInterfaz();
        configurarEventos();
        refrescar();
        limpiarFormulario();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        cmbPedido.setPreferredSize(new Dimension(250, 26));
        cmbRepartidor.setPreferredSize(new Dimension(200, 26));
        cmbFiltroPedido.setPreferredSize(new Dimension(250, 26));
        cmbFiltroRepartidor.setPreferredSize(new Dimension(200, 26));

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.add(Interfaz.seccion("Datos de la entrega",
                Interfaz.fila(new JLabel("Pedido:"), cmbPedido,
                        new JLabel("Repartidor:"), cmbRepartidor),
                Interfaz.fila(new JLabel("Fecha (DD-MM-AAAA):"), txtFecha,
                        new JLabel("Hora (HH:mm):"), txtHora),
                Interfaz.fila(btnGuardar, btnActualizar, btnEliminar, btnLimpiar)), BorderLayout.NORTH);
        norte.add(Interfaz.seccion("Filtros opcionales",
                Interfaz.fila(new JLabel("Por pedido:"), cmbFiltroPedido,
                        new JLabel("Por repartidor:"), cmbFiltroRepartidor)), BorderLayout.CENTER);
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
        cmbFiltroPedido.addActionListener(e -> {
            if (!cargandoCombos) {
                cargarTabla();
            }
        });
        cmbFiltroRepartidor.addActionListener(e -> {
            if (!cargandoCombos) {
                cargarTabla();
            }
        });

        // Al elegir una fila se llena el formulario para poder editarla o eliminarla.
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int fila = tabla.getSelectedRow();
            if (fila < 0 || fila >= entregasEnTabla.size()) {
                idSeleccionado = -1;
                activarEdicion(false);
                return;
            }
            Entrega entrega = entregasEnTabla.get(fila);
            idSeleccionado = entrega.getId();
            elegirPedido(entrega.getIdPedido());
            elegirRepartidor(entrega.getIdRepartidor());
            txtFecha.setText(Formato.fecha(entrega.getFecha()));
            txtHora.setText(entrega.getHora() == null ? "" : entrega.getHora().toString());
            activarEdicion(true);
        });
    }

    // Create: valida los datos y registra la entrega.
    private void guardar() {
        try {
            Entrega entrega = leerFormulario(0);

            if (entregaDAO.create(entrega)) {
                Mensajes.info(this, "Entrega registrada correctamente.");
                limpiarFormulario();
                cargarTabla();
            }
        } catch (IllegalArgumentException ex) {
            Mensajes.advertencia(this, ex.getMessage());
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "registrar la entrega", ex);
        }
    }

    // Update: cambia los datos de la entrega elegida en la tabla.
    private void actualizar() {
        try {
            Entrega entrega = leerFormulario(idSeleccionado);

            if (entregaDAO.update(entrega)) {
                Mensajes.info(this, "Entrega actualizada correctamente.");
                limpiarFormulario();
            } else {
                Mensajes.advertencia(this, "No se encontró la entrega. Es posible que ya haya sido eliminada.");
            }
            cargarTabla();
        } catch (IllegalArgumentException ex) {
            Mensajes.advertencia(this, ex.getMessage());
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "actualizar la entrega", ex);
        }
    }

    // Delete: pide confirmación y elimina la entrega elegida.
    private void eliminar() {
        if (!Mensajes.confirmar(this, "¿Desea eliminar la entrega seleccionada?")) {
            return;
        }
        try {
            if (entregaDAO.delete(idSeleccionado)) {
                Mensajes.info(this, "Entrega eliminada correctamente.");
                limpiarFormulario();
            } else {
                Mensajes.advertencia(this, "No se encontró la entrega. Es posible que ya haya sido eliminada.");
            }
            cargarTabla();
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "eliminar la entrega", ex);
        }
    }

    // Read: llena la tabla respetando los filtros que el usuario haya elegido.
    private void cargarTabla() {
        try {
            entregasEnTabla = entregaDAO.readAll(idDelFiltro(cmbFiltroPedido), idDelFiltro(cmbFiltroRepartidor));

            modeloTabla.setRowCount(0);
            for (Entrega entrega : entregasEnTabla) {
                modeloTabla.addRow(new Object[]{
                        entrega.getId(),
                        entrega.getIdPedido() + " - " + entrega.getDireccionPedido(),
                        entrega.getIdRepartidor() + " - " + entrega.getNombreRepartidor(),
                        Formato.fecha(entrega.getFecha()),
                        entrega.getHora()});
            }
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "listar las entregas", ex);
        }
    }

    // Vuelve a cargar desde la base los pedidos y repartidores que se ven en los combos.
    private void cargarCombos() {
        cargandoCombos = true;
        try {
            List<Pedido> pedidos = pedidoDAO.readAll();
            List<Repartidor> repartidores = repartidorDAO.readAll();

            cmbPedido.removeAllItems();
            cmbFiltroPedido.removeAllItems();
            cmbFiltroPedido.addItem(TODOS);
            for (Pedido pedido : pedidos) {
                cmbPedido.addItem(pedido);
                cmbFiltroPedido.addItem(pedido);
            }

            cmbRepartidor.removeAllItems();
            cmbFiltroRepartidor.removeAllItems();
            cmbFiltroRepartidor.addItem(TODOS);
            for (Repartidor repartidor : repartidores) {
                cmbRepartidor.addItem(repartidor);
                cmbFiltroRepartidor.addItem(repartidor);
            }
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "cargar los pedidos y repartidores en las listas", ex);
        } finally {
            cargandoCombos = false;
        }
    }

    // Valida lo escrito en el formulario y arma la Entrega. El id es 0 cuando la entrega es nueva.
    private Entrega leerFormulario(int id) {
        Pedido pedido = Validador.seleccion((Pedido) cmbPedido.getSelectedItem(), "pedido");
        Repartidor repartidor = Validador.seleccion((Repartidor) cmbRepartidor.getSelectedItem(), "repartidor");
        LocalDate fecha = Validador.fecha(txtFecha.getText());
        LocalTime hora = Validador.hora(txtHora.getText());
        return new Entrega(id, pedido.getId(), repartidor.getId(), fecha, hora);
    }

    // El filtro en "Todos" devuelve null y el DAO entiende que no debe filtrar.
    private Integer idDelFiltro(JComboBox<Object> combo) {
        Object elegido = combo.getSelectedItem();
        if (elegido instanceof Pedido pedido) {
            return pedido.getId();
        }
        if (elegido instanceof Repartidor repartidor) {
            return repartidor.getId();
        }
        return null;
    }

    private void elegirPedido(int id) {
        for (int i = 0; i < cmbPedido.getItemCount(); i++) {
            if (cmbPedido.getItemAt(i).getId() == id) {
                cmbPedido.setSelectedIndex(i);
                return;
            }
        }
    }

    private void elegirRepartidor(int id) {
        for (int i = 0; i < cmbRepartidor.getItemCount(); i++) {
            if (cmbRepartidor.getItemAt(i).getId() == id) {
                cmbRepartidor.setSelectedIndex(i);
                return;
            }
        }
    }

    // Deja el formulario listo para una entrega nueva, con la fecha y hora de este momento.
    private void limpiarFormulario() {
        txtFecha.setText(Formato.fecha(LocalDate.now()));
        txtHora.setText(LocalTime.now().withSecond(0).withNano(0).toString());
        tabla.clearSelection();
    }

    // Actualizar y Eliminar solo tienen sentido cuando hay una fila elegida.
    private void activarEdicion(boolean activa) {
        btnActualizar.setEnabled(activa);
        btnEliminar.setEnabled(activa);
    }

    @Override
    public void refrescar() {
        cargarCombos();
        cargarTabla();
    }
}
