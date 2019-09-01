package greencity.service.impl;

import static org.junit.Assert.*;

import greencity.dto.userownsecurity.UserRegisterDto;
import greencity.entity.VerifyEmail;
import greencity.exception.UserActivationEmailTokenExpiredException;
import greencity.repository.VerifyEmailRepo;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VerifyEmailServiceImplTest {
    @Autowired
    private VerifyEmailRepo repo;
    @Autowired
    private VerifyEmailServiceImpl verifyEmailService;
    @Autowired
    private UserOwnSecurityServiceImpl userOwnSecurityService;
    @Autowired
    private UserServiceImpl userService;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void verify() {
        UserRegisterDto dto =
            UserRegisterDto.builder()
                .email("Nazar.stasyuk@gmail.com")
                .firstName("Nazar")
                .lastName("Stasyuk")
                .password("123123")
                .build();
        userOwnSecurityService.register(dto);

        VerifyEmail verifyEmail =
            repo.findByUser(userService.findByEmail("Nazar.stasyuk@gmail.com"));
        assertNotNull("Verify email is null", verifyEmail);

        String token = verifyEmail.getToken();

        assertNotNull("token is null", token);

        verifyEmailService.verify(token);

        VerifyEmail verifyEmailAfterVerify =
            repo.findByUser(userService.findByEmail("Nazar.stasyuk@gmail.com"));

        assertNull("verify email is not deleted!!!", verifyEmailAfterVerify);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test(expected = UserActivationEmailTokenExpiredException.class)
    public void verifyIsNotActive() {
        UserRegisterDto dto =
            UserRegisterDto.builder()
                .email("Nazar.stasyuk@gmail.com")
                .firstName("Nazar")
                .lastName("Stasyuk")
                .password("123123")
                .build();
        userOwnSecurityService.register(dto);

        VerifyEmail verifyEmail =
            repo.findByUser(userService.findByEmail("Nazar.stasyuk@gmail.com"));

        verifyEmail.setExpiryDate(LocalDateTime.now().minusHours(48));

        VerifyEmail save = repo.save(verifyEmail);
        verifyEmailService.verify(save.getToken());
    }

    @Test
    public void isDateValidate() {
        assertTrue(verifyEmailService.isDateValidate(LocalDateTime.now().plusHours(24)));
        assertFalse(verifyEmailService.isDateValidate(LocalDateTime.now().minusHours(48)));
    }
}
