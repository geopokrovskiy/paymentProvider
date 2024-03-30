-- This is a migration file for population of the tables
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO customers(username) VALUES ('customer1');
INSERT INTO customers(username) VALUES ('customer2');
INSERT INTO customers(username) VALUES ('customer3');

---

INSERT INTO currencies(currency_code) VALUES ('RUB');
INSERT INTO currencies(currency_code) VALUES ('BRL');
INSERT INTO currencies(currency_code) VALUES ('EUR');
INSERT INTO currencies(currency_code) VALUES ('USD');
INSERT INTO currencies(currency_code) VALUES ('CHF');
INSERT INTO currencies(currency_code) VALUES ('CNY');
INSERT INTO currencies(currency_code) VALUES ('TRY');
INSERT INTO currencies(currency_code) VALUES ('JPY');

---

