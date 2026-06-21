package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbSmokeTest {
    public static void main(String[] a) throws Exception {
        Database.init();
        try (Connection c = Database.connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT username,role FROM users WHERE username='admin'")) {
            if (!rs.next()) throw new AssertionError("admin not seeded");
            if (!"admin".equals(rs.getString("role"))) throw new AssertionError("admin role wrong");
        }
        // Verify all 4 tables exist.
        try (Connection c = Database.connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")) {
            StringBuilder sb = new StringBuilder();
            while (rs.next()) sb.append(rs.getString(1)).append(" ");
            String tables = sb.toString();
            for (String t : new String[]{"buyers", "loans", "properties", "users"})
                if (!tables.contains(t)) throw new AssertionError("missing table " + t);
        }
        dao.UserDao udao = new dao.UserDao();
        if (udao.login("admin", "admin123") == null) throw new AssertionError("admin login failed");
        if (udao.login("admin", "wrong") != null) throw new AssertionError("bad login accepted");
        System.out.println("DB SMOKE OK");
    }
}
