package greencity.webcontroller;

import greencity.dto.goal.GoalDto;
import greencity.dto.goal.GoalManagementDto;
import greencity.dto.habit.HabitDto;
import greencity.service.GoalService;
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
@RequestMapping("/management/habit-shoping-list")
public class ManagementHabitShopingListItemController {
    private final GoalService goalService;
    private final HabitService habitService;

    /**
     * Returns management page with {@link GoalManagementDto}.
     *
     * @param model Model that will be configured.
     * @return View template path {@link String}.
     * @author Marian Diakiv.
     */
    @GetMapping("")
    public String getAllShopingListGoals(@RequestParam("habitId") Long id, Model model, Pageable pageable) {
        model.addAttribute("allGoal", goalService.findAllGoalForManagementPageNotContained(id, pageable));
        model.addAttribute("habitId", id);
        model.addAttribute("currentCoals", goalService.getGoalByHabitId(id));
        return "core/management_habit_shoping_list_item";
    }

    /**
     * Controller for deleting {@link GoalDto} by given id.
     *
     * @param listId  list of IDs.
     * @param habitId - {@link HabitDto} the id of the instance from which it will
     *                be deleted.
     * @return {@link ResponseEntity}
     */
    @DeleteMapping("/deleteAll/")
    public ResponseEntity<List<Long>> deleteAllShopingListItem(@RequestParam("habitId") Long habitId,
        @RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitService.deleteAllGoalByListOfId(habitId, listId));
    }

    /**
     * Controller add all {@link GoalDto} by list of ids.
     *
     * @param listId  list of id {@link GoalDto}
     * @param habitId - {@link HabitDto} the id of the instance to which it will be
     *                added.
     * @return {@link ResponseEntity}
     * @author Marian Diakiv.
     */
    @PostMapping("/addAll/")
    public ResponseEntity<List<Long>> addAllShopingListItem(@RequestParam("habitId") Long habitId,
        @RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitService.addAllGoalByListOfId(habitId, listId));
    }

    /**
     * Controller for c the {@link GoalDto} instance by its id.
     *
     * @param goalId  - {@link GoalDto} instance id which will be deleted.
     * @param habitId - {@link HabitDto} the id of the instance from which it will
     *                be deleted.
     * @return {@link ResponseEntity}
     * @author Marian Diakiv.
     */
    @DeleteMapping("/delete/")
    public ResponseEntity<Long> deleteShopingListItem(@RequestParam("habitId") Long habitId,
        @RequestParam("goalId") Long goalId) {
        habitService.deleteGoal(habitId, goalId);
        return ResponseEntity.status(HttpStatus.OK).body(goalId);
    }

    /**
     * Controller add {@link GoalDto} by id.
     *
     * @param habitId - {@link HabitDto} the id of the instance to which it will be
     *                added.
     * @return {@link ResponseEntity}
     * @author Marian Diakiv.
     */
    @PostMapping("/add/")
    public ResponseEntity<Long> addShopingListItemToHabit(@RequestParam("habitId") Long habitId,
        @RequestParam("goalId") Long goalId) {
        habitService.addGoalToHabit(habitId, goalId);
        return ResponseEntity.status(HttpStatus.OK).body(goalId);
    }
}
