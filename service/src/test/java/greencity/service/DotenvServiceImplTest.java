package greencity.service;

import greencity.exception.exceptions.BadSecretKeyException;
import greencity.exception.exceptions.FunctionalityNotAvailableException;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DotenvServiceImplTest {

    @InjectMocks
    private DotenvServiceImpl dotenvService;

    @Mock
    private Dotenv dotenv;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void validateSecretKeyShouldThrowExceptionWhenKeyDoesNotMatch() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).reloadEnvFile();

        when(dotenv.get("logs.secretKey")).thenReturn(secretKey);

        when(passwordEncoder.matches(secretKey, "validSecret")).thenReturn(false);

        assertThrows(BadSecretKeyException.class, () -> spyService.validateSecretKey(secretKey));
    }

    @Test
    void validateSecretKeyShouldSucceedWhenKeyMatches() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).reloadEnvFile();

        when(dotenv.get("logs.secretKey")).thenReturn(secretKey);

        when(passwordEncoder.matches(secretKey, "validSecret")).thenReturn(true);

        spyService.validateSecretKey(secretKey);
    }

    @Test
    void validateSecretKeyShouldThrowFunctionalityNotAvailableExceptionWhenDotenvLoadFails() {
        String secretKey = "secret";

        assertThrows(FunctionalityNotAvailableException.class, () -> dotenvService.validateSecretKey(secretKey));
    }

    @Test
    void deleteDotenvFileShouldDeleteIfSecretKeyIsValid() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).validateSecretKey(secretKey);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);
            mockedFiles.when(() -> Files.deleteIfExists(any())).thenReturn(true);
            assertDoesNotThrow(() -> spyService.deleteDotenvFile(secretKey));
        }
    }

    @Test
    void deleteDotenvFileShouldThrowFunctionalityNotAvailableExceptionWhenDotenvFileDoesNotExist() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).validateSecretKey(secretKey);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(false);
            assertThrows(FunctionalityNotAvailableException.class, () -> spyService.deleteDotenvFile(secretKey));
        }
    }

    @Test
    void deleteDotenvFileShouldThrowFunctionalityNotAvailableExceptionIfCannotDelete() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).validateSecretKey(secretKey);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);
            mockedFiles.when(() -> Files.deleteIfExists(any())).thenReturn(false);
            assertThrows(FunctionalityNotAvailableException.class, () -> spyService.deleteDotenvFile(secretKey));
        }
    }

    @Test
    void deleteDotenvFileShouldThrowFunctionalityNotAvailableExceptionIfDeletingThrowsIOException() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).validateSecretKey(secretKey);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);
            mockedFiles.when(() -> Files.deleteIfExists(any())).thenThrow(IOException.class);
            assertThrows(FunctionalityNotAvailableException.class, () -> spyService.deleteDotenvFile(secretKey));
        }
    }
}