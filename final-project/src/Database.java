import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Central place to open the SQLite connection.
 *
 * The database file lives at  final-project/db/app.db  and the path here is
 * RELATIVE to the folder you run the program from (the project root). That way
 * it works on every groupmate's computer with no editing required.
 */
public class Database {

    // Relative path: created automatically the first time the app runs.
    private static final String DB_URL = "jdbc:sqlite:db/app.db";

    /** Open a new connection to the SQLite database. */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /** Create the tables if they do not exist yet (safe to call every run). */
    public static void init() {
        String sql = """
            CREATE TABLE IF NOT EXISTS students (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                name      TEXT NOT NULL,
                course    TEXT
            );
            """;
        try (Connection conn = connect(); Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Could not initialize database", e);
        }
    }
}
