package greencity.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EndpointValidatorTest {

    @InjectMocks
    private EndpointValidator endpointValidator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(endpointValidator, "validEndpoints",
            List.of("/api/test", "/api/valid", "friends/{friendId}/request/{id}",
                "friends/{friendId}/request/{status}"));
        ReflectionTestUtils.setField(endpointValidator, "validStatuses",
            List.of("pending", "approved", "rejected"));
    }

    @Test
    void hasExtraCharactersWithValidEndpointTest() {
        boolean result = endpointValidator.checkUrl("/api/testse");
        assertFalse(result, "Method should return false for invalid URL");
    }

    @Test
    void hasExtraCharactersWithInvalidEndpointTest() {
        boolean result = endpointValidator.checkUrl("/api/invalid");
        assertFalse(result, "Method should return false for invalid URL");
    }

    @Test
    void isValidEndpointWithValidIdTest() {
        boolean result = endpointValidator.checkUrl("friends/2/request/3");
        assertTrue(result, "Method should return true for valid URL with numeric values");
    }

    @Test
    void isValidEndpointWithValidStatusTest() {
        boolean result = endpointValidator.checkUrl("friends/2/request/pending");
        assertTrue(result, "Method should return true for valid URL with status value");
    }

    @Test
    void isValidEndpointWithInvalidIdTest() {
        boolean result = endpointValidator.checkUrl("friends/2/request/abc");
        assertFalse(result, "Method should return false for invalid URL with non-numeric value");
    }

    @Test
    void checkUrlWithValidEndpointTest() {
        boolean result = endpointValidator.checkUrl("/api/test");
        assertTrue(result, "Method should return true for valid URL");
    }

    @Test
    void checkUrlWithValidEndpointWithVariablesTest() {
        boolean result = endpointValidator.checkUrl("friends/2/request/1");
        assertTrue(result, "Method should return true for URL matching valid endpoint with variables");
    }
}
