package modelo;

// Repartidor de SpeedFast. Refleja la tabla repartidores.
public class Repartidor {

    private int id;
    private String nombre;

    // Para repartidores nuevos: el id lo asigna MySQL al insertar.
    public Repartidor(String nombre) {
        this.nombre = nombre;
    }

    // Para repartidores que ya existen en la base de datos.
    public Repartidor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Los JComboBox muestran este texto y el id queda guardado dentro del objeto.
    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}
