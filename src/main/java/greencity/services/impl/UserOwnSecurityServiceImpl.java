package greencity.services.impl;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.entities.User;
import greencity.entities.UserOwnSecurity;
import greencity.repositories.UserOwnSecurityRepo;
import greencity.services.UserOwnSecurityService;
import greencity.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserOwnSecurityServiceImpl implements UserOwnSecurityService {

    private UserOwnSecurityRepo repo;
    private UserService userService;

    @Override
    public void register(UserRegisterDto dto) {
        User byEmail = userService.findByEmail(dto.getEmail());

        if (byEmail != null) {
            // He has already registered by else method of registration
            repo.save(UserOwnSecurity.builder().password(dto.getPassword()).user(byEmail).build());
        } else {
            User user =
                    User.builder()
                            .firstName(dto.getFirstName())
                            .lastName(dto.getLastName())
                            .email(dto.getEmail())
                            .lastVisit(LocalDateTime.now())
                            .build();
            User savedUser = userService.save(user);
            repo.save(
                    UserOwnSecurity.builder().password(dto.getPassword()).user(savedUser).build());
        }
    }
}
