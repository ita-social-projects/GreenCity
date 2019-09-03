package greencity.controller;

import greencity.dto.userownsecurity.UserRegisterDto;
import greencity.dto.userownsecurity.UserSignInDto;
import greencity.dto.userownsecurity.UserSuccessSignInDto;
import greencity.service.UserOwnSecurityService;
import greencity.service.VerifyEmailService;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ownSecurity")
public class UserOwnSecurityController {
    @Value("${client.address}")
    private String clientAddress;

    private UserOwnSecurityService service;
    private VerifyEmailService verifyEmailService;

    /**
     * Generated javadoc, must be replaced with real one.
     */
    public UserOwnSecurityController(
        UserOwnSecurityService service, VerifyEmailService verifyEmailService) {
        this.service = service;
        this.verifyEmailService = verifyEmailService;
    }

    /**
     * Generated javadoc, must be replaced with real one.
     */
    @PostMapping("/signUp")
    public ResponseEntity<String> singUp(@Valid @RequestBody UserRegisterDto dto) {
        service.register(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Generated javadoc, must be replaced with real one.
     */
    @PostMapping("/signIn")
    public UserSuccessSignInDto singIn(@Valid @RequestBody UserSignInDto dto) {
        return service.signIn(dto);
    }

    /**
     * Generated javadoc, must be replaced with real one.
     */
    @GetMapping("/verifyEmail")
    public ResponseEntity verify(@RequestParam @NotBlank String token)
            throws IOException {
        verifyEmailService.verify(token);
        return new ResponseEntity(HttpStatus.FOUND);
    }

    /**
     * Generated javadoc, must be replaced with real one.
     */
    @PostMapping("/updateAccessToken")
    public String updateAccessToken(@RequestBody @NotBlank String refreshToken) {
        return service.updateAccessToken(refreshToken);
    }
}
