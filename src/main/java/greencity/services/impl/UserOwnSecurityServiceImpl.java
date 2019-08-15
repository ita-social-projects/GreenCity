package greencity.services.impl;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.entities.User;
import greencity.entities.UserOwnSecurity;
import greencity.entities.enums.ROLE;
import greencity.exceptions.BadEmailException;
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
            if (byEmail.getUserOwnSecurity() == null) {
                repo.save(
                        UserOwnSecurity.builder()
                                .password(dto.getPassword())
                                .user(byEmail)
                                .build());
            } else {
                throw new BadEmailException("User with this email are already registered");
            }
        } else {
            User user =
                    User.builder()
                            .firstName(dto.getFirstName())
                            .lastName(dto.getLastName())
                            .email(dto.getEmail())
                            .dateOfRegistration(LocalDateTime.now())
                            .role(ROLE.USER_ROLE)
                            .lastVisit(LocalDateTime.now())
                            .build();
            User savedUser = userService.save(user);
            repo.save(
                    UserOwnSecurity.builder().password(dto.getPassword()).user(savedUser).build());
        }
    }
}
