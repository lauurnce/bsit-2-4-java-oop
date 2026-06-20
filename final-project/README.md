# Final Project — Java Swing + SQLite

Self-contained final project for our Java OOP class. It uses **Java Swing** for
the GUI and **SQLite** for storage. Everything needed to run is inside this
folder — clone the repo and go.

## How to run

You only need a **JDK installed** (`javac` and `java` on your PATH). No internet
required after cloning — the SQLite driver is already bundled in `lib/`.

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
(or just double-click `run.bat`)

The first run creates the database automatically at `db/app.db`.

## Folder layout
```
final-project/
├── src/          Java source files (the actual project code)
├── lib/          SQLite JDBC driver (committed, works on Win/Mac/Linux)
├── db/           the SQLite database file is created here (not committed)
├── bin/          compiled .class files (auto-created, not committed)
├── run.sh        compile + run on macOS/Linux
├── run.bat       compile + run on Windows
└── README.md
```

## Notes for the group
- The database path in `Database.java` is **relative** (`db/app.db`), so it works
  on everyone's machine without editing.
- `db/app.db` is git-ignored on purpose — each person's local data stays local,
  and the table is recreated automatically on first run.
- Add new screens/classes as more `.java` files in `src/`. The run scripts compile
  everything in `src/` automatically.
- If you use Eclipse instead of the scripts: import this folder as a project, add
  the jar in `lib/` to the build path, and set the run directory to `final-project`.
