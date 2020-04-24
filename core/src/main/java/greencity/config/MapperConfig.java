package greencity.config;

import greencity.mapping.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class MapperConfig {
    private UserGoalResponseDtoMapper userGoalResponseDtoMapper;
    private HabitMapper habitMapper;
    private GoalDtoMapper goalDtoMapper;
    private EcoNewsDtoMapper ecoNewsDtoMapper;
    private HabitCreateDtoMapper habitCreateDtoMapper;
    private HabitStatisticMapper habitStatisticMapper;
    private AddHabitStatisticDtoMapper addHabitStatisticDtoMapper;
    private AddEcoNewsDtoRequestMapper addEcoNewsDtoRequestMapper;
    private AddEcoNewsDtoResponseMapper addEcoNewsDtoResponseMapper;
    private EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper;
    private DiscountValueMapper discountValueMapper;
    private FavoritePlaceWithLocationMapper favoritePlaceWithLocationMapper;
    private AdviceTranslateMapper adviceTranslateMapper;
    private FavoritePlaceDtoMapper favoritePlaceDtoMapper;
    private FavoritePlaceMapper favoritePlaceMapper;

    /**
     * Provides a new ModelMapper object. Provides configuration for the object. Sets source
     * properties to be strictly matched to destination properties. Sets matching fields to be
     * enabled. Skips when the property value is {@code null}. Sets {@code AccessLevel} to private.
     *
     * @return the configured instance of {@code ModelMapper}.
     */
    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper
            .getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setFieldMatchingEnabled(true)
            .setSkipNullEnabled(true)
            .setFieldAccessLevel(AccessLevel.PRIVATE);
        addConverters(modelMapper);

        return modelMapper;
    }

    /**
     * Method for adding converters, that are used by {@link ModelMapper} to map entities to dto.
     *
     * @param modelMapper {@link ModelMapper} for which converters are added.
     */
    private void addConverters(ModelMapper modelMapper) {
        modelMapper.addConverter(goalDtoMapper);
        modelMapper.addConverter(userGoalResponseDtoMapper);
        modelMapper.addConverter(ecoNewsDtoMapper);
        modelMapper.addConverter(addEcoNewsDtoRequestMapper);
        modelMapper.addConverter(addEcoNewsDtoResponseMapper);
        modelMapper.addConverter(addHabitStatisticDtoMapper);
        modelMapper.addConverter(ecoNewsAuthorDtoMapper);
        modelMapper.addConverter(discountValueMapper);
        modelMapper.addConverter(habitCreateDtoMapper);
        modelMapper.addConverter(habitStatisticMapper);
        modelMapper.addConverter(habitMapper);
        modelMapper.addConverter(favoritePlaceWithLocationMapper);
        modelMapper.addConverter(adviceTranslateMapper);
        modelMapper.addConverter(favoritePlaceDtoMapper);
        modelMapper.addConverter(favoritePlaceMapper);
    }
}
