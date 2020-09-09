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
    private SearchNewsDtoMapper searchNewsDtoMapper;
    private SearchTipsAndTricksDtoMapper searchTipsAndTricksDtoMapper;
    private DiscountValueMapper discountValueMapper;
    private FavoritePlaceWithLocationMapper favoritePlaceWithLocationMapper;
    private AdviceTranslateMapper adviceTranslateMapper;
    private FavoritePlaceDtoMapper favoritePlaceDtoMapper;
    private FavoritePlaceMapper favoritePlaceMapper;
    private ProposePlaceMapper proposePlaceMapper;
    private TipsAndTricksDtoRequestMapper tipsAndTricksDtoRequestMapper;
    private TipsAndTricksDtoResponseMapper tipsAndTricksDtoResponseMapper;
    private TipsAndTricksDtoManagementMapper tipsAndTricksDtoManagementMapper;
    private MultipartBase64ImageMapper multipartBase64ImageMapper;
    private AddEcoNewsCommentDtoRequestMapper addEcoNewsCommentDtoRequestMapper;
    private EcoNewsCommentDtoMapper ecoNewsCommentDtoMapper;
    private AddEcoNewsCommentDtoResponseMapper addEcoNewsCommentDtoResponseMapper;
    private TipsAndTricksAuthorDtoMapper andTricksAuthorDtoMapper;
    private TipsAndTricksCommentDtoMapper tipsAndTricksCommentDtoMapper;
    private AddTipsAndTricksCommentDtoRequestMapper addTipsAndTricksCommentDtoRequestMapper;
    private AddTipsAndTricksCommentDtoResponseMapper addTipsAndTricksCommentDtoResponseMapper;
    private HabitStatusDtoMapper habitStatusDtoMapper;

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
        modelMapper.addConverter(searchTipsAndTricksDtoMapper);
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
        modelMapper.addConverter(tipsAndTricksDtoRequestMapper);
        modelMapper.addConverter(tipsAndTricksDtoResponseMapper);
        modelMapper.addConverter(tipsAndTricksDtoManagementMapper);
        modelMapper.addConverter(multipartBase64ImageMapper);
        modelMapper.addConverter(addEcoNewsCommentDtoRequestMapper);
        modelMapper.addConverter(ecoNewsCommentDtoMapper);
        modelMapper.addConverter(addEcoNewsCommentDtoResponseMapper);
        modelMapper.addConverter(andTricksAuthorDtoMapper);
        modelMapper.addConverter(tipsAndTricksCommentDtoMapper);
        modelMapper.addConverter(addTipsAndTricksCommentDtoRequestMapper);
        modelMapper.addConverter(addTipsAndTricksCommentDtoResponseMapper);
        modelMapper.addConverter(habitStatusDtoMapper);
    }
}
