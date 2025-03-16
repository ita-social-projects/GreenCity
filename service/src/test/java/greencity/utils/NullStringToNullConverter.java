package greencity.utils;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

public class NullStringToNullConverter extends SimpleArgumentConverter {
    @Override
    protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        if (!(source instanceof String)) {
            throw new ArgumentConversionException("Can only convert from String");
        }
        if (targetType != String.class) {
            throw new ArgumentConversionException("Can only convert to String");
        }
        return "null".equals(source) ? null : source;
    }
}
