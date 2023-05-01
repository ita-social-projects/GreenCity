package greencity.mapping;

import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.entity.Habit;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class CustomHabitMapper extends AbstractConverter<AddCustomHabitDtoRequest, Habit> {
    @Override
    public Habit convert(AddCustomHabitDtoRequest addCustomHabitDtoRequest) {
        return Habit.builder()
            .image(addCustomHabitDtoRequest.getImage())
            .complexity(addCustomHabitDtoRequest.getComplexity())
            .defaultDuration(addCustomHabitDtoRequest.getDefaultDuration())
            .isCustomHabit(true)
            .build();
    }
}
