package dao.impl;

import dao.ConexionDB;
import dao.EntregaDAO;
import modelo.Entrega;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Implementación JDBC de EntregaDAO.
public class EntregaDAOImpl implements EntregaDAO {

    // Registra la entrega que une un pedido con un repartidor.
    @Override
    public boolean create(Entrega entrega) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setObject(3, entrega.getFecha());
            ps.setObject(4, entrega.getHora());
            return ps.executeUpdate() > 0;
        }
    }

    // Trae todas las entregas, sin filtros.
    @Override
    public List<Entrega> readAll() throws SQLException {
        return readAll(null, null);
    }

    // Trae las entregas con la dirección del pedido y el nombre del repartidor.
    // Solo se aplican los filtros que no sean null.
    @Override
    public List<Entrega> readAll(Integer idPedido, Integer idRepartidor) throws SQLException {
        List<Entrega> entregas = new ArrayList<>();

        // Solo se pegan trozos fijos de SQL. Los valores siempre viajan con ?.
        StringBuilder sql = new StringBuilder(
                "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, p.direccion, r.nombre "
                        + "FROM entregas e "
                        + "LEFT JOIN pedidos p ON p.id = e.id_pedido "
                        + "LEFT JOIN repartidores r ON r.id = e.id_repartidor "
                        + "WHERE 1 = 1");
        if (idPedido != null) {
            sql.append(" AND e.id_pedido = ?");
        }
        if (idRepartidor != null) {
            sql.append(" AND e.id_repartidor = ?");
        }
        sql.append(" ORDER BY e.id");

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int posicion = 1;
            if (idPedido != null) {
                ps.setInt(posicion++, idPedido);
            }
            if (idRepartidor != null) {
                ps.setInt(posicion++, idRepartidor);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Entrega entrega = new Entrega(
                            rs.getInt("id"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_repartidor"),
                            rs.getObject("fecha", LocalDate.class),
                            rs.getObject("hora", LocalTime.class));
                    entrega.setDireccionPedido(rs.getString("direccion"));
                    entrega.setNombreRepartidor(rs.getString("nombre"));
                    entregas.add(entrega);
                }
            }
        }
        return entregas;
    }

    // Cambia el pedido, el repartidor, la fecha y la hora de una entrega existente.
    @Override
    public boolean update(Entrega entrega) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setObject(3, entrega.getFecha());
            ps.setObject(4, entrega.getHora());
            ps.setInt(5, entrega.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // Elimina una entrega.
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
