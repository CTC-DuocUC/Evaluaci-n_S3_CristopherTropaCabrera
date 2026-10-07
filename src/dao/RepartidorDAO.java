package dao;

import modelo.Repartidor;

import java.sql.SQLException;
import java.util.List;

// Define qué se puede hacer con los repartidores en la base de datos.
// Las excepciones SQL suben hasta la vista, que las muestra al usuario.
public interface RepartidorDAO {

    boolean create(Repartidor repartidor) throws SQLException;

    List<Repartidor> readAll() throws SQLException;

    boolean update(Repartidor repartidor) throws SQLException;

    boolean delete(int id) throws SQLException;
}
