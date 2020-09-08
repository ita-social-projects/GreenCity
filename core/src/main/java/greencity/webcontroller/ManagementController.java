package greencity.webcontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management")
public class ManagementController {
    /**
     * Returns index page.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     * @author Dovganyuk Taras
     */

    @GetMapping("")
    public String goToIndex(Model model) {
        return "core/index";
    }
}
