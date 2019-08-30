package greencity.service.impl;

import java.time.LocalDateTime;

import greencity.security.dto.own_security.OwnSignUpDto;
import greencity.entity.User;
import greencity.entity.OwnSecurity;
import greencity.entity.enums.ROLE;
import greencity.exception.BadEmailException;
import greencity.exception.BadIdException;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.service.impl.OwnSecurityServiceImpl;
import greencity.security.service.impl.VerifyEmailServiceImpl;
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
public class OwnSecurityServiceImplTest {

    User user =
            User.builder()
                    .email("Nazar.stasyuk@gmail.com")
                    .firstName("Nazar")
                    .lastName("Stasyuk")
                    .role(ROLE.USER_ROLE)
                    .lastVisit(LocalDateTime.now())
                    .dateOfRegistration(LocalDateTime.now())
                    .build();
    OwnSignUpDto dto =
            OwnSignUpDto.builder()
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .password("123123123")
                    .build();
    @Autowired private OwnSecurityServiceImpl service;
    @MockBean private OwnSecurityRepo repo;
    @MockBean private UserServiceImpl userService;
    @MockBean private VerifyEmailServiceImpl verifyEmailService;

    @Test
    public void registerNewUser() {
        when(userService.findByEmail(anyString())).thenReturn(null);
        when(userService.save(any(User.class))).thenReturn(user);
        when(repo.save(any())).thenReturn(new OwnSecurity());
        doNothing().when(verifyEmailService).save(any());

        service.register(dto);
        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).save(any());
        verify(repo, times(1)).save(any());
    }

    @Test
    public void registerUserThatWasRegisteredByAnotherMethod() {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(repo.save(any())).thenReturn(new OwnSecurity());
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
                        .ownSecurity(new OwnSecurity())
                        .dateOfRegistration(LocalDateTime.now())
                        .build();
        when(userService.findByEmail(anyString())).thenReturn(user);
        service.register(dto);
    }

    @Test
    public void delete() {
        doNothing().when(repo).delete(any(OwnSecurity.class));
        when(repo.existsById(anyLong())).thenReturn(true);
        service.delete(OwnSecurity.builder().id(1L).build());
        verify(repo, times(1)).delete(any(OwnSecurity.class));
    }

    @Test(expected = BadIdException.class)
    public void deleteNoExist() {
        doNothing().when(repo).delete(any(OwnSecurity.class));
        when(repo.existsById(anyLong())).thenReturn(false);
        service.delete(OwnSecurity.builder().id(1L).build());
    }

    @Test
    public void deleteNotActiveEmailUsers() {}
}
