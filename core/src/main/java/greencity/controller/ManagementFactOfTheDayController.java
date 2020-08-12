package greencity.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/management/factoftheday")
public class ManagementFactOfTheDayController {
    /**
     * Returns management page with all facts of the day.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     */
    @GetMapping("")
    public String getAllFacts(Model model) {
        return "core/management_fact_of_the_day";
    }
}
