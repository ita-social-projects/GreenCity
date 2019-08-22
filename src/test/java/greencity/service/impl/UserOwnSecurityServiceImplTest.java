package greencity.service.impl;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.exception.BadEmailException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserOwnSecurityServiceImplTest {
    @Autowired private UserOwnSecurityServiceImpl service;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
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
        UserRegisterDto dto =
                UserRegisterDto.builder()
                        .email("Nazar.stasyuk@gmail.com")
                        .firstName("Nazar")
                        .lastName("Stasyuk")
                        .password("123123")
                        .build();
        service.register(dto);
        service.register(dto);
    }
}
