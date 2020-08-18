package greencity.webcontroller;

import greencity.annotations.ApiPageable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

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
    @ApiPageable
    @GetMapping("")
    public String getAllFacts(Model model, @ApiIgnore Pageable pageable) {
        return "core/management_fact_of_the_day";
    }
}
