DELETE FROM employee_authorities_mapping
WHERE user_id IN (
    SELECT empl.user_id
    FROM employee_authorities_mapping AS empl
             INNER JOIN employee_positions_mapping AS epm ON empl.user_id = epm.user_id
    WHERE epm.position_id IN (
        SELECT id
        FROM positions p
        WHERE p.name_eng = 'Admin'
    )
      AND empl.authority_id IN (
        SELECT id
        FROM employee_authorities
        WHERE name = 'CREATE_NEW_LOCATION'
           OR name = 'CREATE_NEW_COURIER'
           OR name = 'CREATE_NEW_STATION'
           OR name = 'CREATE_PRICING_CARD'
    )
);
DELETE FROM employee_authorities_mapping
WHERE user_id IN (
    SELECT empl.user_id
    FROM employee_authorities_mapping AS empl
             INNER JOIN employee_positions_mapping AS epm ON empl.user_id = epm.user_id
    WHERE epm.position_id IN (
        SELECT id
        FROM positions p
        WHERE p.name_eng = 'Service Manager'
    )
      AND empl.authority_id IN (
        SELECT id
        FROM employee_authorities
        WHERE name = 'CREATE_PRICING_CARD'
    )
);
