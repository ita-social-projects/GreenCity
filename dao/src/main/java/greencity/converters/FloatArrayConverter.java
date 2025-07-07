package greencity.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class FloatArrayConverter implements AttributeConverter<Float[], String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Float[] attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Float[] to JSON", e);
        }
    }

    @Override
    public Float[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new Float[0];
        }
        try {
            return objectMapper.readValue(dbData, Float[].class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to Float[]", e);
        }
    }
}
