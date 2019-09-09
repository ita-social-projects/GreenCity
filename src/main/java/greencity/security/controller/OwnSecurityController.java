package greencity.security.controller;

import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.VerifyEmailService;
import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
public class OwnSecurityController {
    @Value("${client.address}")
    private String clientAddress;
    private OwnSecurityService service;
    private VerifyEmailService verifyEmailService;

    /**
     * Constructor.
     *
     * @param service            - {@link OwnSecurityService} - service for security logic.
     * @param verifyEmailService {@link VerifyEmailService} - service for verify email logic.
     */
    public OwnSecurityController(
        OwnSecurityService service, VerifyEmailService verifyEmailService) {
        this.service = service;
        this.verifyEmailService = verifyEmailService;
    }

    /**
     * Method for sign-up by our security logic.
     *
     * @param dto - {@link OwnSignUpDto} that have sign-up information.
     * @return {@link ResponseEntity}
     */
    @PostMapping("/signUp")
    public ResponseEntity singUp(@Valid @RequestBody OwnSignUpDto dto) {
        service.signUp(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Method for sign-in by our security logic.
     *
     * @param dto - {@link OwnSignInDto} that have sign-in information.
     * @return {@link ResponseEntity}
     */
    @PostMapping("/signIn")
    public SuccessSignInDto singIn(@Valid @RequestBody OwnSignInDto dto) {
        return service.signIn(dto);
    }

    /**
     * Method for verifying users email.
     *
     * @param token - {@link String} this is token (hash) to verify user.
     * @return {@link ResponseEntity}
     */
    @GetMapping("/verifyEmail")
    public ResponseEntity verify(@RequestParam @NotBlank String token) throws URISyntaxException {
        verifyEmailService.verifyByToken(token);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(clientAddress));
        return new ResponseEntity(responseHeaders, HttpStatus.SEE_OTHER);
    }

    /**
     * Method for refresh access token.
     *
     * @param refreshToken - {@link String} this is refresh token.
     * @return {@link ResponseEntity} - with new access token.
     */
    @PostMapping("/updateAccessToken")
    public ResponseEntity updateAccessToken(@RequestBody @NotBlank String refreshToken) {
        return ResponseEntity.ok().body(service.updateAccessToken(refreshToken));
    }
}
