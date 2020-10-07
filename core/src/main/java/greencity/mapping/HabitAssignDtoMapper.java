package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitAssign;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitAssign} into
 * {@link HabitAssignDto}.
 */
@Component
public class HabitAssignDtoMapper extends AbstractConverter<HabitAssign, HabitAssignDto> {
    /**
     * Method convert {@link HabitAssign} to {@link HabitAssignDto}.
     *
     * @return {@link HabitAssignDto}
     */
    @Override
    protected HabitAssignDto convert(HabitAssign habitAssign) {
        return HabitAssignDto.builder()
            .id(habitAssign.getId())
            .acquired(habitAssign.getAcquired())
            .suspended(habitAssign.getSuspended())
            .createDateTime(habitAssign.getCreateDate())
            .habit(HabitDto.builder()
                .id(habitAssign.getHabit().getId())
                .image(habitAssign.getHabit().getImage())
                .habitTranslations(habitAssign.getHabit().getHabitTranslations()
                    .stream().map(habitTranslation -> HabitTranslationDto.builder()
                        .id(habitTranslation.getId())
                        .description(habitTranslation.getDescription())
                        .habitItem(habitTranslation.getHabitItem())
                        .name(habitTranslation.getName())
                        .build()).collect(Collectors.toList()))
                .build())
            .habitStatus(HabitStatusDto.builder()
                .id(habitAssign.getHabitStatus().getId())
                .habitStreak(habitAssign.getHabitStatus().getHabitStreak())
                .lastEnrollmentDate(habitAssign.getHabitStatus().getLastEnrollmentDate())
                .workingDays(habitAssign.getHabitStatus().getWorkingDays())
                .build())
            .habitStatistic(habitAssign.getHabitStatistic()
                .stream().map(habitStatistic -> HabitStatisticDto.builder()
                        .id(habitStatistic.getId())
                        .createDate(habitStatistic.getCreateDate())
                        .amountOfItems(habitStatistic.getAmountOfItems())
                        .habitRate(habitStatistic.getHabitRate())
                    .build()).collect(Collectors.toList()))
            .build();
    }
}
