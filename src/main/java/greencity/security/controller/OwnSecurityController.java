package greencity.security.controller;

import static greencity.constant.ErrorMessage.*;

import greencity.constant.HttpStatuses;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.OwnRestoreDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.RestoreLogicService;
import greencity.security.service.VerifyEmailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that provide our sign-up and sign-in logic.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@RestController
@RequestMapping("/ownSecurity")
@Validated
public class OwnSecurityController {
    @Value("${client.address}")
    private String clientAddress;
    private OwnSecurityService service;
    private VerifyEmailService verifyEmailService;
    private RestoreLogicService restoreLogicService;

    /**
     * Constructor.
     *
     * @param service            - {@link OwnSecurityService} - service for security logic.
     * @param verifyEmailService {@link VerifyEmailService} - service for verify email logic.
     */
    public OwnSecurityController(
        OwnSecurityService service, VerifyEmailService verifyEmailService, RestoreLogicService restoreLogicService) {
        this.service = service;
        this.verifyEmailService = verifyEmailService;
        this.restoreLogicService = restoreLogicService;
    }

    /**
     * Method for sign-up by our security logic.
     *
     * @param dto - {@link OwnSignUpDto} that have sign-up information.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Sign-up by own security logic")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = USER_ALREADY_REGISTERED_WITH_THIS_EMAIL)
    })
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
    @ApiOperation("Sign-in by own security logic")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = SuccessSignInDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
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
    @ApiOperation("Verify email by email token (hash that contains link for verification)")
    @ApiResponses(value = {
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN)
    })
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
    @ApiOperation("Updating access token by refresh token")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = REFRESH_TOKEN_NOT_VALID)
    })
    @GetMapping("/updateAccessToken")
    public ResponseEntity updateAccessToken(@RequestParam @NotBlank String refreshToken) {
        return ResponseEntity.ok().body(service.updateAccessToken(refreshToken));
    }


    /**
     * Method for restoring password and sending email for restore.
     *
     * @param email - {@link String}
     * @return - {@link ResponseEntity }
     * @author Dmytro Dovhal
     */
    @GetMapping("/restorePassword")
    public ResponseEntity restore(@RequestParam @Email String email) {
        restoreLogicService.sendEmailForRestore(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for changing password.
     *
     * @param form - {@link OwnRestoreDto}
     * @return - {@link ResponseEntity}
     * @author Dmytro Dovhal
     */
    @PostMapping("/changePassword")
    public ResponseEntity changePassword(@Valid @RequestBody OwnRestoreDto form) {
        restoreLogicService.restoreByToken(form.getToken(), form.getPassword());
        return ResponseEntity.ok().build();
    }
}
