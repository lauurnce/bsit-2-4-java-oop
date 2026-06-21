package ui;

import app.Session;
import dao.PropertyDao;
import model.Property;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PropertyPanel extends JPanel {
    private final PropertyDao dao = new PropertyDao();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Unit Name", "Location", "Type", "Sell Price", "Status"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(18);

    public PropertyPanel() {
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
            List<Property> r = dao.search(searchField.getText().trim());
            if (r.isEmpty()) UiUtil.info(this, "No record found.");
            fill(r);
        } catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }

    private void fill(List<Property> rows) {
        model.setRowCount(0);
        for (Property p : rows)
            model.addRow(new Object[]{p.getId(), p.getUnitName(), p.getLocation(),
                    p.getUnitType(), p.getSellPrice(), p.getStatus()});
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
        try {
            int loans = dao.countLoans(id);
            String msg = loans > 0
                    ? "Property #" + id + " has " + loans + " loan(s). Deleting the property will also delete those loan(s). Continue?"
                    : "Delete property #" + id + "?";
            if (!UiUtil.confirm(this, msg)) return;
            dao.delete(id);
            loadAll();
        } catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }

    private void addOrEdit(Property existing) {
        JTextField unit = new JTextField(existing == null ? "" : existing.getUnitName());
        JTextField loc = new JTextField(existing == null ? "" : existing.getLocation());
        JComboBox<String> type = new JComboBox<>(new String[]{"House & Lot", "Townhouse", "Condo"});
        if (existing != null) type.setSelectedItem(existing.getUnitType());
        JTextField price = new JTextField(existing == null ? "" : String.valueOf(existing.getSellPrice()));
        JComboBox<String> status = new JComboBox<>(new String[]{"Available", "Sold"});
        if (existing != null) status.setSelectedItem(existing.getStatus());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Unit Name*:")); form.add(unit);
        form.add(new JLabel("Location:")); form.add(loc);
        form.add(new JLabel("Type:")); form.add(type);
        form.add(new JLabel("Sell Price*:")); form.add(price);
        form.add(new JLabel("Status:")); form.add(status);

        int ok = JOptionPane.showConfirmDialog(this, form,
                existing == null ? "Add Property" : "Edit Property", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        if (unit.getText().trim().isEmpty()) { UiUtil.error(this, "Unit name is required."); return; }
        double pr;
        try { pr = Double.parseDouble(price.getText().trim()); }
        catch (NumberFormatException ex) { UiUtil.error(this, "Sell price must be a number."); return; }

        try {
            Property p = existing == null ? new Property() : existing;
            p.setUnitName(unit.getText().trim());
            p.setLocation(loc.getText().trim());
            p.setUnitType((String) type.getSelectedItem());
            p.setSellPrice(pr);
            p.setStatus((String) status.getSelectedItem());
            if (existing == null) dao.add(p); else dao.update(p);
            loadAll();
        } catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }
}
