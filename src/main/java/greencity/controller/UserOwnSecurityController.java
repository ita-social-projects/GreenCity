package greencity.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.service.UserOwnSecurityService;
import greencity.service.VerifyEmailService;
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

    public UserOwnSecurityController(
            UserOwnSecurityService service, VerifyEmailService verifyEmailService) {
        this.service = service;
        this.verifyEmailService = verifyEmailService;
    }

    @PostMapping
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterDto dto) {
        service.register(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/verifyEmail")
    public void verify(@RequestParam String token, HttpServletResponse response)
            throws IOException {
        verifyEmailService.verify(token);
        response.sendRedirect(clientAddress);
    }
}
