INSERT INTO positions_authorities_mapping (authorities_id, position_id)
SELECT
    emp.id AS emp_id,
    p.id AS position_id
FROM employee_authorities AS emp
         JOIN positions AS p ON  p.name_eng = 'Admin'
where emp.name IN ('CREATE_NEW_LOCATION',
                   'CREATE_NEW_COURIER',
                   'CREATE_NEW_STATION',
                   'CREATE_PRICING_CARD');

INSERT INTO positions_authorities_mapping (authorities_id, position_id)
SELECT
    emp.id AS emp_id,
    p.id AS position_id
FROM employee_authorities AS emp
         JOIN positions AS p ON  p.name_eng = 'Service Manager'
where emp.name IN ('CREATE_PRICING_CARD');

