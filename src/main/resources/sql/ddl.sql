-- CREATE DATABASE travel_agency

CREATE TABLE users
(
    id       BIGINT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    role     VARCHAR(50)  NOT NULL DEFAULT 'USER',
    balance  DECIMAL(10, 2)        DEFAULT 0
);

CREATE TABLE tours
(
    id            BIGINT PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    price         DECIMAL(10, 2),
    tour_type     VARCHAR(50)  NOT NULL,
    transfer_type VARCHAR(50)  NOT NULL,
    hotel_type    VARCHAR(50)  NOT NULL,
    tour_status   VARCHAR(50)  NOT NULL,
    arrival_date  DATE         NOT NULL,
    eviction_date DATE         NOT NULL,
    user_id       BIGINT,
    is_hot        BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

