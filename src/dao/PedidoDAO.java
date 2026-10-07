package dao;

import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

import java.sql.SQLException;
import java.util.List;

// Define qué se puede hacer con los pedidos en la base de datos.
public interface PedidoDAO {

    boolean create(Pedido pedido) throws SQLException;

    List<Pedido> readAll() throws SQLException;

    // Versión con filtros opcionales: si un parámetro es null, ese filtro no se aplica.
    List<Pedido> readAll(EstadoPedido estado, TipoPedido tipo) throws SQLException;

    boolean update(Pedido pedido) throws SQLException;

    boolean delete(int id) throws SQLException;
}
