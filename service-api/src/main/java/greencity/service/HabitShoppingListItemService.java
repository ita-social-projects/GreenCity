package greencity.service;

import greencity.dto.shoppinglistitem.ShoppingListItemVO;

import java.util.List;

public interface HabitShoppingListItemService {
    /**
     * Method to unlink ShoppingListItems from Habit.
     *
     * @param habitId {@code Habit} id.
     * @param shopIds list of {@link ShoppingListItemVO} ids.
     * @author Vira Maksymets
     */
    void unlinkShoppingListItems(List<Long> shopIds, Long habitId);
}
