package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.entity.*;
import greencity.enums.ShoppingListItemStatus;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class HabitAssignMapper extends AbstractConverter<HabitAssignDto, HabitAssign> {
    @Override
    protected HabitAssign convert(HabitAssignDto dto) {
        List<UserShoppingListItem> listOfShoppingListItem = new ArrayList<>();
        for (UserShoppingListItemAdvanceDto item : dto.getUserShoppingListItems()) {
            if (item.getStatus().equals(ShoppingListItemStatus.INPROGRESS)) {
                listOfShoppingListItem.add(UserShoppingListItem.builder()
                    .id(item.getId())
                    .dateCompleted(item.getDateCompleted())
                    .status(item.getStatus())
                    .shoppingListItem(ShoppingListItem.builder()
                        .id(item.getShoppingListItemId())
                        .build())
                    .build());
            }
        }
        return HabitAssign.builder()
            .id(dto.getId())
            .duration(dto.getDuration())
            .habitStreak(dto.getHabitStreak())
            .createDate(dto.getCreateDateTime())
            .status(dto.getStatus())
            .workingDays(dto.getWorkingDays())
            .lastEnrollmentDate(dto.getLastEnrollmentDate())
            .habit(Habit.builder()
                .id(dto.getHabit().getId())
                .complexity(dto.getHabit().getComplexity())
                .defaultDuration(dto.getDuration())
                .build())
            .userShoppingListItems(listOfShoppingListItem)
            .build();
    }
}
