package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.security.Principal;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
     * @author Rostyslav Khasanov
     */
    @PatchMapping("status")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UserStatusDto userStatusDto, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateStatus(
                    userStatusDto.getId(), userStatusDto.getUserStatus(), principal.getName()));
    }

    /**
     * The method which update user role.
     *
     * @param userRoleDto - dto with updated filed.
     * @return {@code UserRoleDto}
     * @author Rostyslav Khasanov
     */
    @PatchMapping("role")
    public ResponseEntity<?> updateRole(@Valid @RequestBody UserRoleDto userRoleDto, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateRole(
                    userRoleDto.getId(), userRoleDto.getRole(), principal.getName()));
    }

    /**
     * The method which return list of users by page.
     *
     * @param pageable - pageable configuration.
     * @return list of {@code UserPageableDto}
     * @author Rostyslav Khasanov
     */
    @GetMapping
    @ApiPageable
    public ResponseEntity<?> getAllUsers(
        @ApiIgnore("Ignored because swagger ui shows the wrong params, "
        + "instead they are explained in the @ApiPageable") Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByPage(pageable));
    }

    /**
     * The method which return array of existing roles.
     *
     * @return array of roles
     * @author Rostyslav Khasanov
     */
    @GetMapping("roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getRoles());
    }

    /**
     * The method which return list of users by filter.
     *
     * @param filterUserDto dto which contains fields with filter criteria.
     * @param pageable      - pageable configuration.
     * @return list of {@code UserPageableDto}
     * @author Rostyslav Khasanov
     */
    @PostMapping("filter")
    @ApiPageable
    public ResponseEntity<?> getByReg(
        @ApiIgnore("Ignored because swagger ui shows the wrong params, "
        + "instead they are explained in the @ApiPageable")
            Pageable pageable, @RequestBody FilterUserDto filterUserDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsersByFilter(filterUserDto, pageable));
    }
}
