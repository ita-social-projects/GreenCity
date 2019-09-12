package greencity.security.controller;

import greencity.security.dto.SuccessSignInDto;
import greencity.security.service.GoogleSecurityService;
import javax.validation.Valid;
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

    /**
     * Method that provide authenticate with google token.
     *
     * @param idToken {@link String} - google idToken
     * @return {@link SuccessSignInDto}
     */
    @PostMapping
    public SuccessSignInDto authenticate(@Valid @RequestBody String idToken) {
        return service.authenticate(idToken);
    }
}
