INSERT INTO users (id, date_of_registration, email, email_notification, name, role, user_status, refresh_token_key,
                   profile_picture, rating, last_activity_time, first_name, city, user_credo, show_location,
                   show_eco_place, show_shopping_list, language_id, uuid, phone_number, event_organizer_rating)
VALUES (2, '2022-10-25 16:40:06.209394', 'muzgcity@gmail.com', 0, 'Maciej', 'ROLE_ADMIN', 2,
        '10806555-379d-4b25-b051-c3427d1fbae9', null, 0, '2022-10-25 14:40:06.209394 +00:00', 'Maciej', null, null,
        true, true, true, 2, '3de3fa57-0cfc-4a0a-9b58-cfe00d3ca2e2', null, null),
       (3, '2022-10-26 15:13:57.146122', 'mymail@gmail.com', 0, 'Maciej', 'ROLE_ADMIN', 2,
        '1170c8d9-59dd-45e3-8b72-1807a767e77c', null, 0, '2022-10-26 13:13:57.146122 +00:00', 'Maciej', null, null,
        true, true, true, 2, '7c90a1c3-d424-4079-ba51-481994ca9719', null, null),
       (4, '2022-11-05 20:23:59.615581', 'johny@gmail.com', 0, 'JanKowalski', 'ROLE_USER', 2,
        'd419fe35-8d4c-40af-bc06-cd6edfd92a02', null, 0, '2022-11-05 19:23:59.616581 +00:00', 'JanKowalski', null, null,
        true, true, true, 2, '4b05c5c6-80d7-45e5-bfdf-e2bf678c060e', null, null),
       (5, '2022-11-05 22:30:12.144632', 'zet123@test.com', 0, 'Zoldyck123', 'ROLE_USER', 2,
        'c8e13ed1-42c8-4a43-854e-5d15c16ea18c', null, 0, '2022-11-05 21:30:12.144632 +00:00', 'Zoldyck123', null, null,
        true, true, true, 2, '93b55cb4-5939-42c0-bf31-067b5dd19bf5', null, null);

INSERT INTO own_security (id, password, user_id) VALUES
                                                     (1, '{noop}GreenCity123!', 2),
                                                     (2, '{noop}GreenCity124!', 3),
                                                     (3, '{noop}GreenCity125!', 4),
                                                     (4, '{noop}GreenCity126!', 5);