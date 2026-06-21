-- Profriends Inc. — Property Loan Management System
-- SQLite schema (for documentation / ERD reference).
-- The application (db/Database.java) creates these tables automatically on
-- first run; this file mirrors that schema exactly.

CREATE TABLE IF NOT EXISTS users (
    user_id  INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role     TEXT NOT NULL DEFAULT 'staff'   -- 'admin' | 'staff'
);

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
);

CREATE TABLE IF NOT EXISTS properties (
    property_id INTEGER PRIMARY KEY AUTOINCREMENT,
    unit_name   TEXT NOT NULL,
    location    TEXT,
    unit_type   TEXT,                          -- House & Lot | Townhouse | Condo
    sell_price  REAL NOT NULL,
    status      TEXT NOT NULL DEFAULT 'Available'  -- Available | Sold
);

CREATE TABLE IF NOT EXISTS loans (
    loan_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    buyer_id        INTEGER NOT NULL,
    property_id     INTEGER NOT NULL,
    finance_type    TEXT NOT NULL,             -- Bank | InHouse
    loan_amount     REAL NOT NULL,
    downpayment     REAL NOT NULL,
    loan_term_years INTEGER NOT NULL,
    annual_rate     REAL NOT NULL,
    date_booked     TEXT,
    FOREIGN KEY (buyer_id)    REFERENCES buyers(buyer_id),
    FOREIGN KEY (property_id) REFERENCES properties(property_id)
);

-- Default admin account (the app seeds this automatically).
INSERT INTO users (username, password, role)
SELECT 'admin', 'admin123', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');
