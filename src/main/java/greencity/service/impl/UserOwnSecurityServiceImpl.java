package greencity.service.impl;

import java.time.LocalDateTime;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.dto.user_own_security.UserSignInDto;
import greencity.dto.user_own_security.UserSuccessSignInDto;
import greencity.entity.User;
import greencity.entity.UserOwnSecurity;
import greencity.entity.enums.ROLE;
import greencity.exception.BadEmailException;
import greencity.exception.BadEmailOrPasswordException;
import greencity.exception.BadIdException;
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
        log.info("begin");
        User byEmail = userService.findByEmail(dto.getEmail());

        if (byEmail != null) {
            // He has already registered
            if (byEmail.getUserOwnSecurity() == null) {
                // He has already registered by else method of registration
                repo.save(createUserOwnSecurityToUser(dto, byEmail));
                verifyEmailService.save(byEmail);
            } else {
                throw new BadEmailException("User with this email are already registered");
            }
        } else {
            User user = createNewRegisteredUser(dto);
            User savedUser = userService.save(user);
            repo.save(createUserOwnSecurityToUser(dto, savedUser));
            verifyEmailService.save(savedUser);
        }
        log.info("end");
    }

    private UserOwnSecurity createUserOwnSecurityToUser(UserRegisterDto dto, User user) {
        return UserOwnSecurity.builder().password(dto.getPassword()).user(user).build();
    }

    private User createNewRegisteredUser(UserRegisterDto dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .dateOfRegistration(LocalDateTime.now())
                .role(ROLE.USER_ROLE)
                .lastVisit(LocalDateTime.now())
                .build();
    }

    @Override
    public void delete(UserOwnSecurity userOwnSecurity) {
        log.info("begin");
        if (!repo.existsById(userOwnSecurity.getId())) {
            throw new BadIdException(
                    "No any userOwnSecurity to delete with this id: " + userOwnSecurity.getId());
        }
        repo.delete(userOwnSecurity);
        log.info("end");
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

    @Override
    public UserSuccessSignInDto signIn(UserSignInDto dto) {
        // This method will be change when we will add security
        User byEmail = userService.findByEmail(dto.getEmail());

        if (byEmail != null
                && byEmail.getUserOwnSecurity().getPassword().equals(dto.getPassword())
                && byEmail.getVerifyEmail() == null) {
            return UserSuccessSignInDto.builder()
                    .email(dto.getEmail())
                    .accessToken("Access token")
                    .refreshToken("Refresh token")
                    .build();
        } else {
            throw new BadEmailOrPasswordException("Bad email or password!");
        }
    }
}
