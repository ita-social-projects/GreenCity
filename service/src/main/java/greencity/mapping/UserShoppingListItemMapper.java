package greencity.mapping;

import greencity.dto.user.UserShoppingListItemVO;
import greencity.entity.ShoppingListItem;
import greencity.entity.HabitAssign;
import greencity.entity.UserShoppingListItem;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserShoppingListItemMapper extends AbstractConverter<UserShoppingListItemVO, UserShoppingListItem> {
    @Override
    protected UserShoppingListItem convert(UserShoppingListItemVO userShoppingListItemVO) {
        return UserShoppingListItem.builder()
            .id(userShoppingListItemVO.getId())
            .status(userShoppingListItemVO.getStatus())
            .habitAssign(HabitAssign.builder()
                .id(userShoppingListItemVO.getHabitAssign().getId())
                .status(userShoppingListItemVO.getHabitAssign().getStatus())
                .habitStreak(userShoppingListItemVO.getHabitAssign().getHabitStreak())
                .duration(userShoppingListItemVO.getHabitAssign().getDuration())
                .lastEnrollmentDate(userShoppingListItemVO.getHabitAssign().getLastEnrollmentDate())
                .workingDays(userShoppingListItemVO.getHabitAssign().getWorkingDays())
                .build())
            .shoppingListItem(ShoppingListItem.builder()
                .id(userShoppingListItemVO.getShoppingListItemVO().getId())
                .build())
            .dateCompleted(userShoppingListItemVO.getDateCompleted())
            .build();
    }
}
