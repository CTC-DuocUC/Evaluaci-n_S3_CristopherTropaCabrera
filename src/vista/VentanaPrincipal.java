package vista;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Component;

// Ventana principal de SpeedFast con una pestaña por cada entidad.
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("SpeedFast - Gestión de repartos");
        setSize(1000, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Repartidores", new PanelRepartidores());
        pestanas.addTab("Pedidos", new PanelPedidos());
        pestanas.addTab("Entregas", new PanelEntregas());

        // Al cambiar de pestaña se vuelven a leer los datos, así los combos y tablas quedan al día.
        pestanas.addChangeListener(e -> {
            Component actual = pestanas.getSelectedComponent();
            if (actual instanceof Refrescable refrescable) {
                refrescable.refrescar();
            }
        });

        add(pestanas);
    }
}
