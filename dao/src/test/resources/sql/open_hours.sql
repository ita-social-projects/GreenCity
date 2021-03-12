INSERT INTO places(id, name) VALUES (1, 'test');

INSERT INTO opening_hours(id, close_time, open_time, week_day, place_id)
VALUES (1, '20:00:00', '06:00:00', 0, 1),
       (2, '20:00:00', '06:00:00', 1, 1);