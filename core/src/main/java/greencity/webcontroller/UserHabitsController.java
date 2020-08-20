package greencity.webcontroller;

import greencity.service.HabitDictionaryService;
import greencity.service.HabitService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@NoArgsConstructor
@RequestMapping("/management/habits")
public class UserHabitsController {
    private HabitService habitService;
    private HabitDictionaryService habitDictionaryService;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public UserHabitsController(HabitService habitService, HabitDictionaryService habitDictionaryService) {
        this.habitService = habitService;
        this.habitDictionaryService = habitDictionaryService;
    }

    /**
     * Returns management page with all facts of the day.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     * @author Dovganyuk Taras
     */

    @GetMapping("")
    public String getAllFacts(Model model) {
        model.addAttribute("habits", habitService.getAllHabitsDto());
        return "core/user_habits";
    }
}
