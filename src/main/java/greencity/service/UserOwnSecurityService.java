package greencity.service;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.entity.UserOwnSecurity;

public interface UserOwnSecurityService {

    void register(UserRegisterDto dto);

    void delete(UserOwnSecurity userOwnSecurity);

    void deleteNotActiveEmailUsers();
}
