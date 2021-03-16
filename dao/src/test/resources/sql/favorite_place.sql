INSERT INTO locations(id, address, lat, lng)
VALUES (1, 'Brooklyn', 49.84988, 24.022533);

INSERT INTO places (id, name, status, modified_date, location_id)
VALUES (1, 'InterestingPlace', 0, '2020-09-24T00:00', 1),
       (2, 'InterestingPlace2', 0, '2020-09-25T00:00', 1);

insert into languages(id, code)
values(1,'ua');

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_visit,
                   role,
                   user_status,
                   refresh_token_key,
                   rating,
                   city,
                   language_id)
VALUES (1, '2020-09-30T00:00', 'test@email.com', 0, 'SuperTest', '2020-09-30T00:00', 0, 2, 'secret', 10, 'New York',1);

INSERT INTO favorite_places(id, name, place_id, user_id) VALUES (1, 'Favorite place', 1, 1),
                                                                (2, 'Favorite place2', 2, 1);
