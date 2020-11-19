package greencity.mapping;

import greencity.dto.goal.GoalResponseDto;
import greencity.dto.goal.GoalTranslationDTO;
import greencity.dto.language.LanguageVO;
import greencity.entity.Goal;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link Goal} into
 * {@link GoalResponseDto}.
 */
@Component
public class GoalResponseDtoMapper extends AbstractConverter<Goal, GoalResponseDto> {

    @Override
    protected GoalResponseDto convert(Goal goal) {
        return GoalResponseDto.builder()
            .id(goal.getId())
            .translations(goal.getTranslations().stream().map(
                goalTranslation -> GoalTranslationDTO.builder()
                    .id(goalTranslation.getId())
                    .content(goalTranslation.getContent())
                    .language(LanguageVO.builder()
                        .id(goalTranslation.getLanguage().getId())
                        .code(goalTranslation.getLanguage().getCode())
                        .build())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
