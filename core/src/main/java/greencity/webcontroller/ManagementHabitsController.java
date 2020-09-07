package greencity.webcontroller;

import greencity.service.HabitService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/management/habits")
public class ManagementHabitsController {
    private HabitService habitService;

    /**
     * Returns management page with all facts of the day.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     * @author Dovganyuk Taras
     */

    @GetMapping("")
    public String getAllHabits(Model model) {
        model.addAttribute("habits", habitService.getAllHabitsDto());
        return "core/management_user_habits";
    }
}
