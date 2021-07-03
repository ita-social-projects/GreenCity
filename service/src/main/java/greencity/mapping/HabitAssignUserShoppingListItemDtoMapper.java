package greencity.mapping;

import greencity.dto.habit.HabitAssignUserShoppingListItemDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.entity.HabitAssign;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitAssign} into
 * {@link HabitAssignUserShoppingListItemDto}.
 */
@Component
public class HabitAssignUserShoppingListItemDtoMapper extends
    AbstractConverter<HabitAssign, HabitAssignUserShoppingListItemDto> {
    /**
     * Method convert {@link HabitAssign} to
     * {@link HabitAssignUserShoppingListItemDto}.
     *
     * @return {@link HabitAssignUserShoppingListItemDto}
     */
    @Override
    protected HabitAssignUserShoppingListItemDto convert(HabitAssign habitAssign) {
        return HabitAssignUserShoppingListItemDto.builder()
            .habitAssignId(habitAssign.getId())
            .userId(habitAssign.getUser().getId())
            .habitId(habitAssign.getHabit().getId())
            .status(habitAssign.getStatus())
            .workingDays(habitAssign.getWorkingDays())
            .duration(habitAssign.getDuration())
            .userShoppingListItemsDto(habitAssign.getUserShoppingListItems().stream()
                .map(u -> UserShoppingListItemAdvanceDto.builder().id(u.getId())
                    .shoppingListItemId(u.getShoppingListItem().getId())
                    .status(u.getStatus())
                    .dateCompleted(u.getDateCompleted())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
