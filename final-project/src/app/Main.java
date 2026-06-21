package app;

import db.Database;
import ui.LoginWindow;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Database.init();
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}
