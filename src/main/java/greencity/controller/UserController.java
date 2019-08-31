package greencity.controller;

import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.service.UserService;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    /**
     * Generated javadoc, must be replaced with real one.
     */
    @PatchMapping("update/role")
    public ResponseEntity<?> updateRole(
        @RequestParam("id") Long id, @RequestParam("role") ROLE role) {
        userService.updateRole(id, role);
        return ResponseEntity.ok().build();
    }

    /**
     * Generated javadoc, must be replaced with real one.
     */
    @PatchMapping("update/status")
    public ResponseEntity<?> updateUserStatus(
        @RequestParam("id") Long id, @RequestParam UserStatus status) {
        userService.updateUserStatus(id, status);
        return ResponseEntity.ok().build();
    }

    /**
     * Generated javadoc, must be replaced with real one.
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByPage(pageable));
    }

    /**
     * The method which return the role of user who sends request.
     *
     * @param principal - principal of user.
     * @return role of user.
     * @author Nazar Vladyka
     */
    @GetMapping("/role")
    public ResponseEntity getRole(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getRole(principal.getName()));
    }
}
