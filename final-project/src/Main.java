import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Minimal but COMPLETE example that proves the whole stack works:
 * Java Swing GUI  +  SQLite database  +  relative file paths.
 *
 * Replace this with your real final project. Keep Database.java for the
 * connection logic, or expand it however your group prefers.
 */
public class Main {

    public static void main(String[] args) {
        Database.init(); // make sure the table exists before the UI opens
        SwingUtilities.invokeLater(Main::buildAndShowUI);
    }

    private static void buildAndShowUI() {
        JFrame frame = new JFrame("Final Project — Swing + SQLite Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(480, 360);
        frame.setLocationRelativeTo(null);

        // --- input row ---
        JTextField nameField = new JTextField(12);
        JTextField courseField = new JTextField(10);
        JButton addButton = new JButton("Add Student");

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Course:"));
        inputPanel.add(courseField);
        inputPanel.add(addButton);

        // --- list of students from the DB ---
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        refreshList(model);

        // --- wire up the button ---
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String course = courseField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a name.");
                return;
            }
            insertStudent(name, course);
            nameField.setText("");
            courseField.setText("");
            refreshList(model);
        });

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(list), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void insertStudent(String name, String course) {
        String sql = "INSERT INTO students(name, course) VALUES(?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, course);
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void refreshList(DefaultListModel<String> model) {
        model.clear();
        String sql = "SELECT id, name, course FROM students ORDER BY id";
        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addElement(rs.getInt("id") + ". " + rs.getString("name")
                        + "  (" + rs.getString("course") + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
