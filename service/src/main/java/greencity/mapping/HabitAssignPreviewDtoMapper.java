package greencity.mapping;

import greencity.dto.habit.HabitAssignPreviewDto;
import greencity.dto.habit.HabitPreviewDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitTranslation;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitAssign} into
 * {@link HabitAssignPreviewDto}.
 */
@Component
public class HabitAssignPreviewDtoMapper extends AbstractConverter<HabitAssign, HabitAssignPreviewDto> {

    private final ModelMapper modelMapper;

    @Lazy
    public HabitAssignPreviewDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Method convert {@link HabitAssign} to {@link HabitAssignPreviewDto}.
     *
     * @return {@link HabitAssignPreviewDto}
     */
    @Override
    protected HabitAssignPreviewDto convert(HabitAssign habitAssign) {
        Habit habit = habitAssign.getHabit();
        List<HabitTranslation> habitTranslations = habitAssign.getHabit().getHabitTranslations();
        User habitAssignUser = habitAssign.getUser();
        UserVO habitAssignUserVO = modelMapper.map(habitAssignUser, UserVO.class);

        HabitTranslationDto habitTranslationDto = habitTranslations.stream()
            .filter(tr -> Objects.equals(tr.getLanguage().getCode(), habitAssignUserVO.getLanguage().getCode()))
            .findFirst().map(tr -> HabitTranslationDto.builder()
                .name(tr.getName())
                .description(tr.getDescription())
                .habitItem(tr.getHabitItem())
                .build())
            .orElseThrow(NotFoundException::new);
        HabitPreviewDto habitPreviewDto = HabitPreviewDto.builder()
            .id(habit.getId())
            .image(habit.getImage())
            .habitTranslation(habitTranslationDto)
            .build();
        return HabitAssignPreviewDto.builder()
            .id(habitAssign.getId())
            .status(habitAssign.getStatus())
            .userId(habitAssignUser.getId())
            .duration(habitAssign.getDuration())
            .workingDays(habitAssign.getWorkingDays())
            .habit(habitPreviewDto)
            .build();
    }
}
