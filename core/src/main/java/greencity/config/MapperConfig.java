package greencity.config;

import greencity.mapping.AddEcoNewsDtoRequestMapper;
import greencity.mapping.AddEcoNewsDtoResponseMapper;
import greencity.mapping.AddHabitStatisticDtoMapper;
import greencity.mapping.AddTipsAndTricksDtoRequestMapper;
import greencity.mapping.AddTipsAndTricksDtoResponseMapper;
import greencity.mapping.AdviceTranslateMapper;
import greencity.mapping.DiscountValueMapper;
import greencity.mapping.EcoNewsAuthorDtoMapper;
import greencity.mapping.EcoNewsDtoMapper;
import greencity.mapping.FavoritePlaceDtoMapper;
import greencity.mapping.FavoritePlaceMapper;
import greencity.mapping.FavoritePlaceWithLocationMapper;
import greencity.mapping.GoalDtoMapper;
import greencity.mapping.HabitCreateDtoMapper;
import greencity.mapping.HabitMapper;
import greencity.mapping.HabitStatisticMapper;
import greencity.mapping.ProposePlaceMapper;
import greencity.mapping.SearchNewsDtoMapper;
import greencity.mapping.UserGoalResponseDtoMapper;
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
    private SearchNewsDtoMapper searchNewsDtoMapper;
    private DiscountValueMapper discountValueMapper;
    private FavoritePlaceWithLocationMapper favoritePlaceWithLocationMapper;
    private AdviceTranslateMapper adviceTranslateMapper;
    private FavoritePlaceDtoMapper favoritePlaceDtoMapper;
    private FavoritePlaceMapper favoritePlaceMapper;
    private ProposePlaceMapper proposePlaceMapper;
    private AddTipsAndTricksDtoRequestMapper addTipsAndTricksDtoRequestMapper;
    private AddTipsAndTricksDtoResponseMapper addTipsAndTricksDtoResponseMapper;

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
        modelMapper.addConverter(searchNewsDtoMapper);
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
        modelMapper.addConverter(proposePlaceMapper);
        modelMapper.addConverter(addTipsAndTricksDtoRequestMapper);
        modelMapper.addConverter(addTipsAndTricksDtoResponseMapper);
    }
}
