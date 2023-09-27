DELETE FROM positions_authorities_mapping AS pam
    USING positions AS p
WHERE pam.position_id = p.id
  AND p.name_eng = 'Admin'
  AND pam.authorities_id IN (
    SELECT id
    FROM employee_authorities
    WHERE name = 'CREATE_NEW_LOCATION'
   OR name = 'CREATE_NEW_COURIER'
   OR name = 'CREATE_NEW_STATION'
   OR name = 'CREATE_PRICING_CARD'
    );

DELETE FROM positions_authorities_mapping AS pam
    USING positions AS p
WHERE pam.position_id = p.id
  AND p.name_eng = 'Service Manager'
  AND pam.authorities_id IN (
    SELECT id
    FROM employee_authorities
    WHERE name = 'CREATE_PRICING_CARD'
    );
