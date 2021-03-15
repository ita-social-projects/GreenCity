package greencity.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@AllArgsConstructor
@RequestMapping("/chat")
public class RedirectChatController {
    /**
     * {@inheritDoc}
     */
    @GetMapping
    public RedirectView redirectToChatService() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(System.getenv("REDIRECT_CHAT") + "/2");
        return redirectView;
    }
}
