package dao;

import modelo.Entrega;

import java.sql.SQLException;
import java.util.List;

// Define qué se puede hacer con las entregas en la base de datos.
public interface EntregaDAO {

    boolean create(Entrega entrega) throws SQLException;

    List<Entrega> readAll() throws SQLException;

    // Versión con filtros opcionales: si un parámetro es null, ese filtro no se aplica.
    List<Entrega> readAll(Integer idPedido, Integer idRepartidor) throws SQLException;

    boolean update(Entrega entrega) throws SQLException;

    boolean delete(int id) throws SQLException;
}
