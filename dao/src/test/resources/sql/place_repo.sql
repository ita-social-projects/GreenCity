INSERT INTO locations(id, address, lat, lng)
VALUES (1, 'Brooklyn', 49.84988, 24.022533);

INSERT INTO places (id, name, status, modified_date, location_id)
VALUES (1, 'InterestingPlace', 0, '2020-09-24T00:00', 1),
       (2, 'InterestingPlace2', 0, '2020-09-25T00:00', 1),
       (3, 'InterestingPlace3', 0, '2020-09-26T00:00', 1),
       (4, 'InterestingPlace4', 0, '2020-09-27T00:00', 1),
       (5, 'InterestingPlace5', 0, '2020-09-28T00:00', 1),
       (6, 'InterestingPlace6', 1, '2020-09-30T00:00', 1);

INSERT INTO estimates(id, rate, place_id)
VALUES (1, 6, 1),
       (2, 10, 1);