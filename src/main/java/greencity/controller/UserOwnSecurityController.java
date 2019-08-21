package greencity.controller;

import javax.validation.Valid;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.service.UserOwnSecurityService;
import greencity.service.VerifyEmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ownSecurity")
@AllArgsConstructor
public class UserOwnSecurityController {

    private UserOwnSecurityService service;
    private VerifyEmailService verifyEmailService;

    @PostMapping
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterDto dto) {
        service.register(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/verifyEmail")
    public void verify(@RequestParam String token) {
        verifyEmailService.verify(token);
    }
}
