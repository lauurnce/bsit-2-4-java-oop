package dao;

import db.Database;
import model.Buyer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BuyerDao implements Crud<Buyer> {

    @Override
    public void add(Buyer b) throws Exception {
        String sql = "INSERT INTO buyers(full_name,birthdate,gov_id,gender,civil_status,"
                   + "address,monthly_income,mobile_num,email) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, b);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Buyer> getAll() throws Exception {
        return query("SELECT * FROM buyers ORDER BY buyer_id", null);
    }

    /** Overloaded search by free-text keyword (name or email). */
    @Override
    public List<Buyer> search(String keyword) throws Exception {
        return query("SELECT * FROM buyers WHERE full_name LIKE ? OR email LIKE ? "
                   + "ORDER BY buyer_id", "%" + keyword + "%");
    }

    public Buyer findById(int id) throws Exception {
        String sql = "SELECT * FROM buyers WHERE buyer_id = ?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public void update(Buyer b) throws Exception {
        String sql = "UPDATE buyers SET full_name=?,birthdate=?,gov_id=?,gender=?,"
                   + "civil_status=?,address=?,monthly_income=?,mobile_num=?,email=? "
                   + "WHERE buyer_id=?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, b);
            ps.setInt(10, b.getId());
            ps.executeUpdate();
        }
    }

    @Override
    /** Delete a buyer and any loans that reference it, in one transaction (cascade). */
    public void delete(int id) throws Exception {
        try (Connection c = Database.connect()) {
            c.setAutoCommit(false);
            try (PreparedStatement delLoans = c.prepareStatement("DELETE FROM loans WHERE buyer_id=?");
                 PreparedStatement delBuyer = c.prepareStatement("DELETE FROM buyers WHERE buyer_id=?")) {
                delLoans.setInt(1, id);
                delLoans.executeUpdate();
                delBuyer.setInt(1, id);
                delBuyer.executeUpdate();
                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        }
    }

    /** Number of loans referencing this buyer. */
    public int countLoans(int buyerId) throws Exception {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM loans WHERE buyer_id=?")) {
            ps.setInt(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private void bind(PreparedStatement ps, Buyer b) throws Exception {
        ps.setString(1, b.getFullName());
        ps.setString(2, b.getBirthdate());
        ps.setString(3, b.getGovId());
        ps.setString(4, b.getGender());
        ps.setString(5, b.getCivilStatus());
        ps.setString(6, b.getAddress());
        ps.setDouble(7, b.getMonthlyIncome());
        ps.setString(8, b.getMobileNum());
        ps.setString(9, b.getEmail());
    }

    private List<Buyer> query(String sql, String likeArg) throws Exception {
        List<Buyer> out = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (likeArg != null) { ps.setString(1, likeArg); ps.setString(2, likeArg); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    private Buyer map(ResultSet rs) throws Exception {
        return new Buyer(rs.getInt("buyer_id"), rs.getString("full_name"),
                rs.getString("birthdate"), rs.getString("gov_id"), rs.getString("gender"),
                rs.getString("civil_status"), rs.getString("address"),
                rs.getDouble("monthly_income"), rs.getString("mobile_num"),
                rs.getString("email"));
    }
}
