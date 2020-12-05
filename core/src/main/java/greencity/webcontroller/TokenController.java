package greencity.webcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/token")
public class TokenController {
    /**
     * Method that gets access token from client and set it in cookies.
     *
     * @param accessToken {@link String}
     * @param response    {@link HttpServletResponse}
     * @return html view of management page.
     */
    @PostMapping
    public String passToken(@RequestBody String accessToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        response.addCookie(cookie);

        return "redirect:/management";
    }
}
