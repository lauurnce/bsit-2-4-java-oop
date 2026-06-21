package ui;

import app.Session;
import dao.BuyerDao;
import dao.LoanDao;
import dao.PropertyDao;
import model.BankFinancedLoan;
import model.InHouseLoan;
import model.Loan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoanPanel extends JPanel {
    private final LoanDao dao = new LoanDao();
    private final BuyerDao buyerDao = new BuyerDao();
    private final PropertyDao propertyDao = new PropertyDao();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "BuyerID", "PropID", "Type", "Amount", "DP", "Yrs",
                    "Rate%", "Monthly", "Total"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(16);

    public LoanPanel() {
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");
        top.add(new JLabel("Search (type/buyer):")); top.add(searchField);
        top.add(searchBtn); top.add(refreshBtn);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        delBtn.setEnabled(Session.isAdmin());
        bottom.add(addBtn); bottom.add(editBtn); bottom.add(delBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadAll());
        searchBtn.addActionListener(e -> doSearch());
        addBtn.addActionListener(e -> addOrEdit(null));
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> deleteSelected());

        loadAll();
    }

    private void loadAll() {
        try { fill(dao.getAll()); }
        catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }

    private void doSearch() {
        try {
            List<Loan> r = dao.search(searchField.getText().trim());
            if (r.isEmpty()) UiUtil.info(this, "No record found.");
            fill(r);
        } catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }

    private void fill(List<Loan> rows) {
        model.setRowCount(0);
        for (Loan l : rows)
            model.addRow(new Object[]{l.getLoanId(), l.getBuyerId(), l.getPropertyId(),
                    l.getFinanceType(), l.getLoanAmount(), l.getDownpayment(),
                    l.getLoanTermYears(), l.getAnnualRate(),
                    Math.round(l.computeMonthlyAmortization() * 100) / 100.0,
                    Math.round(l.computeTotalPayable() * 100) / 100.0});
    }

    private Integer selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) { UiUtil.info(this, "Select a row first."); return null; }
        return (Integer) model.getValueAt(row, 0);
    }

    private void editSelected() {
        Integer id = selectedId();
        if (id == null) return;
        try { addOrEdit(dao.findById(id)); }
        catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }

    private void deleteSelected() {
        Integer id = selectedId();
        if (id == null) return;
        if (!UiUtil.confirm(this, "Delete loan #" + id + "?")) return;
        try { dao.delete(id); loadAll(); }
        catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }

    private void addOrEdit(Loan existing) {
        JComboBox<String> type = new JComboBox<>(new String[]{"Bank", "InHouse"});
        if (existing != null) type.setSelectedItem(existing.getFinanceType());
        JTextField buyerId = new JTextField(existing == null ? "" : String.valueOf(existing.getBuyerId()));
        JTextField propId = new JTextField(existing == null ? "" : String.valueOf(existing.getPropertyId()));
        JTextField amount = new JTextField(existing == null ? "" : String.valueOf(existing.getLoanAmount()));
        JTextField dp = new JTextField(existing == null ? "" : String.valueOf(existing.getDownpayment()));
        JTextField years = new JTextField(existing == null ? "" : String.valueOf(existing.getLoanTermYears()));
        JTextField rate = new JTextField(existing == null ? "" : String.valueOf(existing.getAnnualRate()));
        JTextField date = new JTextField(existing == null ? "" : existing.getDateBooked());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Finance Type:")); form.add(type);
        form.add(new JLabel("Buyer ID*:")); form.add(buyerId);
        form.add(new JLabel("Property ID*:")); form.add(propId);
        form.add(new JLabel("Loan Amount*:")); form.add(amount);
        form.add(new JLabel("Downpayment*:")); form.add(dp);
        form.add(new JLabel("Term (years)*:")); form.add(years);
        form.add(new JLabel("Annual Rate %*:")); form.add(rate);
        form.add(new JLabel("Date Booked:")); form.add(date);

        int ok = JOptionPane.showConfirmDialog(this, form,
                existing == null ? "Add Loan" : "Edit Loan", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int bId = Integer.parseInt(buyerId.getText().trim());
            int pId = Integer.parseInt(propId.getText().trim());
            double amt = Double.parseDouble(amount.getText().trim());
            double down = Double.parseDouble(dp.getText().trim());
            int yrs = Integer.parseInt(years.getText().trim());
            double rt = Double.parseDouble(rate.getText().trim());

            if (buyerDao.findById(bId) == null) { UiUtil.error(this, "Buyer ID not found."); return; }
            if (propertyDao.findById(pId) == null) { UiUtil.error(this, "Property ID not found."); return; }
            if (down > amt) { UiUtil.error(this, "Downpayment cannot exceed loan amount."); return; }

            String t = (String) type.getSelectedItem();
            Loan l = "InHouse".equals(t) ? new InHouseLoan() : new BankFinancedLoan();
            if (existing != null) l.setLoanId(existing.getLoanId());
            l.setFinanceType(t);
            l.setBuyerId(bId); l.setPropertyId(pId);
            l.setLoanAmount(amt); l.setDownpayment(down);
            l.setLoanTermYears(yrs); l.setAnnualRate(rt);
            l.setDateBooked(date.getText().trim());

            if (existing == null) dao.add(l); else dao.update(l);
            loadAll();
        } catch (NumberFormatException ex) {
            UiUtil.error(this, "IDs, amounts, term and rate must be numbers.");
        } catch (Exception ex) {
            UiUtil.error(this, ex.getMessage());
        }
    }
}
