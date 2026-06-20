package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** SQLite connection factory + schema/seed initialization. */
public class Database {
    private static final String URL = "jdbc:sqlite:db/profriends.db";

    /** Open a connection with foreign keys enforced. */
    public static Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
        }
        return c;
    }

    /** Create tables if missing and seed the default admin. Safe to call every run. */
    public static void init() {
        String users = """
            CREATE TABLE IF NOT EXISTS users (
              user_id  INTEGER PRIMARY KEY AUTOINCREMENT,
              username TEXT NOT NULL UNIQUE,
              password TEXT NOT NULL,
              role     TEXT NOT NULL DEFAULT 'staff'
            )""";
        String buyers = """
            CREATE TABLE IF NOT EXISTS buyers (
              buyer_id       INTEGER PRIMARY KEY AUTOINCREMENT,
              full_name      TEXT NOT NULL,
              birthdate      TEXT,
              gov_id         TEXT,
              gender         TEXT,
              civil_status   TEXT,
              address        TEXT,
              monthly_income REAL NOT NULL,
              mobile_num     TEXT,
              email          TEXT
            )""";
        String properties = """
            CREATE TABLE IF NOT EXISTS properties (
              property_id INTEGER PRIMARY KEY AUTOINCREMENT,
              unit_name   TEXT NOT NULL,
              location    TEXT,
              unit_type   TEXT,
              sell_price  REAL NOT NULL,
              status      TEXT NOT NULL DEFAULT 'Available'
            )""";
        String loans = """
            CREATE TABLE IF NOT EXISTS loans (
              loan_id         INTEGER PRIMARY KEY AUTOINCREMENT,
              buyer_id        INTEGER NOT NULL REFERENCES buyers(buyer_id),
              property_id     INTEGER NOT NULL REFERENCES properties(property_id),
              finance_type    TEXT NOT NULL,
              loan_amount     REAL NOT NULL,
              downpayment     REAL NOT NULL,
              loan_term_years INTEGER NOT NULL,
              annual_rate     REAL NOT NULL,
              date_booked     TEXT
            )""";
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute(users);
            s.execute(buyers);
            s.execute(properties);
            s.execute(loans);
            // Seed default admin only if no admin exists.
            try (ResultSet rs = s.executeQuery(
                    "SELECT COUNT(*) FROM users WHERE username='admin'")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    s.execute("INSERT INTO users(username,password,role) "
                            + "VALUES('admin','admin123','admin')");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database init failed", e);
        }
    }
}
