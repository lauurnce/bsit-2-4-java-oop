# Profriends Inc. — Property Loan Management System

Final project for our **Object-Oriented Programming** subject. A menu-driven
**Java Swing** desktop application backed by **SQLite**, for managing housing/property
loans: buyers, properties (units), and loan applications, with login and reports.

Everything needed to run is inside this folder — clone the repo and go (only a JDK
required; the SQLite driver is bundled in `lib/`).

## How to run

### macOS / Linux
```bash
cd final-project
./run.sh
```

### Windows
```bat
cd final-project
run.bat
```
(or double-click `run.bat`)

The database is created automatically at `db/profriends.db` on first run.

### Default login
| Username | Password | Role  |
|----------|----------|-------|
| `admin`  | `admin123` | admin |

The `admin` account is seeded automatically. Admins can delete records; `staff`
accounts cannot (Delete buttons are disabled for them).

### (Optional) Load sample data for the demo
After compiling once (run the app or the script), populate demo buyers/properties/loans:
```bash
java -cp "bin:lib/*" db.SeedSampleData     # Windows: "bin;lib/*"
```

## Features
- **Login** with database-backed validation and error messages
- **Main menu** to manage Buyers, Properties, Loans, and view Reports
- **Full CRUD** (Add / View / Search / Update / Delete) for each entity
- **Search** with "no record found" messaging
- **Report** with totals and per-loan monthly amortization
- **Input validation** and **exception handling** throughout (dialogs, not crashes)

## Data model (4 tables)
```
users       login accounts (admin/staff)
buyers      loan applicants
properties  housing units
loans       buyer + property + financing terms  (FKs -> buyers, properties)
```
Relationships: one buyer → many loans; one property → many loans.
See `schema.sql` for the full schema and the ERD in the documentation.

## OOP concepts demonstrated
| Concept | Where |
|---|---|
| Classes & Objects | `model` package |
| Encapsulation | private fields + getters/setters in all models |
| Constructors | default + parameterized on every model |
| Inheritance | `Buyer extends Person`; `BankFinancedLoan`/`InHouseLoan extends Loan` |
| Polymorphism | overridden `computeMonthlyAmortization()`; overloaded DAO `search()` |
| Abstraction | abstract `Loan`, abstract `Person`, `Crud` interface |
| Methods | DAO CRUD methods, model compute methods, UI handlers |
| Exception Handling | `try-catch` on all DB and parse operations |
| Collections | `ArrayList`/`List` returned by DAOs into JTables |

## Project layout
```
final-project/
├── src/
│   ├── app/    Main entry point + Session
│   ├── model/  OOP classes (Person, Buyer, Property, User, Loan + subclasses)
│   ├── dao/    Database access (Crud interface + per-table DAOs)
│   ├── db/     Database (connection + schema/seed) + SeedSampleData
│   └── ui/     Swing windows (login, menu, panels, report)
├── lib/        bundled SQLite JDBC driver
├── db/         SQLite database created here at runtime (git-ignored)
├── bin/        compiled .class files (git-ignored)
├── schema.sql  schema reference for docs/ERD
├── run.sh / run.bat
└── README.md
```

## Notes for the group
- The DB path is **relative** (`db/profriends.db`), so it works on every machine.
- `db/profriends.db` is git-ignored — each person's local data stays local and the
  schema is recreated on first run.
- Passwords are stored in plain text for this academic scope (documented limitation).
- To use Eclipse instead of the scripts: import this folder, add `lib/*.jar` to the
  build path, set the run config main class to `app.Main`, and set the working
  directory to `final-project`.
