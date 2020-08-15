package greencity.webcontroller;

import greencity.service.HabitService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@NoArgsConstructor
@RequestMapping("/management/")
public class UserHabitsController {
    private HabitService habitService;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public UserHabitsController(HabitService habitService) {
        this.habitService = habitService;
    }

    /**
     * Returns index page.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     */

    @GetMapping("/")
    public String goToIndex(Model model) {
        return "core/index";
    }

    /**
     * Returns management page with all facts of the day.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     */

    @GetMapping("/habits")
    public String getAllFacts(Model model) {
        model.addAttribute("habits", habitService.getAllHabitsDto());
        return "core/user_habits";
    }
}
