package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.entity.enums.UserStatus;
import greencity.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
     * Parameter principal are ignored because Spring automatically provide the Principal object.
     *
     * @param userStatusDto - dto with updated filed.
     * @return {@link UserStatusDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Update status of user")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserStatus.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("status")
    public ResponseEntity<UserStatusDto> updateStatus(
        @Valid @RequestBody UserStatusDto userStatusDto, @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateStatus(
                    userStatusDto.getId(), userStatusDto.getUserStatus(), principal.getName()));
    }

    /**
     * The method which update user role.
     * Parameter principal are ignored because Spring automatically provide the Principal object.
     *
     * @param userRoleDto - dto with updated field.
     * @return {@link UserRoleDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Update role of user")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserRoleDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("role")
    public ResponseEntity<UserRoleDto> updateRole(
        @Valid @RequestBody UserRoleDto userRoleDto, @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateRole(
                    userRoleDto.getId(), userRoleDto.getRole(), principal.getName()));
    }

    /**
     * The method which return list of users by page.
     * Parameter pageable ignored because swagger ui shows the wrong params,
     * instead they are explained in the {@link ApiPageable}.
     *
     * @param pageable - pageable configuration.
     * @return list of {@link PageableDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Get users by page")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PageableDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @GetMapping
    public ResponseEntity<PageableDto> getAllUsers(@ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByPage(pageable));
    }

    /**
     * The method which return array of existing roles.
     *
     * @return {@link RoleDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Get all available roles")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = RoleDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("roles")
    public ResponseEntity<RoleDto> getRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getRoles());
    }

    /**
     * The method which return list of users by filter.
     * Parameter pageable ignored because swagger ui shows the wrong params,
     * instead they are explained in the {@link ApiPageable}.
     *
     * @param filterUserDto dto which contains fields with filter criteria.
     * @param pageable      - pageable configuration.
     * @return {@link PageableDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Filter all user by search criteria")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PageableDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @PostMapping("filter")
    public ResponseEntity<PageableDto> getUsersByFilter(
        @ApiIgnore Pageable pageable, @RequestBody FilterUserDto filterUserDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsersByFilter(filterUserDto, pageable));
    }
}
