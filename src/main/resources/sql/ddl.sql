-- users
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(100)   NOT NULL UNIQUE,
    password VARCHAR(255)   NOT NULL,
    email    VARCHAR(255)   NOT NULL UNIQUE,
    role     VARCHAR(50)    NOT NULL DEFAULT 'USER',
    balance  NUMERIC(12, 2) NOT NULL DEFAULT 0.00
);

-- tours
CREATE TABLE tours
(
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(100)   NOT NULL,
    description   TEXT,
    price         NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    tour_type     VARCHAR(50),
    transfer_type VARCHAR(50),
    hotel_type    VARCHAR(50),
    tour_status   VARCHAR(50),
    arrival_date  DATE,
    eviction_date DATE,
    user_id       BIGINT REFERENCES users (id) ON DELETE CASCADE,
    is_hot        BOOLEAN        NOT NULL DEFAULT FALSE
);

