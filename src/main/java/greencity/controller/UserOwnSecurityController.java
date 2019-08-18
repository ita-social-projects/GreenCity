package greencity.controller;

import javax.validation.Valid;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.service.UserOwnSecurityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ownSecurity")
@AllArgsConstructor
public class UserOwnSecurityController {

    private UserOwnSecurityService service;

    @PostMapping
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterDto dto) {
        service.register(dto);
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }
}
