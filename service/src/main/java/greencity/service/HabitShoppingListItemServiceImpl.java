package greencity.service;

import greencity.entity.Habit;
import greencity.repository.HabitRepo;
import greencity.repository.ShoppingListItemRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class HabitShoppingListItemServiceImpl implements HabitShoppingListItemService {
    private final ShoppingListItemRepo shoppingListItemRepo;
    private final HabitRepo habitRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlinkShoppingListItems(List<Long> shopIds, Long habitId) {
        Habit h = habitRepo.findById(habitId).orElseThrow();
        shopIds.forEach(sh -> h.getShoppingListItems().remove(shoppingListItemRepo.findById(sh).orElseThrow()));
        habitRepo.save(h);
    }
}
