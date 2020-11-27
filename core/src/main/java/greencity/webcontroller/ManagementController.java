package greencity.webcontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management")
public class ManagementController {
    /**
     * Returns index page.
     *
     * @return model
     * @author Dovganyuk Taras
     */

    @GetMapping
    public String goToIndex(@RequestParam String accessToken, Model model) {
        model.addAttribute("accessToken", accessToken);
        return "core/index";
    }
}
