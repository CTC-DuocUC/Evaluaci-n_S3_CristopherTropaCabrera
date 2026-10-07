package modelo;

// Pedido de SpeedFast. Refleja la tabla pedidos.
public class Pedido {

    private int id;
    private String direccion;
    private TipoPedido tipo;
    private EstadoPedido estado;

    // Para pedidos nuevos: el id lo asigna MySQL al insertar.
    public Pedido(String direccion, TipoPedido tipo, EstadoPedido estado) {
        this.direccion = direccion;
        this.tipo = tipo;
        this.estado = estado;
    }

    // Para pedidos que ya existen en la base de datos.
    public Pedido(int id, String direccion, TipoPedido tipo, EstadoPedido estado) {
        this(direccion, tipo, estado);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public TipoPedido getTipo() {
        return tipo;
    }

    public void setTipo(TipoPedido tipo) {
        this.tipo = tipo;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    // Los JComboBox muestran este texto y el id queda guardado dentro del objeto.
    @Override
    public String toString() {
        return id + " - " + direccion;
    }
}
