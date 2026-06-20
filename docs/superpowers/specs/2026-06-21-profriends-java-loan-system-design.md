# Profriends Inc. — Property Loan Management System (Java + Swing + SQLite)

**Date:** 2026-06-21
**Subject:** Object-Oriented Programming — Final Project
**Concept origin:** Reuse of the IM_G10 "Profriends Inc. Housing Loan" system idea
(originally Python/Flask/MySQL), rebuilt from scratch in pure Java to satisfy the
OOP final-project rubric. No original code is reused — only the domain concept.

---

## 1. Overview

A menu-driven, GUI-based housing/property loan management system written in Java.
Staff log in, then manage **buyers**, **properties (housing units)**, and **loans**,
and generate simple reports. Built with **Java Swing** for the UI and **SQLite** for
storage (bundled JDBC driver — runs on any groupmate's machine with only a JDK).

Lives in the existing `final-project/` folder, reusing its `lib/` (sqlite jar),
`db/`, and `run.sh` / `run.bat` infrastructure. The throwaway demo
(`Main.java`, `Database.java`) is removed and replaced.

---

## 2. Architecture

Layered: `UI → DAO → Database`. UI never writes SQL; DAOs never touch Swing.

```
final-project/src/
├── app/Main.java                 entry point; opens LoginWindow
├── model/                        OOP data classes
│   ├── Person.java               abstract parent (encapsulated fields)
│   ├── Buyer.java                extends Person
│   ├── Property.java
│   ├── Loan.java                 abstract; abstract computeMonthlyAmortization()
│   ├── BankFinancedLoan.java     extends Loan
│   ├── InHouseLoan.java          extends Loan
│   └── User.java
├── dao/
│   ├── Crud.java                 interface: add/getAll/search/update/delete
│   ├── UserDao.java
│   ├── BuyerDao.java
│   ├── PropertyDao.java
│   └── LoanDao.java
├── db/Database.java              SQLite connection + schema init + seed admin
└── ui/
    ├── LoginWindow.java
    ├── MainMenu.java
    ├── BuyerPanel.java           JTable + Add/Edit/Delete/Search
    ├── PropertyPanel.java
    ├── LoanPanel.java
    └── ReportWindow.java
```

Total: 18 classes across 5 packages (exceeds the 5-class minimum).

---

## 3. Database schema (SQLite — 4 tables)

```sql
users
  user_id      INTEGER PRIMARY KEY AUTOINCREMENT
  username     TEXT NOT NULL UNIQUE
  password     TEXT NOT NULL
  role         TEXT NOT NULL DEFAULT 'staff'        -- 'admin' | 'staff'
  -- seeded default: admin / admin123 (role 'admin')

buyers
  buyer_id        INTEGER PRIMARY KEY AUTOINCREMENT
  full_name       TEXT NOT NULL
  birthdate       TEXT
  gov_id          TEXT
  gender          TEXT
  civil_status    TEXT
  address         TEXT
  monthly_income  REAL NOT NULL
  mobile_num      TEXT
  email           TEXT

properties
  property_id   INTEGER PRIMARY KEY AUTOINCREMENT
  unit_name     TEXT NOT NULL
  location      TEXT
  unit_type     TEXT                                -- House & Lot, Townhouse, Condo
  sell_price    REAL NOT NULL
  status        TEXT NOT NULL DEFAULT 'Available'   -- Available | Sold

loans
  loan_id         INTEGER PRIMARY KEY AUTOINCREMENT
  buyer_id        INTEGER NOT NULL REFERENCES buyers(buyer_id)
  property_id     INTEGER NOT NULL REFERENCES properties(property_id)
  finance_type    TEXT NOT NULL                     -- 'Bank' | 'InHouse'
  loan_amount     REAL NOT NULL
  downpayment     REAL NOT NULL
  loan_term_years INTEGER NOT NULL
  annual_rate     REAL NOT NULL
  date_booked     TEXT
```

**Relationships (ERD):** one buyer → many loans; one property → many loans.
FKs live on `loans`. PKs are the `*_id` columns. `PRAGMA foreign_keys = ON` is set
on every connection.

---

## 4. OOP concept → code mapping

| OOP Concept | Where it lives |
|---|---|
| Classes & Objects | `Buyer`, `Property`, `Loan`, `User` instantiated throughout |
| Encapsulation | All model fields `private`, accessed via getters/setters |
| Constructors | Default + parameterized constructors on every model |
| Inheritance | `Buyer extends Person`; `BankFinancedLoan`/`InHouseLoan extends Loan` |
| Polymorphism | `Loan.computeMonthlyAmortization()` overridden per subclass (override); overloaded `search(...)` in DAOs (overload) |
| Abstraction | `abstract Loan`, `abstract Person`, `Crud` interface |
| Methods | CRUD methods in DAOs, compute methods in models, UI handlers |
| Exception Handling | `try-catch` around all DB calls + input parsing; user-friendly `JOptionPane` errors |
| Collections | `ArrayList<Buyer>`, `ArrayList<Loan>`, etc. returned by DAOs into JTables |

---

## 5. Loan amortization (polymorphism detail)

Principal financed = `loan_amount − downpayment`.
Monthly amortization uses the standard formula with `r = annual_rate/12/100`,
`n = loan_term_years * 12`:

```
A = P * r / (1 - (1+r)^(-n))      (when r > 0; else P / n)
```

- `BankFinancedLoan`: default annual rate ~6.5%, no extra processing surcharge in the monthly figure.
- `InHouseLoan`: default annual rate ~9.5%, adds a small processing-fee rule reflected in `computeTotalPayable()`.

The user enters the actual `annual_rate`; subclasses differ in their default rate
and in `computeTotalPayable()`. `LoanDao` instantiates the correct subclass based on
`finance_type` — this is where polymorphism is demonstrated end to end (UI shows
different monthly/total figures without `if/else` on type).

---

## 6. Required features → implementation

| Feature | Implementation |
|---|---|
| Login | `LoginWindow` validates against `users` table; error dialog on failure; seeded admin |
| Main menu | `MainMenu` JFrame with buttons to Buyers / Properties / Loans / Reports / Logout |
| Add | Form dialog per entity → DAO `add()`; required-field validation before save |
| View | `JTable` populated from DAO `getAll()` |
| Search | Search box → DAO `search(keyword)`; "no record found" message if empty |
| Update | Select row → edit form → DAO `update()`; confirms record exists |
| Delete | Admin-only; `JOptionPane` confirm dialog before DAO `delete()` |
| Report | `ReportWindow`: totals (count of buyers/properties/loans, total loan value, monthly amortization summary per loan via polymorphism) |
| Input validation | Non-empty required fields; numeric parsing for income/price/amounts/terms |
| Exception handling | `try-catch` on all DB + parse operations; messages via `JOptionPane` |

**Authorization:** `role = 'admin'` may delete; `staff` cannot (delete buttons
disabled / blocked for staff).

---

## 7. Error handling strategy

- `Database` throws a wrapped `RuntimeException` on init failure (fatal, shown once).
- DAO methods catch `SQLException`, log, and surface a clean message to the UI layer
  via return values / thrown app exceptions — never a raw stack trace to the user.
- All numeric input parsed inside `try-catch (NumberFormatException)` with a dialog.

---

## 8. Testing strategy

Swing UI is hard to unit-test headlessly, so testing focuses on the layers that hold
the logic:

- **Database/DAO smoke test** (headless `main` or simple test class): init schema,
  insert → getAll → search → update → delete for each table; verify FK behavior.
- **Loan math test:** construct `BankFinancedLoan` and `InHouseLoan` with known
  inputs; assert `computeMonthlyAmortization()` and `computeTotalPayable()` match
  hand-computed values (verifies polymorphism + formula).
- **Manual demo checklist:** login, each CRUD screen, report — matching the video
  demonstration requirements.

Tests run via the same `run`-style classpath (`bin:lib/*`), no extra tooling.

---

## 9. Deliverables alignment

- Complete Java project folder → `final-project/`
- Database/SQL script → schema created in `Database.java`; an exported `schema.sql`
  committed for documentation
- ERD + UML → drawn from §3 / §2 (documentation task, not code)
- Runnable on any machine → `run.sh` / `run.bat`, bundled sqlite jar, relative DB path

---

## 10. Out of scope (YAGNI)

- spouse / employment / household / beneficiary tables (dropped to stay defendable)
- password hashing (plain text is acceptable for this academic scope; note in docs)
- multi-user concurrency, networking, reporting to PDF
