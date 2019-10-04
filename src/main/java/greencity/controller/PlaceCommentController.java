package greencity.controller;

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

@RestController
@AllArgsConstructor
public class PlaceCommentController {
    /**
     * Autowired CommentService instance.
     */
    private PlaceCommentService placeCommentService;
    private UserService userService;
    private PlaceService placeService;

    @GetMapping("/place/{placeId}/comments")
    public ResponseEntity getAllByPlaceId(@PathVariable Long placeId, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(placeCommentService.findAllByPlaceId(placeId, pageable));
    }

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

    @GetMapping("comments/{id}")
    public ResponseEntity getCommentById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(placeCommentService.findById(id));
    }
}
