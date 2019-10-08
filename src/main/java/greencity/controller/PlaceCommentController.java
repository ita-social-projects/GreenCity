package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.ErrorMessage;
import greencity.dto.comment.AddCommentDto;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.UserStatus;
import greencity.exception.NotFoundException;
import greencity.service.PlaceCommentService;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/place/{placeId}/comments")
    public ResponseEntity save(@PathVariable Long placeId,
                               @Valid @RequestBody AddCommentDto addCommentDto, Principal principal) {
        User user = userService.findByEmail(principal.getName())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + principal.getName()));
        if (user.getUserStatus().equals(UserStatus.BLOCKED)) {
            throw new NotFoundException(" ");
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
    public ResponseEntity getCommentById(@PathVariable Long id) {
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
    @GetMapping("comments")
    public ResponseEntity getAllComments(@ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(placeCommentService.getAllComments(pageable));
    }

    /**
     * Method that delete comment by id.
     *
     * @param id comment id
     * @author Rostyslav Khasanov
     */
    @DeleteMapping
    public ResponseEntity delete(Long id) {
        placeService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
