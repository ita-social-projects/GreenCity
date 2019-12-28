package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.ErrorMessage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.UserStatus;
import greencity.exception.exceptions.BadEmailException;
import greencity.exception.exceptions.UserBlockedException;
import greencity.service.PlaceCommentService;
import greencity.service.PlaceService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@RestController
@AllArgsConstructor
public class PlaceCommentController {
    /**
     * Autowired CommentService instance.
     */
    private PlaceCommentService placeCommentService;
    private UserService userService;
    private PlaceService placeService;


    /**
     * Method witch save comment by Place Id.
     *
     * @param placeId       Id of place to witch related comment.
     * @param addCommentDto DTO with contain data od Comment.
     * @return CommentDTO
     */
    @ApiOperation(value = "Add comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = CommentReturnDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/place/{placeId}/comments")
    public ResponseEntity<Object> save(@PathVariable Long placeId,
                               @Valid @RequestBody AddCommentDto addCommentDto,
                               @ApiIgnore @AuthenticationPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getName())
            .orElseThrow(() -> new BadEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + principal.getName()));
        if (user.getUserStatus().equals(UserStatus.BLOCKED)) {
            throw new UserBlockedException(ErrorMessage.USER_HAS_BLOCKED_STATUS);
        }
        Place place = placeService.findById(placeId);
        return ResponseEntity
            .status(HttpStatus.CREATED).body(placeCommentService.save(place.getId(), addCommentDto, user.getEmail()));
    }

    /**
     * Method return comment by id.
     *
     * @param id Comment id
     * @return CommentDto
     * @author Marian Milian
     */
    @GetMapping("comments/{id}")
    public ResponseEntity<Object> getCommentById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(placeCommentService.findById(id));
    }

    /**
     * Method return comment by id.
     * Parameter pageable ignored because swagger ui shows the wrong params,
     * instead they are explained in the {@link ApiPageable}.
     *
     * @param pageable pageable configuration
     * @return PageableDto
     * @author Rostyslav Khasanov
     */
    @ApiPageable
    @ApiOperation(value = "Get comments by page")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PageableDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("comments")
    public ResponseEntity<Object> getAllComments(@ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(placeCommentService.getAllComments(pageable));
    }

    /**
     * Method that delete comment by id.
     *
     * @param id comment id
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Delete comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("comments")
    public ResponseEntity<Object> delete(Long id) {
        placeCommentService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
