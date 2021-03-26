package greencity.webcontroller;

import greencity.constant.HttpStatuses;
import greencity.security.service.TokenService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {
    private final TokenService tokenService;

    /**
     * Method that gets access token from client and set it in cookies.
     *
     * @param accessToken {@link String}
     * @param response    {@link HttpServletResponse}
     * @return html view of management page.
     */
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @GetMapping
    public String passTokenToCookies(@RequestParam String accessToken, HttpServletResponse response) {
        tokenService.passTokenToCookies(accessToken, response);

        return "redirect:/management";
    }
}