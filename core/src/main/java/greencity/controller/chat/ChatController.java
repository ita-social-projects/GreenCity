package greencity.controller.chat;

import greencity.dto.user.UserVO;
import greencity.service.UserService;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String redirectToChatService() {
        //RedirectView redirectView = new RedirectView();
        //redirectView.setUrl(System.getenv("REDIRECT_CHAT"));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        UserVO userVO = modelMapper.map(userService.findById(1L), UserVO.class);
        HttpEntity<UserVO> entity = new HttpEntity<>(userVO, headers);
        System.out.println(entity);
        return restTemplate.exchange(
            System.getenv("REDIRECT_CHAT"), HttpMethod.POST, entity, String.class).getBody();
    }
}