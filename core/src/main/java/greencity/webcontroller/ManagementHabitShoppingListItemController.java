package greencity.webcontroller;

import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.shoppinglistitem.ShoppingListItemManagementDto;
import greencity.dto.habit.HabitDto;
import greencity.service.ShoppingListItemService;
import greencity.service.HabitService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management/habit-shopping-list")
public class ManagementHabitShoppingListItemController {
    private final ShoppingListItemService shoppingListItemService;
    private final HabitService habitService;

    /**
     * Returns management page with {@link ShoppingListItemManagementDto}.
     *
     * @param model Model that will be configured.
     * @return View template path {@link String}.
     * @author Marian Diakiv.
     */
    @GetMapping("")
    public String getAllShoppingListItems(@RequestParam("habitId") Long id, Model model, Pageable pageable) {
        model.addAttribute("shoppingListItems",
            shoppingListItemService.findAllShoppingListItemsForManagementPageNotContained(id, pageable));
        model.addAttribute("habitId", id);
        model.addAttribute("currentShoppingListItems", shoppingListItemService.getShoppingListByHabitId(id));
        return "core/management_habit_shopping_list_item";
    }

    /**
     * Controller for deleting {@link ShoppingListItemDto} by given id.
     *
     * @param listId  list of IDs.
     * @param habitId - {@link HabitDto} the id of the instance from which it will
     *                be deleted.
     * @return {@link ResponseEntity}
     */
    @DeleteMapping("/deleteAll/")
    public ResponseEntity<List<Long>> deleteAllShoppingListItem(@RequestParam("habitId") Long habitId,
        @RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitService.deleteAllShoppingListItemsByListOfId(habitId, listId));
    }

    /**
     * Controller add all {@link ShoppingListItemDto} by list of ids.
     *
     * @param listId  list of id {@link ShoppingListItemDto}
     * @param habitId - {@link HabitDto} the id of the instance to which it will be
     *                added.
     * @return {@link ResponseEntity}
     * @author Marian Diakiv.
     */
    @PostMapping("/addAll/")
    public ResponseEntity<List<Long>> addAllShoppingListItem(@RequestParam("habitId") Long habitId,
        @RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitService.addAllShoppingListItemsByListOfId(habitId, listId));
    }

    /**
     * Controller for c the {@link ShoppingListItemDto} instance by its id.
     *
     * @param itemId  - {@link ShoppingListItemDto} instance id which will be
     *                deleted.
     * @param habitId - {@link HabitDto} the id of the instance from which it will
     *                be deleted.
     * @return {@link ResponseEntity}
     * @author Marian Diakiv.
     */
    @DeleteMapping("/delete/")
    public ResponseEntity<Long> deleteShoppingListItem(@RequestParam("habitId") Long habitId,
        @RequestParam("itemId") Long itemId) {
        habitService.deleteShoppingListItem(habitId, itemId);
        return ResponseEntity.status(HttpStatus.OK).body(itemId);
    }

    /**
     * Controller add {@link ShoppingListItemDto} by id.
     *
     * @param habitId - {@link HabitDto} the id of the instance to which it will be
     *                added.
     * @return {@link ResponseEntity}
     * @author Marian Diakiv.
     */
    @PostMapping("/add/")
    public ResponseEntity<Long> addShoppingListItemToHabit(@RequestParam("habitId") Long habitId,
        @RequestParam("itemId") Long itemId) {
        habitService.addShoppingListItemToHabit(habitId, itemId);
        return ResponseEntity.status(HttpStatus.OK).body(itemId);
    }
}
