package greencity.webcontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/management")
@RequiredArgsConstructor
public class AboutUsController {
    @GetMapping("/aboutus")
    public String aboutUs(Model model) {
        return "core/about_us";
    }
}
