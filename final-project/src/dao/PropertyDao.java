package dao;

import db.Database;
import model.Property;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PropertyDao implements Crud<Property> {

    @Override
    public void add(Property p) throws Exception {
        String sql = "INSERT INTO properties(unit_name,location,unit_type,sell_price,status) "
                   + "VALUES(?,?,?,?,?)";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Property> getAll() throws Exception {
        return query("SELECT * FROM properties ORDER BY property_id", null);
    }

    @Override
    public List<Property> search(String keyword) throws Exception {
        return query("SELECT * FROM properties WHERE unit_name LIKE ? OR location LIKE ? "
                   + "ORDER BY property_id", "%" + keyword + "%");
    }

    public Property findById(int id) throws Exception {
        String sql = "SELECT * FROM properties WHERE property_id = ?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public void update(Property p) throws Exception {
        String sql = "UPDATE properties SET unit_name=?,location=?,unit_type=?,"
                   + "sell_price=?,status=? WHERE property_id=?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p);
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    /** Delete a property and any loans that reference it, in one transaction (cascade). */
    public void delete(int id) throws Exception {
        try (Connection c = Database.connect()) {
            c.setAutoCommit(false);
            try (PreparedStatement delLoans = c.prepareStatement("DELETE FROM loans WHERE property_id=?");
                 PreparedStatement delProp = c.prepareStatement("DELETE FROM properties WHERE property_id=?")) {
                delLoans.setInt(1, id);
                delLoans.executeUpdate();
                delProp.setInt(1, id);
                delProp.executeUpdate();
                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        }
    }

    /** Number of loans referencing this property. */
    public int countLoans(int propertyId) throws Exception {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM loans WHERE property_id=?")) {
            ps.setInt(1, propertyId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private void bind(PreparedStatement ps, Property p) throws Exception {
        ps.setString(1, p.getUnitName());
        ps.setString(2, p.getLocation());
        ps.setString(3, p.getUnitType());
        ps.setDouble(4, p.getSellPrice());
        ps.setString(5, p.getStatus());
    }

    private List<Property> query(String sql, String likeArg) throws Exception {
        List<Property> out = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (likeArg != null) { ps.setString(1, likeArg); ps.setString(2, likeArg); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    private Property map(ResultSet rs) throws Exception {
        return new Property(rs.getInt("property_id"), rs.getString("unit_name"),
                rs.getString("location"), rs.getString("unit_type"),
                rs.getDouble("sell_price"), rs.getString("status"));
    }
}
