package greencity.service.impl;

import greencity.controller.VerifyEmailController;
import greencity.dto.user_own_security.UserRegisterDto;
import greencity.exception.BadEmailException;
import greencity.exception.UserActivationEmailTokenExpiredException;
import greencity.service.UserOwnSecurityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserOwnSecurityServiceImplTest {
    @Autowired
    private UserOwnSecurityServiceImpl service;

    @Test
    public void register() {
        UserRegisterDto dto =
                UserRegisterDto.builder()
                        .email("Nazar.stasyuk@gmail.com")
                        .firstName("Nazar")
                        .lastName("Stasyuk")
                        .password("123123")
                        .build();
        service.register(dto);
    }

    @Test(expected = BadEmailException.class)
    public void registerSameUser() {
        register();
        register();
    }
}
