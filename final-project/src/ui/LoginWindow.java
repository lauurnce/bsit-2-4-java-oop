package ui;

import app.Session;
import dao.UserDao;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    public LoginWindow() {
        setTitle("Profriends Inc. — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360, 200);
        setLocationRelativeTo(null);

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.add(new JLabel("Username:")); form.add(userField);
        form.add(new JLabel("Password:")); form.add(passField);
        form.add(new JLabel()); form.add(loginBtn);
        add(form);

        loginBtn.addActionListener(e -> doLogin(userField.getText(),
                new String(passField.getPassword())));
        getRootPane().setDefaultButton(loginBtn);
    }

    private void doLogin(String username, String password) {
        try {
            User u = new UserDao().login(username.trim(), password);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Session.current = u;
            dispose();
            new MainMenu().setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
