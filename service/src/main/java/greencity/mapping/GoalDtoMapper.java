package greencity.mapping;

import greencity.dto.goal.GoalDto;
import greencity.entity.localization.GoalTranslation;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link GoalTranslation} into
 * {@link GoalDto}.
 */
@Component
public class GoalDtoMapper extends AbstractConverter<GoalTranslation, GoalDto> {
    /**
     * Method for converting {@link GoalTranslation} into {@link GoalDto}.
     *
     * @param goalTranslation object to convert.
     * @return converted object.
     */
    @Override
    protected GoalDto convert(GoalTranslation goalTranslation) {
        return new GoalDto(goalTranslation.getGoal().getId(), goalTranslation.getContent());
    }
}
