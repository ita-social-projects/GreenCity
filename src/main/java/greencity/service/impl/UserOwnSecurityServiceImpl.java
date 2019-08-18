package greencity.service.impl;

import java.time.LocalDateTime;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.entity.User;
import greencity.entity.UserOwnSecurity;
import greencity.entity.enums.ROLE;
import greencity.exception.BadEmailException;
import greencity.repository.UserOwnSecurityRepo;
import greencity.service.UserOwnSecurityService;
import greencity.service.UserService;
import greencity.service.VerifyEmailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserOwnSecurityServiceImpl implements UserOwnSecurityService {

    private UserOwnSecurityRepo repo;
    private UserService userService;
    private VerifyEmailService verifyEmailService;

    @Override
    public void register(UserRegisterDto dto) {
        User byEmail = userService.findByEmail(dto.getEmail());

        if (byEmail != null) {
            // He has already registered
            if (byEmail.getUserOwnSecurity() == null) {
                // He has already registered by else method of registration
                repo.save(
                        UserOwnSecurity.builder()
                                .password(dto.getPassword())
                                .user(byEmail)
                                .build());
                verifyEmailService.save(byEmail);
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
            verifyEmailService.save(savedUser);
        }
    }
}
