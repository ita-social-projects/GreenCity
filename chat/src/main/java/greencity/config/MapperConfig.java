package greencity.config;

import java.util.List;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    /**
     * Provides a new ModelMapper object and configuration.
     *
     * @param converters Converters, that are used by {@link ModelMapper} and are
     *                   discovered by Spring.
     * @return the configured instance of {@code ModelMapper}.
     */
    @Bean
    public ModelMapper getModelMapper(List<Converter<?, ?>> converters) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper
            .getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setFieldMatchingEnabled(true)
            .setSkipNullEnabled(true)
            .setFieldAccessLevel(AccessLevel.PRIVATE);

        converters.forEach(modelMapper::addConverter);

        return modelMapper;
    }
}
