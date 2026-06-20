# Profriends Java Loan System Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a menu-driven Java Swing + SQLite property-loan management system (Profriends Inc.) that satisfies every OOP final-project rubric item.

**Architecture:** Layered `UI → DAO → Database`. Model classes hold OOP concepts (inheritance, polymorphism, abstraction, encapsulation). DAOs implement a `Crud` interface and own all SQL. Swing windows handle interaction only. SQLite via bundled JDBC jar; relative DB path for cross-machine runs.

**Tech Stack:** Java 17+ (built/tested on JDK 23), Swing, SQLite (`sqlite-jdbc-3.53.2.0.jar` in `final-project/lib/`), no build tool — compiled via `javac -cp "lib/*"`.

## Global Constraints

- All code lives under `final-project/src/` in packages: `app`, `model`, `dao`, `db`, `ui`.
- Compile with: `javac -cp "lib/*" -d bin $(find src -name "*.java")` run from `final-project/`.
- Run with: `java -cp "bin:lib/*" app.Main` (Windows: `bin;lib/*`).
- DB path is relative: `jdbc:sqlite:db/profriends.db`. Never absolute.
- `PRAGMA foreign_keys = ON` on every connection.
- Default seeded admin: username `admin`, password `admin123`, role `admin`.
- All model fields `private` with getters/setters. Every model has a default + parameterized constructor.
- All DB and numeric-parse operations wrapped in `try-catch`; user sees `JOptionPane` messages, never raw stack traces.
- Delete is admin-only.
- Existing demo files `final-project/src/Main.java` and `final-project/src/Database.java` are DELETED before new code.
- `final-project/db/*.db` and `final-project/bin/` remain git-ignored (already configured).

---

### Task 1: Remove demo, set up package dirs, Database with schema + seed

**Files:**
- Delete: `final-project/src/Main.java`, `final-project/src/Database.java`
- Create: `final-project/src/db/Database.java`
- Test: `final-project/src/db/DbSmokeTest.java` (temporary, headless `main`)

**Interfaces:**
- Consumes: nothing (foundation).
- Produces:
  - `db.Database.connect() -> java.sql.Connection` (foreign_keys ON)
  - `db.Database.init() -> void` (creates 4 tables if absent, seeds admin)
  - Tables/columns exactly as spec §3.

- [ ] **Step 1: Delete the demo files**

```bash
cd final-project
rm -f src/Main.java src/Database.java
mkdir -p src/app src/model src/dao src/db src/ui
```

- [ ] **Step 2: Write `db/Database.java`**

```java
package db;

import java.sql.Connection;
import java.sql.DriverManager;
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
            var rs = s.executeQuery("SELECT COUNT(*) FROM users WHERE username='admin'");
            rs.next();
            if (rs.getInt(1) == 0) {
                s.execute("INSERT INTO users(username,password,role) "
                        + "VALUES('admin','admin123','admin')");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database init failed", e);
        }
    }
}
```

- [ ] **Step 3: Write a temporary headless smoke test**

```java
package db;

import java.sql.*;

public class DbSmokeTest {
    public static void main(String[] a) throws Exception {
        Database.init();
        try (Connection c = Database.connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT username,role FROM users WHERE username='admin'")) {
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
            for (String t : new String[]{"buyers","loans","properties","users"})
                if (!tables.contains(t)) throw new AssertionError("missing table " + t);
        }
        System.out.println("DB SMOKE OK");
    }
}
```

- [ ] **Step 4: Compile and run the smoke test**

Run:
```bash
cd final-project && rm -f db/profriends.db && mkdir -p bin db
javac -cp "lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" db.DbSmokeTest
```
Expected: prints `DB SMOKE OK`, no exceptions.

- [ ] **Step 5: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src
git commit -m "feat: add SQLite Database layer with schema and seeded admin"
```

---

### Task 2: Model classes — Person, Buyer, Property, User

**Files:**
- Create: `final-project/src/model/Person.java`, `Buyer.java`, `Property.java`, `User.java`

**Interfaces:**
- Consumes: nothing.
- Produces:
  - `abstract model.Person` with private `id:int`, `fullName:String`; getters/setters; `abstract String describe()`.
  - `model.Buyer extends Person` fields: `birthdate,govId,gender,civilStatus,address:String`, `monthlyIncome:double`, `mobileNum,email:String`. Default + full parameterized constructor. `describe()` overridden.
  - `model.Property` fields: `id:int, unitName,location,unitType:String, sellPrice:double, status:String`. Default + parameterized ctor; getters/setters.
  - `model.User` fields: `id:int, username,password,role:String`; getters; `isAdmin():boolean`.

- [ ] **Step 1: Write `model/Person.java`**

```java
package model;

/** Abstract base for people in the system (demonstrates abstraction + inheritance). */
public abstract class Person {
    private int id;
    private String fullName;

    public Person() {}
    public Person(int id, String fullName) { this.id = id; this.fullName = fullName; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    /** Each subclass describes itself (polymorphism). */
    public abstract String describe();
}
```

- [ ] **Step 2: Write `model/Buyer.java`**

```java
package model;

public class Buyer extends Person {
    private String birthdate, govId, gender, civilStatus, address, mobileNum, email;
    private double monthlyIncome;

    public Buyer() { super(); }

