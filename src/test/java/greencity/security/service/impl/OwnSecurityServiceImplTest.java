package greencity.security.service.impl;

import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.exception.exceptions.BadIdException;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.service.impl.UserServiceImpl;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class OwnSecurityServiceImplTest {

    private User user =
        User.builder()
            .email("Nazar.stasyuk@gmail.com")
            .firstName("Nazar")
            .lastName("Stasyuk")
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    private OwnSignUpDto dto =
        OwnSignUpDto.builder()
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .password("123123123")
            .build();

    @InjectMocks
    private OwnSecurityServiceImpl service;

    @Mock
    private OwnSecurityRepo repo;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private VerifyEmailServiceImpl verifyEmailService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager manager;
    @Mock
    private JwtTool tokenTool;
}
