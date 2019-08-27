package greencity.service.impl;

import greencity.entity.enums.UserStatus;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class UserOwnSecurityServiceImpl implements UserOwnSecurityService {

    private UserOwnSecurityRepo repo;
    private UserService userService;
    private VerifyEmailService verifyEmailService;

    @Transactional
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
                    .userStatus(UserStatus.ACTIVATED)
                    .lastVisit(LocalDateTime.now())
                    .build();
            User savedUser = userService.save(user);
            repo.save(
                UserOwnSecurity.builder().password(dto.getPassword()).user(savedUser).build());
            verifyEmailService.save(savedUser);
        }
    }

    @Override
    public void delete(UserOwnSecurity userOwnSecurity) {
        repo.delete(userOwnSecurity);
    }

    @Scheduled(fixedRate = 86400000)
    @Override
    public void deleteNotActiveEmailUsers() {
        // 86400000 - доба
        log.info("begin");
        verifyEmailService
            .findAll()
            .forEach(
                verifyEmail -> {
                    if (verifyEmailService.isDateValidate(verifyEmail.getExpiryDate())) {
                        delete(verifyEmail.getUser().getUserOwnSecurity());
                        verifyEmailService.delete(verifyEmail);
                        userService.deleteById(verifyEmail.getUser().getId());
                    }
                });
        log.info("end");
    }
}