    public Buyer(int id, String fullName, String birthdate, String govId, String gender,
                 String civilStatus, String address, double monthlyIncome,
                 String mobileNum, String email) {
        super(id, fullName);
        this.birthdate = birthdate; this.govId = govId; this.gender = gender;
        this.civilStatus = civilStatus; this.address = address;
        this.monthlyIncome = monthlyIncome; this.mobileNum = mobileNum; this.email = email;
    }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String v) { birthdate = v; }
    public String getGovId() { return govId; }
    public void setGovId(String v) { govId = v; }
    public String getGender() { return gender; }
    public void setGender(String v) { gender = v; }
    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String v) { civilStatus = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { address = v; }
    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double v) { monthlyIncome = v; }
    public String getMobileNum() { return mobileNum; }
    public void setMobileNum(String v) { mobileNum = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }

    @Override
    public String describe() {
        return "Buyer #" + getId() + ": " + getFullName();
    }
}
```

- [ ] **Step 3: Write `model/Property.java`**

```java
package model;

public class Property {
    private int id;
    private String unitName, location, unitType, status;
    private double sellPrice;

    public Property() {}

    public Property(int id, String unitName, String location, String unitType,
                    double sellPrice, String status) {
        this.id = id; this.unitName = unitName; this.location = location;
        this.unitType = unitType; this.sellPrice = sellPrice; this.status = status;
    }

    public int getId() { return id; }
    public void setId(int v) { id = v; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String v) { unitName = v; }
    public String getLocation() { return location; }
    public void setLocation(String v) { location = v; }
    public String getUnitType() { return unitType; }
    public void setUnitType(String v) { unitType = v; }
    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double v) { sellPrice = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }
}
```

- [ ] **Step 4: Write `model/User.java`**

```java
package model;

public class User {
    private int id;
    private String username, password, role;

    public User() {}
    public User(int id, String username, String password, String role) {
        this.id = id; this.username = username; this.password = password; this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isAdmin() { return "admin".equalsIgnoreCase(role); }
}
```

- [ ] **Step 5: Compile**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java")
```
Expected: compiles with no errors.

- [ ] **Step 6: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src/model
git commit -m "feat: add Person/Buyer/Property/User model classes"
```

---

### Task 3: Loan hierarchy (abstract + 2 subclasses) with TDD math

**Files:**
- Create: `final-project/src/model/Loan.java`, `BankFinancedLoan.java`, `InHouseLoan.java`
- Test: `final-project/src/model/LoanMathTest.java` (temporary headless `main`)

**Interfaces:**
- Consumes: nothing.
- Produces:
  - `abstract model.Loan` fields: `loanId,buyerId,propertyId:int`, `loanAmount,downpayment,annualRate:double`, `loanTermYears:int`, `dateBooked:String`, `financeType:String`. Getters/setters. Concrete `double principal()` = `loanAmount - downpayment`. `abstract double computeMonthlyAmortization()`. `abstract double computeTotalPayable()`.
  - `model.BankFinancedLoan extends Loan` (financeType "Bank", default rate 6.5).
  - `model.InHouseLoan extends Loan` (financeType "InHouse", default rate 9.5, processing fee in total).
  - Standard amortization: `r=annualRate/12/100; n=years*12; A = r>0 ? P*r/(1-(1+r)^-n) : P/n`.

- [ ] **Step 1: Write the failing math test**

```java
package model;

public class LoanMathTest {
    static void assertClose(double a, double b, double tol, String msg) {
        if (Math.abs(a - b) > tol) throw new AssertionError(msg + " expected " + b + " got " + a);
    }
    public static void main(String[] args) {
        // P = 1,000,000 - 200,000 = 800,000; rate 6.5%/yr; 10 yrs => 120 months
        BankFinancedLoan bank = new BankFinancedLoan();
        bank.setLoanAmount(1_000_000); bank.setDownpayment(200_000);
        bank.setAnnualRate(6.5); bank.setLoanTermYears(10);
        // Hand/spreadsheet value ~ 9085.26
        assertClose(bank.computeMonthlyAmortization(), 9085.26, 1.0, "bank monthly");

        // InHouse same numbers but rate 9.5%
        InHouseLoan in = new InHouseLoan();
        in.setLoanAmount(1_000_000); in.setDownpayment(200_000);
        in.setAnnualRate(9.5); in.setLoanTermYears(10);
        // ~ 10349.92
        assertClose(in.computeMonthlyAmortization(), 10349.92, 1.0, "inhouse monthly");

        // Polymorphism: same reference type, different behavior
        Loan poly = new BankFinancedLoan();
        poly.setLoanAmount(500_000); poly.setDownpayment(0);
        poly.setAnnualRate(0); poly.setLoanTermYears(5); // zero-rate branch: P/n = 500000/60
        assertClose(poly.computeMonthlyAmortization(), 8333.33, 0.5, "zero-rate monthly");

        System.out.println("LOAN MATH OK");
    }
}
```

- [ ] **Step 2: Write `model/Loan.java` (abstract)**

```java
package model;

public abstract class Loan {
    private int loanId, buyerId, propertyId, loanTermYears;
    private double loanAmount, downpayment, annualRate;
    private String dateBooked, financeType;

    public Loan() {}

    public int getLoanId() { return loanId; }
    public void setLoanId(int v) { loanId = v; }
    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int v) { buyerId = v; }
    public int getPropertyId() { return propertyId; }
    public void setPropertyId(int v) { propertyId = v; }
    public int getLoanTermYears() { return loanTermYears; }
    public void setLoanTermYears(int v) { loanTermYears = v; }
    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double v) { loanAmount = v; }
    public double getDownpayment() { return downpayment; }
    public void setDownpayment(double v) { downpayment = v; }
    public double getAnnualRate() { return annualRate; }
    public void setAnnualRate(double v) { annualRate = v; }
    public String getDateBooked() { return dateBooked; }
    public void setDateBooked(String v) { dateBooked = v; }
    public String getFinanceType() { return financeType; }
    public void setFinanceType(String v) { financeType = v; }

    /** Amount actually financed. */
    public double principal() { return loanAmount - downpayment; }

    /** Shared amortization formula used by subclasses. */
    protected double amortize() {
        double p = principal();
        int n = loanTermYears * 12;
        if (n <= 0) return 0;
        double r = annualRate / 12.0 / 100.0;
        if (r <= 0) return p / n;
        return p * r / (1 - Math.pow(1 + r, -n));
    }

    public abstract double computeMonthlyAmortization();
    public abstract double computeTotalPayable();
}
```

- [ ] **Step 3: Write `model/BankFinancedLoan.java`**

```java
package model;

