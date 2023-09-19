package greencity.mapping;

import greencity.dto.habit.AddUpdateCustomHabitDtoRequest;
import greencity.entity.Habit;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class CustomHabitMapper extends AbstractConverter<AddUpdateCustomHabitDtoRequest, Habit> {
    @Override
    public Habit convert(AddUpdateCustomHabitDtoRequest addCustomHabitDtoRequest) {
        return Habit.builder()
            .image(addCustomHabitDtoRequest.getImage())
            .complexity(addCustomHabitDtoRequest.getComplexity())
            .defaultDuration(addCustomHabitDtoRequest.getDefaultDuration())
            .isCustomHabit(true)
            .build();
    }
}
