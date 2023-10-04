INSERT INTO employee_authorities_mapping (authority_id, user_id)
SELECT a.id, u.id
FROM employee_authorities AS a
         JOIN users AS u
              ON u.email LIKE 'admin.greencity@starmaker.email';
INSERT INTO employee_positions_mapping (position_id, user_id)
SELECT p.id, u.id
FROM positions AS p
         JOIN users AS u
              ON u.email LIKE 'admin.greencity@starmaker.email';
