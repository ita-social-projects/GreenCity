package greencity.controller;

import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@AllArgsConstructor
@RequestMapping("/chat")
public class RedirectChatController {
    private UserService userService;
    private ModelMapper modelMapper;

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