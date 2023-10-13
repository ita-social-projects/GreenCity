INSERT INTO employee_authorities_mapping (authority_id, user_id)
Select ea.id,epm.user_id
from employee_positions_mapping epm
         Join  employee_authorities ea on ea.name IN ('CREATE_NEW_LOCATION',
                                                      'CREATE_NEW_COURIER',
                                                      'CREATE_NEW_STATION',
                                                      'CREATE_PRICING_CARD')
where epm.position_id in(select p.id from positions p where p.name_eng='Admin');

INSERT INTO employee_authorities_mapping (authority_id, user_id)
Select ea.id,epm.user_id
from employee_positions_mapping epm
         Join  employee_authorities ea on ea.name IN ('CREATE_PRICING_CARD')
where epm.position_id
          in(select p.id
             from positions p where p.name_eng='Service Manager');
