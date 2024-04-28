-- This is a migration file for creation of tables

CREATE TABLE IF NOT EXISTS merchants
(
    id uuid primary key DEFAULT gen_random_uuid(),
    username          VARCHAR(64) NOT NULL UNIQUE,
    password          VARCHAR(64) NOT NULL,
    country           VARCHAR(64) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS accounts
(
    id uuid primary key DEFAULT gen_random_uuid(),
    currency_code VARCHAR(8) NOT NULL,
    merchant_id UUID NOT NULL,
    balance       DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS currencies
(
    currency_code VARCHAR(8) primary key not null
);

CREATE table if not exists cards
(
    id uuid primary key DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    card_number     VARCHAR(32) UNIQUE NOT NULL,
    cvv             VARCHAR(8)         NOT NULL,
    expiration_date VARCHAR(16)        NOT NULL
);

CREATE TABLE IF NOT EXISTS customers
(
    id uuid primary key DEFAULT gen_random_uuid(),
    first_name VARCHAR(64)        NOT NULL,
    last_name  VARCHAR(64)        NOT NULL,
    username   VARCHAR(64) unique NOT NULL,
    country    VARCHAR(64)        NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions
(
    id uuid primary key DEFAULT gen_random_uuid(),
    transaction_type   VARCHAR(16)      NOT NULL,
    account_id uuid NOT NULL,
    created_at         timestamp default current_timestamp,
    updated_at         timestamp default current_timestamp,
    card_id uuid not null,
    language           varchar(64),
    amount             double precision not null,
    notification_url   varchar(256),
    transaction_status varchar(16)      not null
);

CREATE TABLE IF NOT EXISTS callbacks
(
    id uuid primary key DEFAULT gen_random_uuid(),
    transaction_id UUID NOT NULL,
    iteration int not null
);


