-- dml.sql (before make sure that you run ddl.sql)

-- Users
INSERT INTO users (id, username, password, email, role, balance)
VALUES (1, 'Andrii', 'password', 'Andrii@gmail.com', 'ADMIN', 1000.00);

INSERT INTO users (id, username, password, email, role, balance)
VALUES (2, 'Nazar', 'password', 'Ihor@gmail.com', 'MANAGER', 1000.00);

INSERT INTO users (id, username, password, email, role, balance)
VALUES (3, 'Olha', 'password', 'Olha@gmail.com', 'USER', 0.00);


-- Tours
INSERT INTO tours (id, title, description, price, tour_type, transfer_type, hotel_type,
                   tour_status, arrival_date, eviction_date, user_id, is_hot)
VALUES (1,
        'Bulgaria',
        '7-денний відпочинок на узбережжі Болгарії: пляжі, екскурсії, харчування "All inclusive".',
        899.00,
        'HEALTH',
        'BUS',
        'FOUR_STARS',
        'REGISTERED',
        '2025-08-10',
        '2025-08-17',
        1,
        TRUE);

INSERT INTO tours (id, title, description, price, tour_type, transfer_type, hotel_type,
                   tour_status, arrival_date, eviction_date, user_id, is_hot)
VALUES (2,
        'Alpine Ski Weekend',
        '3 дні на схилах з інструктором, трансфер і сніданки включено.',
        330,
        'ECO',
        'MINIBUS',
        'TWO_STARS',
        'REGISTERED',
        '2025-12-15',
        '2025-12-18',
        NULL,
        FALSE);

INSERT INTO tours (id, title, description, price, tour_type, transfer_type, hotel_type,
                   tour_status, arrival_date, eviction_date, user_id, is_hot)
VALUES (3,
        'Cultural Prague Trip',
        '4 дні в Празі: оглядова екскурсія, музеї, прогулянки.',
        299.00,
        'SPORTS',
        'PRIVATE_CAR',
        'FOUR_STARS',
        'PAID',
        '2025-05-20',
        '2025-05-24',
        3,
        FALSE);
