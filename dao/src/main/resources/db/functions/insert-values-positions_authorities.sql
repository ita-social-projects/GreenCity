INSERT INTO positions_authorities_mapping (authorities_id, position_id)
SELECT ea.id, pam.position_id
FROM positions_authorities_mapping AS pam
         JOIN employee_authorities AS ea
              ON ea.name LIKE 'DELETE_DEACTIVATE_COURIER'
WHERE pam.authorities_id IN (
    SELECT id
    FROM employee_authorities
    WHERE name LIKE 'CREATE_NEW_COURIER'
);
INSERT INTO positions_authorities_mapping (authorities_id, position_id)
SELECT ea.id, pam.position_id
FROM positions_authorities_mapping AS pam
         JOIN employee_authorities AS ea
              ON ea.name LIKE 'DELETE_DEACTIVATE_STATION'
WHERE pam.authorities_id IN (
    SELECT id
    FROM employee_authorities
    WHERE name LIKE 'CREATE_NEW_STATION'
);