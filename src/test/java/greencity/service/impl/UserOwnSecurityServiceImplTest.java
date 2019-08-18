package greencity.service.impl;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.exception.BadEmailException;
import greencity.service.UserOwnSecurityService;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserOwnSecurityServiceImplTest {
    @Autowired
    private UserOwnSecurityService service;

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