public class BankFinancedLoan extends Loan {
    public static final double DEFAULT_RATE = 6.5;

    public BankFinancedLoan() {
        setFinanceType("Bank");
        setAnnualRate(DEFAULT_RATE);
    }

    @Override
    public double computeMonthlyAmortization() { return amortize(); }

    @Override
    public double computeTotalPayable() {
        return computeMonthlyAmortization() * getLoanTermYears() * 12 + getDownpayment();
    }
}
```

- [ ] **Step 4: Write `model/InHouseLoan.java`**

```java
package model;

public class InHouseLoan extends Loan {
    public static final double DEFAULT_RATE = 9.5;
    public static final double PROCESSING_FEE = 25_000.0;

    public InHouseLoan() {
        setFinanceType("InHouse");
        setAnnualRate(DEFAULT_RATE);
    }

    @Override
    public double computeMonthlyAmortization() { return amortize(); }

    @Override
    public double computeTotalPayable() {
        return computeMonthlyAmortization() * getLoanTermYears() * 12
                + getDownpayment() + PROCESSING_FEE;
    }
}
```

- [ ] **Step 5: Run the math test (should now pass)**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" model.LoanMathTest
```
Expected: prints `LOAN MATH OK`.

- [ ] **Step 6: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src/model
git commit -m "feat: add abstract Loan with Bank/InHouse subclasses and math tests"
```

---

### Task 4: Crud interface + UserDao with login + DAO smoke test

**Files:**
- Create: `final-project/src/dao/Crud.java`, `final-project/src/dao/UserDao.java`
- Test: extend `final-project/src/db/DbSmokeTest.java`

**Interfaces:**
- Consumes: `db.Database`, `model.User`.
- Produces:
  - `interface dao.Crud<T>` methods: `void add(T) throws Exception; java.util.List<T> getAll() throws Exception; java.util.List<T> search(String keyword) throws Exception; void update(T) throws Exception; void delete(int id) throws Exception;`
  - `dao.UserDao` method: `model.User login(String username, String password) throws Exception` (returns null if no match).

- [ ] **Step 1: Write `dao/Crud.java`**

```java
package dao;

import java.util.List;

/** Generic CRUD contract (abstraction) implemented by every entity DAO. */
public interface Crud<T> {
    void add(T item) throws Exception;
    List<T> getAll() throws Exception;
    List<T> search(String keyword) throws Exception;
    void update(T item) throws Exception;
    void delete(int id) throws Exception;
}
```

- [ ] **Step 2: Write `dao/UserDao.java`**

```java
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
```

- [ ] **Step 3: Add a login assertion to `db/DbSmokeTest.java`**

Insert before the final `System.out.println("DB SMOKE OK");`:

```java
        dao.UserDao udao = new dao.UserDao();
        if (udao.login("admin", "admin123") == null) throw new AssertionError("admin login failed");
        if (udao.login("admin", "wrong") != null) throw new AssertionError("bad login accepted");
```

- [ ] **Step 4: Compile and run the smoke test**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" db.DbSmokeTest
```
Expected: prints `DB SMOKE OK`.

- [ ] **Step 5: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src/dao final-project/src/db
git commit -m "feat: add Crud interface and UserDao with login validation"
```

---

### Task 5: BuyerDao + PropertyDao (CRUD + overloaded search) with TDD

**Files:**
- Create: `final-project/src/dao/BuyerDao.java`, `final-project/src/dao/PropertyDao.java`
- Test: `final-project/src/dao/DaoCrudTest.java` (temporary headless `main`)

**Interfaces:**
- Consumes: `db.Database`, `dao.Crud`, `model.Buyer`, `model.Property`.
- Produces:
  - `dao.BuyerDao implements Crud<Buyer>` plus overloaded `List<Buyer> search(String keyword)` (matches name/email) and `Buyer findById(int id)`.
  - `dao.PropertyDao implements Crud<Property>` plus `Property findById(int id)`.

- [ ] **Step 1: Write the failing CRUD test**

```java
package dao;

import model.Buyer;
import model.Property;
import java.util.List;

public class DaoCrudTest {
    public static void main(String[] a) throws Exception {
        db.Database.init();
        BuyerDao bd = new BuyerDao();
        Buyer b = new Buyer(0, "Juan Cruz", "1990-01-01", "GID-1", "M", "S",
                "Manila", 50000, "09171234567", "juan@example.com");
        bd.add(b);
        List<Buyer> all = bd.getAll();
        if (all.isEmpty()) throw new AssertionError("buyer not added");
        Buyer added = all.get(all.size() - 1);
        added.setFullName("Juan Updated");
        bd.update(added);
        if (bd.findById(added.getId()) == null
            || !bd.findById(added.getId()).getFullName().equals("Juan Updated"))
            throw new AssertionError("update failed");
        if (bd.search("Updated").isEmpty()) throw new AssertionError("search failed");

        PropertyDao pd = new PropertyDao();
        Property p = new Property(0, "Unit A", "Cavite", "Townhouse", 2_500_000, "Available");
        pd.add(p);
        Property padded = pd.getAll().get(pd.getAll().size() - 1);
        if (pd.findById(padded.getId()) == null) throw new AssertionError("property findById failed");

        // cleanup so reruns stay clean
        bd.delete(added.getId());
        pd.delete(padded.getId());
        System.out.println("DAO CRUD OK");
    }
}
```

- [ ] **Step 2: Run to confirm it fails (no BuyerDao yet)**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java") 2>&1 | head
```
Expected: compile error — `BuyerDao`/`PropertyDao` not found.

