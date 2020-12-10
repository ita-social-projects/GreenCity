package greencity.controller.chat;

import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@AllArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    UserService userService;
    ModelMapper modelMapper;
    RestTemplate restTemplate;

    @GetMapping
    public RedirectView redirectToChatService() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(System.getenv("REDIRECT_CHAT") + "/2");
        return redirectView;
    }
}