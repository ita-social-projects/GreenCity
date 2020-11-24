package greencity.security.service;

import greencity.entity.VerifyEmail;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.security.repository.VerifyEmailRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class VerifyEmailServiceImplTest {
    @Mock
    private VerifyEmailRepo verifyEmailRepo;

    @InjectMocks
    private VerifyEmailServiceImpl verifyEmailService;

    @BeforeEach
    public void init() {
        initMocks(this);
        String address = "http://localhost:4200";
        verifyEmailService = new VerifyEmailServiceImpl(verifyEmailRepo, address);
    }

    @Test
    void verifyByTokenNotExpiredTokenTest() throws URISyntaxException {
        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setExpiryDate(LocalDateTime.MAX);
        when(verifyEmailRepo.findByTokenAndUserId(1L, "token")).thenReturn(Optional.of(verifyEmail));
        verifyEmailService.verifyByToken(1L, "token");
        verify(verifyEmailRepo).deleteVerifyEmailByTokenAndUserId(1L, "token");
    }

    @Test
    void verifyByTokenExpiredTokenTest() {
        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setExpiryDate(LocalDateTime.MIN);
        when(verifyEmailRepo.findByTokenAndUserId(1L, "token")).thenReturn(Optional.of(verifyEmail));
        Assertions.assertThrows(UserActivationEmailTokenExpiredException.class,
            () -> verifyEmailService.verifyByToken(1L, "token"));
    }

    @Test
    void deleteAllUsersThatDidNotVerifyEmailTest() {
        verifyEmailService.deleteAllUsersThatDidNotVerifyEmail();
        verify(verifyEmailRepo).deleteAllUsersThatDidNotVerifyEmail();
    }
}