- [ ] **Step 3: Write `dao/BuyerDao.java`**

```java
package dao;

import db.Database;
import model.Buyer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BuyerDao implements Crud<Buyer> {

    @Override
    public void add(Buyer b) throws Exception {
        String sql = "INSERT INTO buyers(full_name,birthdate,gov_id,gender,civil_status,"
                   + "address,monthly_income,mobile_num,email) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, b);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Buyer> getAll() throws Exception {
        return query("SELECT * FROM buyers ORDER BY buyer_id", null);
    }

    /** Overloaded search by free-text keyword (name or email). */
    @Override
    public List<Buyer> search(String keyword) throws Exception {
        return query("SELECT * FROM buyers WHERE full_name LIKE ? OR email LIKE ? "
                   + "ORDER BY buyer_id", "%" + keyword + "%");
    }

    public Buyer findById(int id) throws Exception {
        String sql = "SELECT * FROM buyers WHERE buyer_id = ?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public void update(Buyer b) throws Exception {
        String sql = "UPDATE buyers SET full_name=?,birthdate=?,gov_id=?,gender=?,"
                   + "civil_status=?,address=?,monthly_income=?,mobile_num=?,email=? "
                   + "WHERE buyer_id=?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, b);
            ps.setInt(10, b.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM buyers WHERE buyer_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Buyer b) throws Exception {
        ps.setString(1, b.getFullName());
        ps.setString(2, b.getBirthdate());
        ps.setString(3, b.getGovId());
        ps.setString(4, b.getGender());
        ps.setString(5, b.getCivilStatus());
        ps.setString(6, b.getAddress());
        ps.setDouble(7, b.getMonthlyIncome());
        ps.setString(8, b.getMobileNum());
        ps.setString(9, b.getEmail());
    }

    private List<Buyer> query(String sql, String likeArg) throws Exception {
        List<Buyer> out = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (likeArg != null) { ps.setString(1, likeArg); ps.setString(2, likeArg); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    private Buyer map(ResultSet rs) throws Exception {
        return new Buyer(rs.getInt("buyer_id"), rs.getString("full_name"),
                rs.getString("birthdate"), rs.getString("gov_id"), rs.getString("gender"),
                rs.getString("civil_status"), rs.getString("address"),
                rs.getDouble("monthly_income"), rs.getString("mobile_num"),
                rs.getString("email"));
    }
}
```

- [ ] **Step 4: Write `dao/PropertyDao.java`**

```java
package dao;

import db.Database;
import model.Property;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PropertyDao implements Crud<Property> {

    @Override
    public void add(Property p) throws Exception {
        String sql = "INSERT INTO properties(unit_name,location,unit_type,sell_price,status) "
                   + "VALUES(?,?,?,?,?)";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Property> getAll() throws Exception {
        return query("SELECT * FROM properties ORDER BY property_id", null);
    }

    @Override
    public List<Property> search(String keyword) throws Exception {
        return query("SELECT * FROM properties WHERE unit_name LIKE ? OR location LIKE ? "
                   + "ORDER BY property_id", "%" + keyword + "%");
    }

    public Property findById(int id) throws Exception {
        String sql = "SELECT * FROM properties WHERE property_id = ?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public void update(Property p) throws Exception {
        String sql = "UPDATE properties SET unit_name=?,location=?,unit_type=?,"
                   + "sell_price=?,status=? WHERE property_id=?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p);
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM properties WHERE property_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Property p) throws Exception {
        ps.setString(1, p.getUnitName());
        ps.setString(2, p.getLocation());
        ps.setString(3, p.getUnitType());
        ps.setDouble(4, p.getSellPrice());
        ps.setString(5, p.getStatus());
    }

    private List<Property> query(String sql, String likeArg) throws Exception {
        List<Property> out = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (likeArg != null) { ps.setString(1, likeArg); ps.setString(2, likeArg); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    private Property map(ResultSet rs) throws Exception {
        return new Property(rs.getInt("property_id"), rs.getString("unit_name"),
                rs.getString("location"), rs.getString("unit_type"),
                rs.getDouble("sell_price"), rs.getString("status"));
    }
}
```

- [ ] **Step 5: Run the CRUD test (should pass)**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" dao.DaoCrudTest
```
Expected: prints `DAO CRUD OK`.

- [ ] **Step 6: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src/dao
git commit -m "feat: add BuyerDao and PropertyDao with CRUD and search"
```

---

### Task 6: LoanDao (polymorphic instantiation by finance_type) with TDD

**Files:**
- Create: `final-project/src/dao/LoanDao.java`
- Test: extend `final-project/src/dao/DaoCrudTest.java`

**Interfaces:**
- Consumes: `db.Database`, `dao.Crud`, `model.Loan`, `model.BankFinancedLoan`, `model.InHouseLoan`, plus `BuyerDao`/`PropertyDao` for FK setup in the test.
- Produces:
  - `dao.LoanDao implements Crud<Loan>`. `getAll()`/`search()`/`findById()` return the correct subclass instance based on `finance_type` (`"InHouse"` → `InHouseLoan`, else `BankFinancedLoan`). `search(keyword)` matches `finance_type` or buyer/property id text.

