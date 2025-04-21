package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.category.CategoryDto;
import greencity.dto.location.UserLocationDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Method to find {@link UserCityDto} by user id.
     *
     * @param userId id of the user
     * @return {@link UserCityDto}.
     */
    @Operation(summary = "View a list of user cities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/{id}/cities")
    public ResponseEntity<UserCityDto> findAllUsersCities(@PathVariable(name = "id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllUsersCities(userId));
    }

    /**
     * Method to find {@link UserLocationDto} by user id.
     *
     * @param userId id of the user
     * @return {@link UserLocationDto}.
     */
    @Operation(summary = "View user location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/{id}/location")
    public ResponseEntity<UserLocationDto> findUserLocationByUserId(@PathVariable(name = "id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUserLocationDtoByUserId(userId));
    }

    /**
     * Method to update user location by user id.
     *
     * @param userId id of the user
     * @param userProfileDtoRequest contains location data
     */
    @Operation(summary = "Update user location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @PatchMapping("/{id}/location")
    public ResponseEntity<Void> setLocationForUser(
            @PathVariable(name = "id") Long userId,
            @RequestBody UserProfileDtoRequest userProfileDtoRequest
    ) {
        userService.setLocationForUser(userId, userProfileDtoRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
