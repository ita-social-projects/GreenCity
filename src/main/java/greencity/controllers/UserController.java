package greencity.controllers;

import greencity.entities.User;
import greencity.services.UserService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping
    public ResponseEntity<?> saveUser(@Valid @RequestBody User user) {
        userService.save(user);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PostMapping("update/role")
    public ResponseEntity<?> makeUserToModerator(
        @RequestParam("id") Long id
    ) {
        userService.makeUserToModerator(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