- [ ] **Step 1: Add failing loan assertions to `dao/DaoCrudTest.java`**

Replace the cleanup + final print at the end of `main` with:

```java
        // --- Loan polymorphism via DAO ---
        // need valid FK rows; re-add a buyer and property
        Buyer fb = new Buyer(0, "Loan Buyer", null, null, null, null, null, 60000, null, null);
        bd.add(fb);
        int fbId = bd.getAll().get(bd.getAll().size() - 1).getId();
        Property fp = new Property(0, "Loan Unit", "QC", "Condo", 3_000_000, "Available");
        pd.add(fp);
        int fpId = pd.getAll().get(pd.getAll().size() - 1).getId();

        LoanDao ld = new LoanDao();
        model.InHouseLoan loan = new model.InHouseLoan();
        loan.setBuyerId(fbId); loan.setPropertyId(fpId);
        loan.setLoanAmount(3_000_000); loan.setDownpayment(600_000);
        loan.setAnnualRate(9.5); loan.setLoanTermYears(15); loan.setDateBooked("2026-06-21");
        ld.add(loan);
        model.Loan fetched = ld.getAll().get(ld.getAll().size() - 1);
        if (!(fetched instanceof model.InHouseLoan))
            throw new AssertionError("DAO did not rebuild InHouseLoan subtype");
        if (fetched.computeMonthlyAmortization() <= 0)
            throw new AssertionError("amortization not computed");

        ld.delete(fetched.getLoanId());
        bd.delete(fbId);
        pd.delete(fpId);

        // original cleanup
        bd.delete(added.getId());
        pd.delete(padded.getId());
        System.out.println("DAO CRUD OK");
```

(Remove the old final cleanup/print lines so they are not duplicated.)

- [ ] **Step 2: Run to confirm failure (no LoanDao)**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java") 2>&1 | head
```
Expected: compile error — `LoanDao` not found.

- [ ] **Step 3: Write `dao/LoanDao.java`**

```java
package dao;

