DECLARE
@createNewCourierId INT
            SET @createNewCourierId = (SELECT id FROM employee_authorities WHERE name = 'CREATE_NEW_COURIER');
            DECLARE
@deleteDeactivateCourierId INT
            SET @deleteDeactivateCourierId = (SELECT id FROM employee_authorities WHERE name = 'DELETE_DEACTIVATE_COURIER');

INSERT INTO employee_authorities_mapping (authorities_id, user_id)
SELECT @deleteDeactivateCourierId, user_id
FROM employee_authorities
WHERE authorities_id = @createNewCourierId;

DECLARE
@createNewStation INT
            SET @createNewStation = (SELECT id FROM employee_authorities WHERE name = 'CREATE_NEW_STATION');
            DECLARE
@deleteDeactivateStation INT
            SET @deleteDeactivateStation = (SELECT id FROM employee_authorities WHERE name = 'DELETE_DEACTIVATE_STATION');

INSERT INTO employee_authorities_mapping (authorities_id, user_id)
SELECT @deleteDeactivateStation, user_id
FROM employee_authorities
WHERE authorities_id = @createNewStation;

DECLARE
@editDeleteDeactivatePricingCard INT
            SET @editDeleteDeactivatePricingCard = (SELECT id FROM employee_authorities WHERE name = 'EDIT_DELETE_DEACTIVATE_PRICING_CARD');
            DECLARE
@createPricingCard INT
            SET @createPricingCard = (SELECT id FROM employee_authorities WHERE name = 'CREATE_PRICING_CARD');

INSERT INTO employee_authorities_mapping (authorities_id, user_id)
SELECT @createPricingCard, user_id
FROM employee_authorities
WHERE authorities_id = @editDeleteDeactivatePricingCard;
