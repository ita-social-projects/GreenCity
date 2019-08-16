package greencity.controller;

import greencity.entity.enums.ROLE;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping("update/role")
    public ResponseEntity<?> makeUserToModerator(
            @RequestParam("id") Long id, @RequestParam("role") ROLE role) {
        userService.updateRole(id, role);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("block")
    public ResponseEntity<?> blockUser(
        @RequestParam("id") Long id
    ) {
        userService.blockIUser(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
