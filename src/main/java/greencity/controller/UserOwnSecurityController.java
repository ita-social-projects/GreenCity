package greencity.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.dto.user_own_security.UserSignInDto;
import greencity.dto.user_own_security.UserSuccessSignInDto;
import greencity.service.UserOwnSecurityService;
import greencity.service.VerifyEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that provide our sign-up and sign-in logic.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@RestController
@RequestMapping("/ownSecurity")
public class UserOwnSecurityController {

    @Value("${client.address}")
    private String clientAddress;

    private UserOwnSecurityService service;
    private VerifyEmailService verifyEmailService;

    /**
     * Constructor.
     *
     * @param service - {@link UserOwnSecurityService} - service for security logic.
     * @param verifyEmailService {@link VerifyEmailService} - service for verify email logic.
     */
    public UserOwnSecurityController(
            UserOwnSecurityService service, VerifyEmailService verifyEmailService) {
        this.service = service;
        this.verifyEmailService = verifyEmailService;
    }

    /**
     * Method for sign-up by our security logic.
     *
     * @param dto - {@link UserRegisterDto} that have sign-up information.
     * @return {@link ResponseEntity}
     */
    @PostMapping("/signUp")
    public ResponseEntity singUp(@Valid @RequestBody UserRegisterDto dto) {
        service.signUp(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Method for sign-in by our security logic.
     *
     * @param dto - {@link UserSignInDto} that have sign-in information.
     * @return {@link ResponseEntity}
     */
    @PostMapping("/signIn")
    public UserSuccessSignInDto singIn(@Valid @RequestBody UserSignInDto dto) {
        return service.signIn(dto);
    }

    /**
     * Method for verifying users email.
     *
     * @param token - {@link String} this is token (hash) to verify user.
     * @return {@link ResponseEntity}
     */
    @GetMapping("/verifyEmail")
    public ResponseEntity verify(@RequestParam @NotBlank String token) {
        verifyEmailService.verifyByToken(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Method for refresh access token.
     *
     * @param refreshToken - {@link String} this is refresh token.
     * @return {@link String} - new access token.
     */
    @PostMapping("/updateAccessToken")
    public String updateAccessToken(@RequestBody @NotBlank String refreshToken) {
        return service.updateAccessToken(refreshToken);
    }
}
