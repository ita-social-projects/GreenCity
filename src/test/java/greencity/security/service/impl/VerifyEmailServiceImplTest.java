package greencity.security.service.impl;

import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.exception.exceptions.BadIdException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.security.repository.VerifyEmailRepo;
import greencity.service.EmailService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class VerifyEmailServiceImplTest {
    @Mock
    VerifyEmailRepo verifyEmailRepo;

    @Mock
    EmailService emailService;

    private VerifyEmailServiceImpl verifyEmailService;

    @Before
    public void init() {
        initMocks(this);
        verifyEmailService = new VerifyEmailServiceImpl(5, verifyEmailRepo, emailService);
    }

    @Test
    public void save() {
        when(verifyEmailRepo.save(any(VerifyEmail.class))).thenReturn(null);
        verifyEmailService.saveEmailVerificationTokenForUser(User.builder().email("").firstName("").build());
        verify(verifyEmailRepo, (times(1))).save(any(VerifyEmail.class));
    }

    @Test
    public void verifyByEmail() {
        VerifyEmail verifyEmail =
            VerifyEmail.builder().expiryDate(LocalDateTime.now().plusHours(2)).id(2L).build();
        when(verifyEmailRepo.findByToken(anyString())).thenReturn(Optional.of(verifyEmail));
        when(verifyEmailRepo.existsById(anyLong())).thenReturn(true);
        doNothing().when(verifyEmailRepo).delete(any(VerifyEmail.class));
        verifyEmailService.verifyByToken("some token");
        verify(verifyEmailRepo, times(1)).delete(any());
    }

    @Test(expected = UserActivationEmailTokenExpiredException.class)
    public void verifyIsNotActive() {
        VerifyEmail verifyEmail =
            VerifyEmail.builder().expiryDate(LocalDateTime.now().minusHours(2)).build();
        when(verifyEmailRepo.findByToken(anyString())).thenReturn(Optional.of(verifyEmail));
        verifyEmailService.verifyByToken("some token");
    }

    @Test
    public void isDateValidate() {
        assertTrue(verifyEmailService.isNotExpired(LocalDateTime.now().plusHours(24)));
        assertFalse(verifyEmailService.isNotExpired(LocalDateTime.now().minusHours(48)));
    }
}
