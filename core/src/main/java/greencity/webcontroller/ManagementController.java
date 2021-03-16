package greencity.webcontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String goToIndex() {
        return "core/index";
    }
}
