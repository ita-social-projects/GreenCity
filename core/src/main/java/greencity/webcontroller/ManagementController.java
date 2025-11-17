package greencity.webcontroller;

import greencity.properties.RemoteWebClientProperties;
import greencity.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class ManagementController {
    private final RemoteWebClientProperties remoteWebClientProperties;

    /**
     * Returns index page.
     *
     * @return model
     * @author Dovganyuk Taras
     */

    @GetMapping("/management")
    public String goToIndex() {
        return "core/index";
    }

    /**
     * Redirect to logIn page.
     *
     * @author Ihor Volianskyi
     */

    @GetMapping("/")
    public String redirectLogin() {
        return "redirect:/management/login";
    }

    /**
     * Redirect to logIn page on GreenCityUser or redirect to management page if you
     * already sign in.
     *
     * @author Ihor Volianskyi
     */

    @GetMapping("/management/login")
    public String login() {
        if (!SecurityUtils.isAuthenticated()) {
            String managementLoginUrl =
                UriComponentsBuilder.fromHttpUrl(remoteWebClientProperties.getGreencityUserServerAddress())
                    .path("/management/login")
                    .build()
                    .toUriString();
            return "redirect:" + managementLoginUrl;
        } else {
            return "redirect:/management";
        }
    }
}
