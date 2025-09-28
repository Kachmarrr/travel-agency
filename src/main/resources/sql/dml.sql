-- dml.sql (після ddl.sql з BIGSERIAL)

-- Users
INSERT INTO users (username, password, email, role, balance)
VALUES
    ('Andrii', 'password', 'Andrii@gmail.com', 'ADMIN', 1000.00),
    ('Nazar', 'password', 'Ihor@gmail.com', 'MANAGER', 1000.00),
    ('Olha', 'password', 'Olha@gmail.com', 'USER', 0.00),
    ('Maria', 'password', 'Maria@gmail.com', 'USER', 500.00),
    ('Ivan', 'password', 'Ivan@gmail.com', 'MANAGER', 750.00);

-- Tours
INSERT INTO tours (title, description, price, tour_type, transfer_type, hotel_type,
                   tour_status, arrival_date, eviction_date, user_id, is_hot)
VALUES
    ('Bulgaria',
     '7-денний відпочинок на узбережжі Болгарії: пляжі, екскурсії, харчування "All inclusive".',
     8999.00, 'HEALTH', 'BUS', 'FOUR_STARS', 'AVAILABLE', '2025-08-10', '2025-08-17', null, TRUE),

    ('Alpine Ski Weekend',
     '3 дні на схилах з інструктором, трансфер і сніданки включено.',
     3300, 'ECO', 'MINIBUS', 'TWO_STARS', 'AVAILABLE', '2025-12-15', '2025-12-18', null, FALSE),

    ('Cultural Prague Trip',
     '4 дні в Празі: оглядова екскурсія, музеї, прогулянки.',
     2999.00, 'SPORTS', 'PRIVATE_CAR', 'FOUR_STARS', 'PAID', '2025-05-20', '2025-05-24', 3, FALSE),

    ('Greek Island Escape',
     '5 днів на грецьких островах з готелем 4*, трансфер і сніданки включено.',
     7800.00, 'HEALTH', 'BUS', 'FOUR_STARS', 'AVAILABLE', '2025-09-01', '2025-09-06', null, TRUE),

    ('Romantic Paris',
     '3 дні для двох: екскурсії, круїз по Сені, готель 3* з сніданком.',
     4500.00, 'SPORTS', 'TRAIN', 'THREE_STARS', 'PAID', '2025-07-10', '2025-07-13', 2, FALSE),

    ('Solo Island', '7 днів на острові в іспанії, на 3 осіб включено хачування та переліт з Варшави',
     7500, 'CULTURAL', 'BUS', 'FIVE_STARS', 'AVAILABLE', '2025-07-13', ' 2025-07-20', null, FALSE),


    ('Spain Culture',
     '7-денний відпочинок на узбережжі Spain: пляжі, екскурсії, харчування "All inclusive".',
     8999.00, 'WINE', 'MINIBUS', 'TWO_STARS', 'AVAILABLE', '2025-08-10', '2025-08-17', null, TRUE),

    ('Alaska',
     '3 дні на схилах з інструктором, трансфер і сніданки включено.',
     3300, 'ECO', 'JEEPS', 'ONE_STAR', 'AVAILABLE', '2025-12-15', '2025-12-18', null, FALSE),

    (' Prague top trip ',
     '4 дні в Празі: оглядова екскурсія, музеї, прогулянки.',
     2999.00, 'ADVENTURE', 'PRIVATE_CAR', 'THREE_STARS', 'AVAILABLE', '2025-05-20', '2025-05-24', 3, FALSE),

    ('Greek Mega tour',
     '5 днів на грецьких островах з готелем 4*, трансфер і сніданки включено.',
     7800.00, 'HEALTH', 'BUS', 'FOUR_STARS', 'AVAILABLE', '2025-09-01', '2025-09-06', null, TRUE),

    ('Romantic Bulgaria',
     '3 дні для двох: екскурсії, круїз по Nesebar, готель 3* з сніданком.',
     4500.00, 'SPORTS', 'TRAIN', 'THREE_STARS', 'PAID', '2025-07-10', '2025-07-13', 2, FALSE),

    ('Solo trip to China', '7 днів на острові в china, на 3 осіб включено хачування та переліт з Варшави',
     7500, 'CULTURAL', 'BUS', 'FIVE_STARS', 'AVAILABLE', '2025-07-13', ' 2025-07-20', null, FALSE);