import db.Database;
import model.BankFinancedLoan;
import model.InHouseLoan;
import model.Loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LoanDao implements Crud<Loan> {

    @Override
    public void add(Loan l) throws Exception {
        String sql = "INSERT INTO loans(buyer_id,property_id,finance_type,loan_amount,"
                   + "downpayment,loan_term_years,annual_rate,date_booked) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, l);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Loan> getAll() throws Exception {
        return query("SELECT * FROM loans ORDER BY loan_id", null);
    }

    @Override
    public List<Loan> search(String keyword) throws Exception {
        return query("SELECT * FROM loans WHERE finance_type LIKE ? "
                   + "OR CAST(buyer_id AS TEXT) LIKE ? ORDER BY loan_id", "%" + keyword + "%");
    }

    public Loan findById(int id) throws Exception {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM loans WHERE loan_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public void update(Loan l) throws Exception {
        String sql = "UPDATE loans SET buyer_id=?,property_id=?,finance_type=?,loan_amount=?,"
                   + "downpayment=?,loan_term_years=?,annual_rate=?,date_booked=? WHERE loan_id=?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, l);
            ps.setInt(9, l.getLoanId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM loans WHERE loan_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Loan l) throws Exception {
        ps.setInt(1, l.getBuyerId());
        ps.setInt(2, l.getPropertyId());
        ps.setString(3, l.getFinanceType());
        ps.setDouble(4, l.getLoanAmount());
        ps.setDouble(5, l.getDownpayment());
        ps.setInt(6, l.getLoanTermYears());
        ps.setDouble(7, l.getAnnualRate());
        ps.setString(8, l.getDateBooked());
    }

    private List<Loan> query(String sql, String likeArg) throws Exception {
        List<Loan> out = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (likeArg != null) { ps.setString(1, likeArg); ps.setString(2, likeArg); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    /** Polymorphic factory: rebuild the correct subclass from the stored finance_type. */
    private Loan map(ResultSet rs) throws Exception {
        String type = rs.getString("finance_type");
        Loan l = "InHouse".equalsIgnoreCase(type) ? new InHouseLoan() : new BankFinancedLoan();
        l.setLoanId(rs.getInt("loan_id"));
        l.setBuyerId(rs.getInt("buyer_id"));
        l.setPropertyId(rs.getInt("property_id"));
        l.setFinanceType(type);
        l.setLoanAmount(rs.getDouble("loan_amount"));
        l.setDownpayment(rs.getDouble("downpayment"));
        l.setLoanTermYears(rs.getInt("loan_term_years"));
        l.setAnnualRate(rs.getDouble("annual_rate"));
        l.setDateBooked(rs.getString("date_booked"));
        return l;
    }
}
```

- [ ] **Step 4: Run the CRUD test (should pass)**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" dao.DaoCrudTest
```
Expected: prints `DAO CRUD OK`.

- [ ] **Step 5: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src/dao
git commit -m "feat: add LoanDao with polymorphic subclass reconstruction"
```

---

### Task 7: Session + LoginWindow + Main entry point

**Files:**
- Create: `final-project/src/app/Session.java`, `final-project/src/ui/LoginWindow.java`, `final-project/src/app/Main.java`

**Interfaces:**
- Consumes: `db.Database`, `dao.UserDao`, `model.User`, `ui.MainMenu` (created in Task 8 — Main references it; build Task 8 before running the GUI).
- Produces:
  - `app.Session` static holder: `static model.User current; static boolean isAdmin()`.
  - `ui.LoginWindow extends JFrame` — username/password fields, validates via `UserDao.login`, error dialog on failure, opens `MainMenu` on success.
  - `app.Main.main` → `Database.init()` then shows `LoginWindow`.

- [ ] **Step 1: Write `app/Session.java`**

```java
package app;

import model.User;

/** Holds the logged-in user for authorization checks across the UI. */
public class Session {
    public static User current;
    public static boolean isAdmin() { return current != null && current.isAdmin(); }
}
```

- [ ] **Step 2: Write `ui/LoginWindow.java`**

```java
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
```

- [ ] **Step 3: Write `app/Main.java`**

```java
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
```

- [ ] **Step 4: Compile (will fail until MainMenu exists — expected)**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java") 2>&1 | head
```
Expected: error referencing `ui.MainMenu` not found. This is resolved in Task 8; do not commit a broken build — proceed to Task 8 and commit together.

- [ ] **Step 5: (Deferred commit)** — commit happens at end of Task 8 once the build is green.

---

### Task 8: MainMenu shell + table utility, get a green compiling GUI

**Files:**
- Create: `final-project/src/ui/MainMenu.java`, `final-project/src/ui/UiUtil.java`

**Interfaces:**
- Consumes: `app.Session`, and the panels `ui.BuyerPanel`, `ui.PropertyPanel`, `ui.LoanPanel`, `ui.ReportWindow` (Tasks 9–12). To keep the build green now, MainMenu opens panels that are created as minimal stubs here and fleshed out in later tasks.
- Produces:
  - `ui.UiUtil.error(Component,String)`, `ui.UiUtil.info(Component,String)`, `ui.UiUtil.confirm(Component,String):boolean` helpers.
  - `ui.MainMenu extends JFrame` with buttons: Buyers, Properties, Loans, Reports, Logout. Buttons open the corresponding panel in a `JDialog` or new frame.

- [ ] **Step 1: Write `ui/UiUtil.java`**

```java
package ui;

import javax.swing.*;
import java.awt.*;

/** Small Swing helpers for consistent dialogs. */
public class UiUtil {
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
```

- [ ] **Step 2: Write minimal stub panels so MainMenu compiles**

Create `final-project/src/ui/BuyerPanel.java`, `PropertyPanel.java`, `LoanPanel.java` each as:

```java
package ui;

import javax.swing.*;

/** STUB — full implementation in a later task. */
public class BuyerPanel extends JPanel {
    public BuyerPanel() { add(new JLabel("Buyers — coming in Task 9")); }
}
```
(Repeat with class names `PropertyPanel` / `LoanPanel` and matching labels.)

And `final-project/src/ui/ReportWindow.java`:

```java
package ui;

import javax.swing.*;

/** STUB — full implementation in Task 12. */
public class ReportWindow extends JFrame {
    public ReportWindow() {
        setTitle("Reports");
        setSize(400, 300);
        setLocationRelativeTo(null);
        add(new JLabel("Reports — coming in Task 12"));
    }
}
```

- [ ] **Step 3: Write `ui/MainMenu.java`**

```java
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
        reports.addActionListener(e -> new ReportWindow().setVisible(true));
        logout.addActionListener(e -> { Session.current = null; dispose();
            new LoginWindow().setVisible(true); });

        p.add(buyers); p.add(props); p.add(loans); p.add(reports); p.add(logout);
        add(p);
    }

    private void openPanel(String title, JPanel panel) {
        JDialog d = new JDialog(this, title, true);
        d.setContentPane(panel);
        d.setSize(720, 460);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
}
```

- [ ] **Step 4: Compile the whole project green**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java")
```
Expected: compiles with no errors.

- [ ] **Step 5: Commit Tasks 7+8 together**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src
git commit -m "feat: add login, session, main menu and UI stubs (compiling GUI)"
```

---

### Task 9: BuyerPanel — full CRUD UI (PARALLELIZABLE)

> Tasks 9, 10, 11 are independent of each other (each touches only its own file + shared, already-built `UiUtil`/DAOs). They may be built in parallel by separate agents, then merged. Each must end with the whole project compiling.

**Files:**
- Modify (replace stub): `final-project/src/ui/BuyerPanel.java`

**Interfaces:**
- Consumes: `dao.BuyerDao`, `model.Buyer`, `ui.UiUtil`, `app.Session`.
- Produces: a `JPanel` with a `JTable`, Refresh, Search field, Add, Edit, Delete buttons. Delete disabled unless `Session.isAdmin()`.

- [ ] **Step 1: Replace `ui/BuyerPanel.java` with full implementation**

```java
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
```

- [ ] **Step 2: Compile**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java")
```
Expected: compiles clean.

- [ ] **Step 3: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src/ui/BuyerPanel.java
git commit -m "feat: implement BuyerPanel CRUD UI"
```

---

### Task 10: PropertyPanel — full CRUD UI (PARALLELIZABLE)

**Files:**
- Modify (replace stub): `final-project/src/ui/PropertyPanel.java`

**Interfaces:**
- Consumes: `dao.PropertyDao`, `model.Property`, `ui.UiUtil`, `app.Session`.
- Produces: `JPanel` with JTable + Search/Refresh/Add/Edit/Delete (delete admin-only).

- [ ] **Step 1: Replace `ui/PropertyPanel.java`**

```java
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
        if (!UiUtil.confirm(this, "Delete property #" + id + "?")) return;
        try { dao.delete(id); loadAll(); }
        catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
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
```

- [ ] **Step 2: Compile**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java")
```
Expected: clean compile.

- [ ] **Step 3: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src/ui/PropertyPanel.java
git commit -m "feat: implement PropertyPanel CRUD UI"
```

---

### Task 11: LoanPanel — full CRUD UI with computed columns (PARALLELIZABLE)

**Files:**
- Modify (replace stub): `final-project/src/ui/LoanPanel.java`

**Interfaces:**
- Consumes: `dao.LoanDao`, `dao.BuyerDao`, `dao.PropertyDao`, `model.Loan`, `model.BankFinancedLoan`, `model.InHouseLoan`, `ui.UiUtil`, `app.Session`.
- Produces: `JPanel` with JTable (incl. computed Monthly + Total columns via polymorphism) + Search/Refresh/Add/Edit/Delete (delete admin-only). Buyer/Property chosen by ID entry (validated against DAO `findById`).

- [ ] **Step 1: Replace `ui/LoanPanel.java`**

```java
package ui;

import app.Session;
import dao.BuyerDao;
import dao.LoanDao;
import dao.PropertyDao;
import model.BankFinancedLoan;
import model.InHouseLoan;
import model.Loan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoanPanel extends JPanel {
    private final LoanDao dao = new LoanDao();
    private final BuyerDao buyerDao = new BuyerDao();
    private final PropertyDao propertyDao = new PropertyDao();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "BuyerID", "PropID", "Type", "Amount", "DP", "Yrs",
                    "Rate%", "Monthly", "Total"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(16);

    public LoanPanel() {
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");
        top.add(new JLabel("Search (type/buyer):")); top.add(searchField);
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
            List<Loan> r = dao.search(searchField.getText().trim());
            if (r.isEmpty()) UiUtil.info(this, "No record found.");
            fill(r);
        } catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }

    private void fill(List<Loan> rows) {
        model.setRowCount(0);
        for (Loan l : rows)
            model.addRow(new Object[]{l.getLoanId(), l.getBuyerId(), l.getPropertyId(),
                    l.getFinanceType(), l.getLoanAmount(), l.getDownpayment(),
                    l.getLoanTermYears(), l.getAnnualRate(),
                    Math.round(l.computeMonthlyAmortization() * 100) / 100.0,
                    Math.round(l.computeTotalPayable() * 100) / 100.0});
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
        if (!UiUtil.confirm(this, "Delete loan #" + id + "?")) return;
        try { dao.delete(id); loadAll(); }
        catch (Exception ex) { UiUtil.error(this, ex.getMessage()); }
    }

    private void addOrEdit(Loan existing) {
        JComboBox<String> type = new JComboBox<>(new String[]{"Bank", "InHouse"});
        if (existing != null) type.setSelectedItem(existing.getFinanceType());
        JTextField buyerId = new JTextField(existing == null ? "" : String.valueOf(existing.getBuyerId()));
        JTextField propId = new JTextField(existing == null ? "" : String.valueOf(existing.getPropertyId()));
        JTextField amount = new JTextField(existing == null ? "" : String.valueOf(existing.getLoanAmount()));
        JTextField dp = new JTextField(existing == null ? "" : String.valueOf(existing.getDownpayment()));
        JTextField years = new JTextField(existing == null ? "" : String.valueOf(existing.getLoanTermYears()));
        JTextField rate = new JTextField(existing == null ? "" : String.valueOf(existing.getAnnualRate()));
        JTextField date = new JTextField(existing == null ? "" : existing.getDateBooked());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Finance Type:")); form.add(type);
        form.add(new JLabel("Buyer ID*:")); form.add(buyerId);
        form.add(new JLabel("Property ID*:")); form.add(propId);
        form.add(new JLabel("Loan Amount*:")); form.add(amount);
        form.add(new JLabel("Downpayment*:")); form.add(dp);
        form.add(new JLabel("Term (years)*:")); form.add(years);
        form.add(new JLabel("Annual Rate %*:")); form.add(rate);
        form.add(new JLabel("Date Booked:")); form.add(date);

        int ok = JOptionPane.showConfirmDialog(this, form,
                existing == null ? "Add Loan" : "Edit Loan", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int bId = Integer.parseInt(buyerId.getText().trim());
            int pId = Integer.parseInt(propId.getText().trim());
            double amt = Double.parseDouble(amount.getText().trim());
            double down = Double.parseDouble(dp.getText().trim());
            int yrs = Integer.parseInt(years.getText().trim());
            double rt = Double.parseDouble(rate.getText().trim());

            if (buyerDao.findById(bId) == null) { UiUtil.error(this, "Buyer ID not found."); return; }
            if (propertyDao.findById(pId) == null) { UiUtil.error(this, "Property ID not found."); return; }
            if (down > amt) { UiUtil.error(this, "Downpayment cannot exceed loan amount."); return; }

            String t = (String) type.getSelectedItem();
            Loan l = "InHouse".equals(t) ? new InHouseLoan() : new BankFinancedLoan();
            if (existing != null) l.setLoanId(existing.getLoanId());
            l.setFinanceType(t);
            l.setBuyerId(bId); l.setPropertyId(pId);
            l.setLoanAmount(amt); l.setDownpayment(down);
            l.setLoanTermYears(yrs); l.setAnnualRate(rt);
            l.setDateBooked(date.getText().trim());

            if (existing == null) dao.add(l); else dao.update(l);
            loadAll();
        } catch (NumberFormatException ex) {
            UiUtil.error(this, "IDs, amounts, term and rate must be numbers.");
        } catch (Exception ex) {
            UiUtil.error(this, ex.getMessage());
        }
    }
}
```

- [ ] **Step 2: Compile**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java")
```
Expected: clean compile.

- [ ] **Step 3: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src/ui/LoanPanel.java
git commit -m "feat: implement LoanPanel CRUD UI with computed amortization"
```

---

### Task 12: ReportWindow — summary report

**Files:**
- Modify (replace stub): `final-project/src/ui/ReportWindow.java`

**Interfaces:**
- Consumes: `dao.BuyerDao`, `dao.PropertyDao`, `dao.LoanDao`, `model.Loan`.
- Produces: a window showing counts, total loan value, total downpayments, and total monthly amortization across all loans (uses polymorphism), in a read-only `JTextArea`.

- [ ] **Step 1: Replace `ui/ReportWindow.java`**

```java
package ui;

import dao.BuyerDao;
import dao.LoanDao;
import dao.PropertyDao;
import model.Loan;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReportWindow extends JFrame {
    public ReportWindow() {
        setTitle("Profriends Inc. — Summary Report");
        setSize(480, 380);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        add(new JScrollPane(area), BorderLayout.CENTER);

        area.setText(buildReport());
    }

    private String buildReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("     PROFRIENDS INC. — SUMMARY REPORT\n");
        sb.append("=========================================\n\n");
        try {
            int buyers = new BuyerDao().getAll().size();
            int props = new PropertyDao().getAll().size();
            List<Loan> loans = new LoanDao().getAll();

            double totalLoan = 0, totalDp = 0, totalMonthly = 0;
            for (Loan l : loans) {
                totalLoan += l.getLoanAmount();
                totalDp += l.getDownpayment();
                totalMonthly += l.computeMonthlyAmortization(); // polymorphic
            }

            sb.append(String.format("Total Buyers      : %d%n", buyers));
            sb.append(String.format("Total Properties  : %d%n", props));
            sb.append(String.format("Total Loans       : %d%n%n", loans.size()));
            sb.append(String.format("Total Loan Value  : %,.2f%n", totalLoan));
            sb.append(String.format("Total Downpayments: %,.2f%n", totalDp));
            sb.append(String.format("Total Monthly Amort: %,.2f%n%n", totalMonthly));

            sb.append("--- Per-Loan Monthly Amortization ---\n");
            for (Loan l : loans) {
                sb.append(String.format("Loan #%d (%s): %,.2f%n",
                        l.getLoanId(), l.getFinanceType(), l.computeMonthlyAmortization()));
            }
        } catch (Exception ex) {
            sb.append("Error generating report: ").append(ex.getMessage());
        }
        return sb.toString();
    }
}
```

- [ ] **Step 2: Compile and run the full app smoke (headless-safe compile)**

Run:
```bash
cd final-project && javac -cp "lib/*" -d bin $(find src -name "*.java")
```
Expected: clean compile. (Launching the GUI requires a display; do that in manual verification.)

- [ ] **Step 3: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project/src/ui/ReportWindow.java
git commit -m "feat: implement summary ReportWindow"
```

---

### Task 13: Cleanup temp tests, export schema.sql, update README, seed sample data

**Files:**
- Delete: `final-project/src/db/DbSmokeTest.java`, `final-project/src/model/LoanMathTest.java`, `final-project/src/dao/DaoCrudTest.java`
- Create: `final-project/schema.sql`, `final-project/src/db/SeedSampleData.java` (optional demo seeding, run once)
- Modify: `final-project/README.md`

**Interfaces:**
- Consumes: all prior.
- Produces: a clean buildable project whose only `main` is `app.Main`; documentation updated for the new run command (`java -cp "bin:lib/*" app.Main`).

- [ ] **Step 1: Remove temporary test classes**

```bash
cd final-project
rm -f src/db/DbSmokeTest.java src/model/LoanMathTest.java src/dao/DaoCrudTest.java
```

- [ ] **Step 2: Write `final-project/schema.sql`** (documentation copy of the schema — mirror §3 of the spec exactly: `users`, `buyers`, `properties`, `loans` with the same columns, plus the `INSERT` for the default admin).

- [ ] **Step 3: Write `final-project/src/db/SeedSampleData.java`** — a `main` that inserts 2–3 buyers, 2–3 properties, and 2 loans (one Bank, one InHouse) using the DAOs, guarded so it only seeds when the tables are empty. Used to populate demo data and screenshots.

- [ ] **Step 4: Update `README.md`** — change run instructions to `app.Main`, document the default admin login (`admin` / `admin123`), the optional `java -cp "bin:lib/*" db.SeedSampleData` step, and the four-table data model.

- [ ] **Step 5: Final clean compile + run scripts updated**

Update `run.sh` final line to `java -cp "bin:$CP" app.Main` and `run.bat` to `java -cp "bin;%CP%" app.Main`. Then:
```bash
cd final-project && rm -rf bin && mkdir -p bin db
javac -cp "lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" db.SeedSampleData
```
Expected: clean compile; seeding prints a confirmation.

- [ ] **Step 6: Commit**

```bash
cd /Users/jadekingabunada/bsit-2-4-java-oop
git add final-project docs
git commit -m "chore: remove temp tests, add schema.sql, seed data, update README and run scripts"
```

---

## Manual Verification Checklist (for the demo / video)

Run `cd final-project && ./run.sh` (or `run.bat`) and confirm:

- [ ] Login with `admin` / `admin123` succeeds; wrong password shows error dialog.
- [ ] Main menu shows all 5 buttons.
- [ ] Buyers: add (validation blocks empty name / non-numeric income), view in table, search (no-result message), edit, delete (admin).
- [ ] Properties: same CRUD cycle works.
- [ ] Loans: add referencing valid buyer/property IDs; invalid IDs rejected; Monthly/Total columns compute; Bank vs InHouse differ.
- [ ] Report window shows counts and totals.
- [ ] Logout returns to login.
- [ ] (Optional) create a `staff` user row directly and confirm Delete buttons are disabled.
