-- Insert for 'DELETE_DEACTIVATE_COURIER' authority
INSERT INTO employee_authorities_mapping (authority_id, user_id)
SELECT ea.id, pam.user_id
FROM employee_authorities_mapping AS pam
         JOIN employee_authorities AS ea
              ON ea.name LIKE 'DELETE_DEACTIVATE_COURIER'
WHERE pam.authority_id IN (
    SELECT id
    FROM employee_authorities
    WHERE name LIKE 'CREATE_NEW_COURIER'
);


-- Insert for 'DELETE_DEACTIVATE_STATION' authority
INSERT INTO employee_authorities_mapping (authority_id, user_id)
SELECT ea.id, pam.user_id
FROM employee_authorities_mapping AS pam
         JOIN employee_authorities AS ea
              ON ea.name LIKE 'DELETE_DEACTIVATE_STATION'
WHERE pam.authority_id IN (
    SELECT id
    FROM employee_authorities
    WHERE name LIKE 'CREATE_NEW_STATION'
);

