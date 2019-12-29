package greencity.security.service.impl;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.exception.exceptions.BadIdException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.security.repository.VerifyEmailRepo;
import greencity.service.EmailService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class VerifyEmailServiceImplTest {

    @Mock
    private VerifyEmailRepo repo;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerifyEmailServiceImpl verifyEmailService;


    @Test
    public void save() {
        ReflectionTestUtils.setField(verifyEmailService, "expireTime", 5);
        when(repo.save(any(VerifyEmail.class))).thenReturn(null);
        verifyEmailService.save(User.builder().email("").firstName("").build());

        verify(repo, (times(1))).save(any(VerifyEmail.class));
    }

    @Test
    public void verifyByEmail() {
        VerifyEmail verifyEmail =
            VerifyEmail.builder().expiryDate(LocalDateTime.now().plusHours(2)).id(2L).build();
        when(repo.findByToken(anyString())).thenReturn(Optional.of(verifyEmail));
        when(repo.existsById(anyLong())).thenReturn(true);
        doNothing().when(repo).delete(any(VerifyEmail.class));
        verifyEmailService.verifyByToken("some token");
        verify(repo, times(1)).delete(any());
    }

    @Test(expected = UserActivationEmailTokenExpiredException.class)
    public void verifyIsNotActive() {
        VerifyEmail verifyEmail =
            VerifyEmail.builder().expiryDate(LocalDateTime.now().minusHours(2)).build();
        when(repo.findByToken(anyString())).thenReturn(Optional.of(verifyEmail));
        verifyEmailService.verifyByToken("some token");
    }

    @Test
    public void isDateValidate() {
        assertTrue(verifyEmailService.isNotExpired(LocalDateTime.now().plusHours(24)));
        assertFalse(verifyEmailService.isNotExpired(LocalDateTime.now().minusHours(48)));
    }


    @Test(expected = BadIdException.class)
    public void delete() {
        when(repo.existsById(anyLong())).thenReturn(false);
        verifyEmailService.delete(VerifyEmail.builder().id(1L).build());
    }
}
