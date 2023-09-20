package greencity.mapping;

import greencity.dto.habit.CustomHabitDtoRequest;
import greencity.entity.Habit;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class CustomHabitMapper extends AbstractConverter<CustomHabitDtoRequest, Habit> {
    @Override
    public Habit convert(CustomHabitDtoRequest addCustomHabitDtoRequest) {
        return Habit.builder()
            .image(addCustomHabitDtoRequest.getImage())
            .complexity(addCustomHabitDtoRequest.getComplexity())
            .defaultDuration(addCustomHabitDtoRequest.getDefaultDuration())
            .isCustomHabit(true)
            .build();
    }
}
