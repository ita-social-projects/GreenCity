package greencity.service.impl;

import java.util.Calendar;
import java.util.Date;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.entity.VerifyEmail;
import greencity.exception.UserActivationEmailTokenExpiredException;
import greencity.repository.VerifyEmailRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VerifyEmailServiceImplTest {

    @Autowired private VerifyEmailRepo repo;
    @Autowired private VerifyEmailServiceImpl verifyEmailService;
    @Autowired private UserOwnSecurityServiceImpl userOwnSecurityService;
    @Autowired private UserServiceImpl userService;

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
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -48);
        verifyEmail.setExpiryDate(calendar.getTime());
        VerifyEmail save = repo.save(verifyEmail);
        verifyEmailService.verify(save.getToken());
    }

    @Test
    public void isDateValidate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        Date date = calendar.getTime();
        assertTrue(verifyEmailService.isDateValidate(date));

        calendar.add(Calendar.HOUR, -48);
        Date newDate = calendar.getTime();
        assertFalse(verifyEmailService.isDateValidate(newDate));
    }
}
