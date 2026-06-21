package dao;

import db.Database;
import model.BankFinancedLoan;
import model.InHouseLoan;
import model.Loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LoanDao implements Crud<Loan> {

    @Override
    public void add(Loan l) throws Exception {
        String sql = "INSERT INTO loans(buyer_id,property_id,finance_type,loan_amount,"
                   + "downpayment,loan_term_years,annual_rate,date_booked) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, l);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Loan> getAll() throws Exception {
        return query("SELECT * FROM loans ORDER BY loan_id", null);
    }

    @Override
    public List<Loan> search(String keyword) throws Exception {
        return query("SELECT * FROM loans WHERE finance_type LIKE ? "
                   + "OR CAST(buyer_id AS TEXT) LIKE ? ORDER BY loan_id", "%" + keyword + "%");
    }

    public Loan findById(int id) throws Exception {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM loans WHERE loan_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public void update(Loan l) throws Exception {
        String sql = "UPDATE loans SET buyer_id=?,property_id=?,finance_type=?,loan_amount=?,"
                   + "downpayment=?,loan_term_years=?,annual_rate=?,date_booked=? WHERE loan_id=?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, l);
            ps.setInt(9, l.getLoanId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM loans WHERE loan_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Loan l) throws Exception {
        ps.setInt(1, l.getBuyerId());
        ps.setInt(2, l.getPropertyId());
        ps.setString(3, l.getFinanceType());
        ps.setDouble(4, l.getLoanAmount());
        ps.setDouble(5, l.getDownpayment());
        ps.setInt(6, l.getLoanTermYears());
        ps.setDouble(7, l.getAnnualRate());
        ps.setString(8, l.getDateBooked());
    }

    private List<Loan> query(String sql, String likeArg) throws Exception {
        List<Loan> out = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (likeArg != null) { ps.setString(1, likeArg); ps.setString(2, likeArg); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    /** Polymorphic factory: rebuild the correct subclass from the stored finance_type. */
    private Loan map(ResultSet rs) throws Exception {
        String type = rs.getString("finance_type");
        Loan l = "InHouse".equalsIgnoreCase(type) ? new InHouseLoan() : new BankFinancedLoan();
        l.setLoanId(rs.getInt("loan_id"));
        l.setBuyerId(rs.getInt("buyer_id"));
        l.setPropertyId(rs.getInt("property_id"));
        l.setFinanceType(type);
        l.setLoanAmount(rs.getDouble("loan_amount"));
        l.setDownpayment(rs.getDouble("downpayment"));
        l.setLoanTermYears(rs.getInt("loan_term_years"));
        l.setAnnualRate(rs.getDouble("annual_rate"));
        l.setDateBooked(rs.getString("date_booked"));
        return l;
    }
}
