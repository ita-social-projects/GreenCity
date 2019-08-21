package greencity.repository;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.entity.UserOwnSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOwnSecurityRepo extends JpaRepository<UserOwnSecurity, Long> {

    void register(UserRegisterDto dto);

    void delete(UserOwnSecurity userOwnSecurity);

    void deleteNotActiveEmailUsers();

}
