package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

// Entrega de SpeedFast. Refleja la tabla entregas y une un pedido con un repartidor.
public class Entrega {

    private int id;
    private int idPedido;
    private int idRepartidor;
    private LocalDate fecha;
    private LocalTime hora;

    // Estos dos datos no son columnas de entregas: el DAO los trae con un JOIN para poder mostrarlos en la tabla.
    private String direccionPedido;
    private String nombreRepartidor;

    // Para entregas nuevas: el id lo asigna MySQL al insertar.
    public Entrega(int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    // Para entregas que ya existen en la base de datos.
    public Entrega(int id, int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this(idPedido, idRepartidor, fecha, hora);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(int idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getDireccionPedido() {
        return direccionPedido;
    }

    public void setDireccionPedido(String direccionPedido) {
        this.direccionPedido = direccionPedido;
    }

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = nombreRepartidor;
    }
}
