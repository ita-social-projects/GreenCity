package greencity.security.controller;

import greencity.constant.HttpStatuses;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.SuccessSignUpDto;
import greencity.security.dto.ownsecurity.OwnRestoreDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.PasswordRecoveryService;
import greencity.security.service.VerifyEmailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static greencity.constant.ErrorMessage.*;
import static greencity.constant.ValidationConstants.USER_CREATED;

/**
 * Controller that provides our sign-up and sign-in logic.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@RestController
@RequestMapping("/ownSecurity")
@Validated
@Slf4j
public class OwnSecurityController {
    private final OwnSecurityService service;
    private final VerifyEmailService verifyEmailService;
    private final PasswordRecoveryService passwordRecoveryService;

    /**
     * Constructor.
     *
     * @param service            - {@link OwnSecurityService} - service for security logic.
     * @param verifyEmailService {@link VerifyEmailService} - service for email verification.
     */
    @Autowired
    public OwnSecurityController(OwnSecurityService service,
                                 VerifyEmailService verifyEmailService,
                                 PasswordRecoveryService passwordRecoveryService) {
        this.service = service;
        this.verifyEmailService = verifyEmailService;
        this.passwordRecoveryService = passwordRecoveryService;
    }

    /**
     * Method for sign-up by our security logic.
     *
     * @param dto - {@link OwnSignUpDto} that have sign-up information.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Sign-up by own security logic")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = USER_CREATED, response = SuccessSignUpDto.class),
        @ApiResponse(code = 400, message = USER_ALREADY_REGISTERED_WITH_THIS_EMAIL)
    })
    @PostMapping("/signUp")
    public ResponseEntity<SuccessSignUpDto> singUp(@Valid @RequestBody OwnSignUpDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.signUp(dto));
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
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN)
    })
    @GetMapping("/verifyEmail")
    public ResponseEntity<Object> verify(@RequestParam @NotBlank String token,
                                         @RequestParam("user_id") Long userId) {
        verifyEmailService.verifyByToken(userId, token);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Object> updateAccessToken(@RequestParam @NotBlank String refreshToken) {
        return ResponseEntity.ok().body(service.updateAccessTokens(refreshToken));
    }

    /**
     * Method for restoring password and sending email for restore.
     *
     * @param email - {@link String}
     * @return - {@link ResponseEntity}
     * @author Dmytro Dovhal
     */
    @ApiOperation("Sending email for restore password.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = USER_NOT_FOUND_BY_EMAIL)
    })
    @GetMapping("/restorePassword")
    public ResponseEntity<Object> restore(@RequestParam @Email String email) {
        passwordRecoveryService.sendPasswordRecoveryEmailTo(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for changing password.
     *
     * @param form - {@link OwnRestoreDto}
     * @return - {@link ResponseEntity}
     * @author Dmytro Dovhal
     */
    @ApiOperation("Updating password for restore password option.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = TOKEN_FOR_RESTORE_IS_INVALID)
    })
    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody OwnRestoreDto form) {
        passwordRecoveryService.updatePasswordUsingToken(form.getToken(), form.getPassword());
        return ResponseEntity.ok().build();
    }

    /**
     * Method for updating current password.
     *
     * @param updateDto - {@link UpdatePasswordDto}
     * @return - {@link ResponseEntity}
     * @author Dmytro Dovhal
     */
    @ApiOperation("Updating current password.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = PASSWORD_DOES_NOT_MATCH)
    })
    @PutMapping
    public ResponseEntity<Object> updatePassword(@Valid @RequestBody UpdatePasswordDto updateDto,
                                                 @ApiIgnore @AuthenticationPrincipal Principal principal) {
        String email = principal.getName();
        service.updateCurrentPassword(updateDto, email);
        return ResponseEntity.ok().build();
    }
}
