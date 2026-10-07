package dao.impl;

import dao.ConexionDB;
import dao.PedidoDAO;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Implementación JDBC de PedidoDAO.
public class PedidoDAOImpl implements PedidoDAO {

    // Inserta un pedido nuevo.
    @Override
    public boolean create(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            return ps.executeUpdate() > 0;
        }
    }

    // Trae todos los pedidos, sin filtros.
    @Override
    public List<Pedido> readAll() throws SQLException {
        return readAll(null, null);
    }

    // Trae los pedidos aplicando solo los filtros que no sean null.
    @Override
    public List<Pedido> readAll(EstadoPedido estado, TipoPedido tipo) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        // Solo se pegan trozos fijos de SQL. Los valores siempre viajan con ?.
        StringBuilder sql = new StringBuilder("SELECT id, direccion, tipo, estado FROM pedidos WHERE 1 = 1");
        if (estado != null) {
            sql.append(" AND estado = ?");
        }
        if (tipo != null) {
            sql.append(" AND tipo = ?");
        }
        sql.append(" ORDER BY id");

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int posicion = 1;
            if (estado != null) {
                ps.setString(posicion++, estado.name());
            }
            if (tipo != null) {
                ps.setString(posicion++, tipo.name());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(new Pedido(
                            rs.getInt("id"),
                            rs.getString("direccion"),
                            aTipo(rs.getString("tipo")),
                            aEstado(rs.getString("estado"))));
                }
            }
        }
        return pedidos;
    }

    // Actualiza dirección, tipo y estado de un pedido existente.
    @Override
    public boolean update(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            ps.setInt(4, pedido.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // Elimina un pedido. Si tiene entregas asociadas, MySQL lo rechaza por la clave foránea.
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Las columnas ENUM del script aceptan NULL, por eso se convierte con cuidado.
    private TipoPedido aTipo(String texto) {
        return texto == null ? null : TipoPedido.valueOf(texto);
    }

    private EstadoPedido aEstado(String texto) {
        return texto == null ? null : EstadoPedido.valueOf(texto);
    }
}
