package greencity.service;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.dto.user_own_security.UserSignInDto;
import greencity.dto.user_own_security.UserSuccessSignInDto;
import greencity.entity.UserOwnSecurity;

public interface UserOwnSecurityService {

    void register(UserRegisterDto dto);

    void delete(UserOwnSecurity userOwnSecurity);

    void deleteNotActiveEmailUsers();

    UserSuccessSignInDto signIn(UserSignInDto dto);
}
