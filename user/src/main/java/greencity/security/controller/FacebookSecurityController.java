package greencity.security.controller;

import greencity.constant.HttpStatuses;
import greencity.security.service.FacebookSecurityService;
import greencity.security.dto.SuccessSignInDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static greencity.constant.ErrorMessage.BAD_FACEBOOK_TOKEN;

/**
 * Controller that provide google security logic.
 *
 * @author Oleh Yurchuk
 * @version 1.0
 */
@RestController
@RequestMapping("/facebookSecurity")
public class FacebookSecurityController {
    private final FacebookSecurityService facebookSecurityService;

    /**
     * Constructor.
     *
     * @param facebookSecurityService {@link FacebookSecurityService}
     */
    @Autowired
    public FacebookSecurityController(FacebookSecurityService facebookSecurityService) {
        this.facebookSecurityService = facebookSecurityService;
    }

    /**
     * Method that generate facebook authorization url.
     *
     * @return {@link String} facebook auth url
     */
    @ApiOperation("Generate Facebook Authorization URL")
    @GetMapping("/generateFacebookAuthorizeURL")
    public String generateFacebookAuthorizeURL() {
        return facebookSecurityService.generateFacebookAuthorizeURL();
    }

    /**
     * Method that provide authenticate with facebook token.
     *
     * @param code {@link String} - facebook token.
     * @return {@link SuccessSignInDto} if token valid
     */
    @ApiOperation("Make authentication by Facebook")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = SuccessSignInDto.class),
        @ApiResponse(code = 400, message = BAD_FACEBOOK_TOKEN)
    })
    @GetMapping("/facebook")
    public SuccessSignInDto generateFacebookAccessToken(@RequestParam("code") String code) {
        return facebookSecurityService.generateFacebookAccessToken(code);
    }
}
