package ui;

import app.Session;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Profriends Inc. — Main Menu  (" + Session.current.getUsername()
                + " / " + Session.current.getRole() + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(5, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton buyers = new JButton("Manage Buyers");
        JButton props = new JButton("Manage Properties");
        JButton loans = new JButton("Manage Loans");
        JButton reports = new JButton("Reports");
        JButton logout = new JButton("Logout");

        buyers.addActionListener(e -> openPanel("Buyers", new BuyerPanel()));
        props.addActionListener(e -> openPanel("Properties", new PropertyPanel()));
        loans.addActionListener(e -> openPanel("Loans", new LoanPanel()));
        reports.addActionListener(e -> openPanel("Reports", new ReportPanel()));
        logout.addActionListener(e -> { Session.current = null; dispose();
            new LoginWindow().setVisible(true); });

        p.add(buyers); p.add(props); p.add(loans); p.add(reports); p.add(logout);
        add(p);
    }

    /**
     * Open a feature panel in a modal dialog with a "Back to Menu" button at the
     * top. Closing the dialog (Back button or window X) returns to this menu.
     */
    private void openPanel(String title, JPanel panel) {
        JDialog d = new JDialog(this, title, true);

        JButton back = new JButton("← Back to Menu");
        back.addActionListener(e -> d.dispose());
        JPanel backBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBar.add(back);

        JPanel content = new JPanel(new BorderLayout());
        content.add(backBar, BorderLayout.NORTH);
        content.add(panel, BorderLayout.CENTER);

        d.setContentPane(content);
        d.setSize(720, 500);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
}
