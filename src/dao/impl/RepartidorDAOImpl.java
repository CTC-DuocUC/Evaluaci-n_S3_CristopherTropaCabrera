package dao.impl;

import dao.ConexionDB;
import dao.RepartidorDAO;
import modelo.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Implementación JDBC de RepartidorDAO.
// Connection, PreparedStatement y ResultSet se cierran solos gracias al try-with-resources.
public class RepartidorDAOImpl implements RepartidorDAO {

    // Inserta un repartidor nuevo.
    @Override
    public boolean create(Repartidor repartidor) throws SQLException {
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, repartidor.getNombre());
            return ps.executeUpdate() > 0;
        }
    }

    // Trae todos los repartidores ordenados por id.
    @Override
    public List<Repartidor> readAll() throws SQLException {
        List<Repartidor> repartidores = new ArrayList<>();
        String sql = "SELECT id, nombre FROM repartidores ORDER BY id";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                repartidores.add(new Repartidor(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return repartidores;
    }

    // Cambia el nombre de un repartidor existente.
    @Override
    public boolean update(Repartidor repartidor) throws SQLException {
        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, repartidor.getNombre());
            ps.setInt(2, repartidor.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // Elimina un repartidor. Si tiene entregas asociadas, MySQL lo rechaza por la clave foránea.
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
