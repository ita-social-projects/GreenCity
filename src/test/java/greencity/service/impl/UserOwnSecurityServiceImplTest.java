package greencity.service.impl;

import java.time.LocalDateTime;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.entity.User;
import greencity.entity.UserOwnSecurity;
import greencity.entity.enums.ROLE;
import greencity.exception.BadEmailException;
import greencity.exception.BadIdException;
import greencity.repository.UserOwnSecurityRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UserOwnSecurityServiceImplTest {

    User user =
            User.builder()
                    .email("Nazar.stasyuk@gmail.com")
                    .firstName("Nazar")
                    .lastName("Stasyuk")
                    .role(ROLE.USER_ROLE)
                    .lastVisit(LocalDateTime.now())
                    .dateOfRegistration(LocalDateTime.now())
                    .build();
    UserRegisterDto dto =
            UserRegisterDto.builder()
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .password("123123123")
                    .build();
    @Autowired private UserOwnSecurityServiceImpl service;
    @MockBean private UserOwnSecurityRepo repo;
    @MockBean private UserServiceImpl userService;
    @MockBean private VerifyEmailServiceImpl verifyEmailService;

    @Test
    public void registerNewUser() {
        when(userService.findByEmail(anyString())).thenReturn(null);
        when(userService.save(any(User.class))).thenReturn(user);
        when(repo.save(any())).thenReturn(new UserOwnSecurity());
        doNothing().when(verifyEmailService).save(any());

        service.register(dto);
        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).save(any());
        verify(repo, times(1)).save(any());
    }

    @Test
    public void registerUserThatWasRegisteredByAnotherMethod() {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(repo.save(any())).thenReturn(new UserOwnSecurity());
        doNothing().when(verifyEmailService).save(any());

        service.register(dto);
        verify(userService, times(1)).findByEmail(anyString());
        verify(repo, times(1)).save(any());
    }

    @Test(expected = BadEmailException.class)
    public void registerSameUser() {
        User user =
                User.builder()
                        .email("Nazar.stasyuk@gmail.com")
                        .firstName("Nazar")
                        .lastName("Stasyuk")
                        .role(ROLE.USER_ROLE)
                        .lastVisit(LocalDateTime.now())
                        .userOwnSecurity(new UserOwnSecurity())
                        .dateOfRegistration(LocalDateTime.now())
                        .build();
        when(userService.findByEmail(anyString())).thenReturn(user);
        service.register(dto);
    }

    @Test
    public void delete() {
        doNothing().when(repo).delete(any(UserOwnSecurity.class));
        when(repo.existsById(anyLong())).thenReturn(true);
        service.delete(UserOwnSecurity.builder().id(1L).build());
        verify(repo, times(1)).delete(any(UserOwnSecurity.class));
    }

    @Test(expected = BadIdException.class)
    public void deleteNoExist() {
        doNothing().when(repo).delete(any(UserOwnSecurity.class));
        when(repo.existsById(anyLong())).thenReturn(false);
        service.delete(UserOwnSecurity.builder().id(1L).build());
    }

    @Test
    public void deleteNotActiveEmailUsers() {}
}
