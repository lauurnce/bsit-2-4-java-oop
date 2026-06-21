package ui;

import app.Session;
import dao.BuyerDao;
import model.Buyer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BuyerPanel extends JPanel {
    private final BuyerDao dao = new BuyerDao();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Gov ID", "Income", "Mobile", "Email"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(18);

    public BuyerPanel() {
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");
        top.add(new JLabel("Search:")); top.add(searchField);
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
            List<Buyer> r = dao.search(searchField.getText().trim());
            if (r.isEmpty()) UiUtil.info(this, "No record found.");
            fill(r);
        } catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }

    private void fill(List<Buyer> rows) {
        model.setRowCount(0);
        for (Buyer b : rows)
            model.addRow(new Object[]{b.getId(), b.getFullName(), b.getGovId(),
                    b.getMonthlyIncome(), b.getMobileNum(), b.getEmail()});
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
        if (!UiUtil.confirm(this, "Delete buyer #" + id + "?")) return;
        try { dao.delete(id); loadAll(); }
        catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }

    /** Add (existing == null) or edit a buyer via a form dialog. */
    private void addOrEdit(Buyer existing) {
        JTextField name = new JTextField(existing == null ? "" : existing.getFullName());
        JTextField bdate = new JTextField(existing == null ? "" : existing.getBirthdate());
        JTextField gov = new JTextField(existing == null ? "" : existing.getGovId());
        JTextField gender = new JTextField(existing == null ? "" : existing.getGender());
        JTextField civil = new JTextField(existing == null ? "" : existing.getCivilStatus());
        JTextField addr = new JTextField(existing == null ? "" : existing.getAddress());
        JTextField income = new JTextField(existing == null ? "" : String.valueOf(existing.getMonthlyIncome()));
        JTextField mobile = new JTextField(existing == null ? "" : existing.getMobileNum());
        JTextField email = new JTextField(existing == null ? "" : existing.getEmail());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Full Name*:")); form.add(name);
        form.add(new JLabel("Birthdate:")); form.add(bdate);
        form.add(new JLabel("Gov ID:")); form.add(gov);
        form.add(new JLabel("Gender:")); form.add(gender);
        form.add(new JLabel("Civil Status:")); form.add(civil);
        form.add(new JLabel("Address:")); form.add(addr);
        form.add(new JLabel("Monthly Income*:")); form.add(income);
        form.add(new JLabel("Mobile:")); form.add(mobile);
        form.add(new JLabel("Email:")); form.add(email);

        int ok = JOptionPane.showConfirmDialog(this, form,
                existing == null ? "Add Buyer" : "Edit Buyer", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        if (name.getText().trim().isEmpty()) { UiUtil.error(this, "Full name is required."); return; }
        double inc;
        try { inc = Double.parseDouble(income.getText().trim()); }
        catch (NumberFormatException ex) { UiUtil.error(this, "Income must be a number."); return; }

        try {
            Buyer b = existing == null ? new Buyer() : existing;
            b.setFullName(name.getText().trim());
            b.setBirthdate(bdate.getText().trim());
            b.setGovId(gov.getText().trim());
            b.setGender(gender.getText().trim());
            b.setCivilStatus(civil.getText().trim());
            b.setAddress(addr.getText().trim());
            b.setMonthlyIncome(inc);
            b.setMobileNum(mobile.getText().trim());
            b.setEmail(email.getText().trim());
            if (existing == null) dao.add(b); else dao.update(b);
            loadAll();
        } catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }
}
