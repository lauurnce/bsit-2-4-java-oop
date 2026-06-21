package dao;

import db.Database;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {
    /** Returns the matching User, or null if credentials are invalid. */
    public User login(String username, String password) throws Exception {
        String sql = "SELECT user_id, username, password, role FROM users "
                   + "WHERE username = ? AND password = ?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("password"), rs.getString("role"));
                }
            }
        }
        return null;
    }
}
