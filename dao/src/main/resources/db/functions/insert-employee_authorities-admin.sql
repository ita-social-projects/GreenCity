INSERT INTO employee_authorities_mapping (authority_id, user_id)
Select ea.id,epm.user_id
from employee_positions_mapping epm
         Join  employee_authorities ea on ea.name IN (Select employee_authorities.name from employee_authorities)
where epm.position_id
          in(select p.id
             from positions p where p.name_eng='Super Admin');

