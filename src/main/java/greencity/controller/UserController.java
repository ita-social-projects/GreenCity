package greencity.controller;

import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.entity.enums.ROLE;
import greencity.service.UserService;
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

    /**
     * The method which update user status.
     *
     * @param userStatusDto - dto with updated filed.
     * @return {@code UserStatusDto}
     * @author Rostyslav Khasnaov
     */
    @PatchMapping("update/status")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UserStatusDto userStatusDto) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateStatus(
                    userStatusDto.getId(), userStatusDto.getUserStatus()));
    }

    /**
     * The method which update user role.
     *
     * @param userRoleDto - dto with updated filed.
     * @return {@code UserRoleDto}
     * @author Rostyslav Khasnaov
     */
    @PatchMapping("update/role")
    public ResponseEntity<?> updateRole(@Valid @RequestBody UserRoleDto userRoleDto) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateRole(
                    userRoleDto.getId(), userRoleDto.getRole()));
    }

    /**
     * The method which return list of users by page.
     *
     * @param pageable - pageable configuration.
     * @return list of {@code UserPageableDto}
     * @author Rostyslav Khasnaov
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByPage(pageable));
    }

    /**
     * The method which return array of existing roles.
     *
     * @return array of roles
     * @author Rostyslav Khasnaov
     */
    @GetMapping("roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getRoles());
    }
}
