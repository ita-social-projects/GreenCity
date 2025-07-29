package greencity.converters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class FloatArrayConverterTest {
    private FloatArrayConverter converter;
    private ObjectMapper mockObjectMapper;


    @BeforeEach
    void setUp() {
        converter = new FloatArrayConverter();
        mockObjectMapper = spy(ObjectMapper.class);
        ReflectionTestUtils.setField(converter, "objectMapper", mockObjectMapper);
    }

    @Test
    void testConvertToDatabaseColumn_NullInput() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void testConvertToDatabaseColumn_ValidArray() {
        Float[] array = new Float[]{1.1f, 2.2f, 3.3f};
        String result = converter.convertToDatabaseColumn(array);

        assertEquals("[1.1,2.2,3.3]", result);
    }

    @Test
    void testConvertToEntityAttribute_NullInput() {
        Float[] result = converter.convertToEntityAttribute(null);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testConvertToEntityAttribute_EmptyString() {
        Float[] result = converter.convertToEntityAttribute("");
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testConvertToEntityAttribute_ValidJson() {
        String json = "[1.1,2.2,3.3]";
        Float[] result = converter.convertToEntityAttribute(json);

        assertArrayEquals(new Float[]{1.1f, 2.2f, 3.3f}, result);
    }

    @Test
    void testConvertToEntityAttribute_InvalidJson() {
        String invalidJson = "[1.1, 2.2, not_a_number]";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute(invalidJson));

        assertTrue(exception.getMessage().contains("Error converting JSON to Float[]"));
    }

    @Test
    void testConvertToDatabaseColumn_InvalidObject() {
        Float[] arrayWithNull = new Float[]{1.0f, null};

        String result = converter.convertToDatabaseColumn(arrayWithNull);

        assertEquals("[1.0,null]", result);
    }
    @Test
    void testConvertToDatabaseColumn_ThrowsException() throws Exception {
        Float[] array = new Float[]{1.1f};

        when(mockObjectMapper.writeValueAsString(array))
                .thenThrow(new JsonProcessingException("Serialization error"){});

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> converter.convertToDatabaseColumn(array));

        assertTrue(exception.getMessage().contains("Error converting Float[] to JSON"));
        assertNotNull(exception.getCause());
    }
}
