package greencity.controller;

import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.service.UserService;
import java.security.Principal;
import javax.validation.Valid;
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

    /** Generated javadoc, must be replaced with real one. */
    @PatchMapping("update/status")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UserStatusDto userStatusDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        userService.updateStatus(
                                userStatusDto.getId(), userStatusDto.getUserStatus()));
    }

    @PatchMapping("update/role")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UserRoleDto userRoleDto) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateRole(
                    userRoleDto.getId(), userRoleDto.getRole()));
    }

    /** Generated javadoc, must be replaced with real one. */
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
