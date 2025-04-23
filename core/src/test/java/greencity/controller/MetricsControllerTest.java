package greencity.controller;

import greencity.dto.metric.LoginEventDto;
import greencity.metrics.ActiveUsersInMemoryMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class MetricsControllerTest {

    @Mock
    private ActiveUsersInMemoryMetrics activeUsersInMemoryMetrics;

    @InjectMocks
    private MetricsController metricsController;

    private LoginEventDto validLoginEventDto;

    @BeforeEach
    void setUp() {
        validLoginEventDto = new LoginEventDto("user@example.com", 1234567890L);
    }

    @Test
    void recordLoginWithValidDtoSucceedsTest() {
        ResponseEntity<Void> response = metricsController.recordLogin(validLoginEventDto);

        verify(activeUsersInMemoryMetrics).recordLogin("user@example.com", 1234567890L);
        verifyNoMoreInteractions(activeUsersInMemoryMetrics);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void recordLoginWithNullEmailInvokesServiceTest() {
        LoginEventDto dtoWithNullEmail = new LoginEventDto(null, 1234567890L);

        ResponseEntity<Void> response = metricsController.recordLogin(dtoWithNullEmail);

        verify(activeUsersInMemoryMetrics).recordLogin(null, 1234567890L);
        verifyNoMoreInteractions(activeUsersInMemoryMetrics);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void recordLoginWithNullLoginTimeInvokesServiceTest() {
        LoginEventDto dtoWithNullLoginTime = new LoginEventDto("user@example.com", null);

        ResponseEntity<Void> response = metricsController.recordLogin(dtoWithNullLoginTime);

        verify(activeUsersInMemoryMetrics).recordLogin("user@example.com", null);
        verifyNoMoreInteractions(activeUsersInMemoryMetrics);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void recordLoginWithEmptyDtoInvokesServiceTest() {
        LoginEventDto emptyDto = new LoginEventDto();

        ResponseEntity<Void> response = metricsController.recordLogin(emptyDto);

        verify(activeUsersInMemoryMetrics).recordLogin(null, null);
        verifyNoMoreInteractions(activeUsersInMemoryMetrics);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }
}
