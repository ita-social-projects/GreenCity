package greencity.security.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.own_security.OwnSignInDto;
import greencity.security.dto.own_security.OwnSignUpDto;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.VerifyEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ownSecurity")
public class OwnSecurityController {

    @Value("${client.address}")
    private String clientAddress;

    private OwnSecurityService service;
    private VerifyEmailService verifyEmailService;

    public OwnSecurityController(
        OwnSecurityService service, VerifyEmailService verifyEmailService) {
        this.service = service;
        this.verifyEmailService = verifyEmailService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<String> singUp(@Valid @RequestBody OwnSignUpDto dto) {
        service.register(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/signIn")
    public SuccessSignInDto singIn(@Valid @RequestBody OwnSignInDto dto) {
        return service.signIn(dto);
    }

    @GetMapping("/verifyEmail")
    public void verify(@RequestParam @NotBlank String token, HttpServletResponse response)
            throws IOException {
        verifyEmailService.verify(token);
        response.sendRedirect(clientAddress);
    }

    @PostMapping("/updateAccessToken")
    public String updateAccessToken(@RequestBody @NotBlank String refreshToken) {
        return service.updateAccessToken(refreshToken);
    }
}
