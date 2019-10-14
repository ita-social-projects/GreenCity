package greencity.controller;

import greencity.constant.ErrorMessage;
import greencity.dto.comment.AddCommentDto;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.UserStatus;
import greencity.exception.NotFoundException;
import greencity.exception.UserBlockedException;
import greencity.service.PlaceCommentService;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
        System.out.println(addCommentDto);
        User user = userService.findByEmail(principal.getName())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + principal.getName()));
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
    public ResponseEntity getCommentById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(placeCommentService.findById(id));
    }
}
