-- Insert for 'CREATE_NEW_COURIER' authority
INSERT INTO employee_authorities_mapping (authority_id, user_id)
SELECT ea.id, eam.user_id
FROM employee_authorities AS ea
         JOIN employee_authorities_mapping AS eam ON eam.authority_id IN (
    SELECT ea2.id
    FROM employee_authorities AS ea2
    WHERE ea2.name = 'CREATE_NEW_COURIER'
)
WHERE ea.id IN (
    SELECT ea3.id
    FROM employee_authorities AS ea3
    WHERE ea3.name = 'DELETE_DEACTIVATE_COURIER'
);

-- Insert for 'DELETE_DEACTIVATE_STATION' authority
INSERT INTO employee_authorities_mapping (authority_id, user_id)
SELECT ea.id, eam.user_id
FROM employee_authorities AS ea
         JOIN employee_authorities_mapping AS eam ON eam.authority_id IN (
    SELECT ea2.id
    FROM employee_authorities AS ea2
    WHERE ea2.name = 'CREATE_NEW_STATION'
)
WHERE ea.id IN (
    SELECT ea3.id
    FROM employee_authorities AS ea3
    WHERE ea3.name = 'DELETE_DEACTIVATE_STATION'
);
