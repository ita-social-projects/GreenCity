package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemVO;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.user.UserShoppingListItemVO;
import greencity.entity.UserShoppingListItem;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserShoppingListItemVOMapper extends AbstractConverter<UserShoppingListItem, UserShoppingListItemVO> {
    @Override
    protected UserShoppingListItemVO convert(UserShoppingListItem userShoppingListItem) {
        return UserShoppingListItemVO.builder()
            .id(userShoppingListItem.getId())
            .shoppingListItemVO(ShoppingListItemVO.builder()
                .id(userShoppingListItem.getShoppingListItem().getId())
                .build())
            .status(userShoppingListItem.getStatus())
            .habitAssign(HabitAssignVO.builder()
                .id(userShoppingListItem.getHabitAssign().getId()).build())
            .dateCompleted(userShoppingListItem.getDateCompleted())
            .build();
    }
}
