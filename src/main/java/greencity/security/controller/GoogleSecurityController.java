package greencity.security.controller;

import javax.validation.Valid;

import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.google_security.GoogleSecurityDto;
import greencity.security.service.GoogleSecurityService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/googleSecurity")
@AllArgsConstructor
public class GoogleSecurityController {

    private GoogleSecurityService service;

    @PostMapping
    public SuccessSignInDto authenticate(@Valid @RequestBody GoogleSecurityDto dto) {
        return service.authenticate(dto);
    }
}
