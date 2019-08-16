package greencity.controller;

import greencity.service.VerifyEmailService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verifyEmail")
@AllArgsConstructor
public class VerifyEmailController {

    private VerifyEmailService service;

    @GetMapping
    public void verify(@RequestParam String token) {
        service.verify(token);
    }
}
