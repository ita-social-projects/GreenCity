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

    /**
     * Converts a {@code Float[]} array into a JSON {@code String} for database storage.
     *
     * @param attribute the {@code Float[]} array to be converted
     * @return a JSON string representation of the array, or {@code null} if the input is {@code null}
     * @throws IllegalArgumentException if the array cannot be converted to JSON
     */
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

    /**
     * Converts a JSON {@code String} retrieved from the database back into a {@code Float[]} array.
     *
     * @param dbData the JSON string from the database
     * @return a {@code Float[]} array parsed from the JSON, or an empty array if input is {@code null} or empty
     * @throws IllegalArgumentException if the string cannot be parsed into a {@code Float[]} array
     */
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
