package greencity.controller;

import greencity.dto.comment.CommentDto;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.exception.NotFoundException;
import greencity.service.PlaceCommentService;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.security.Principal;
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
    public ResponseEntity save(@PathVariable Long placeId, @RequestBody CommentDto commentDto, Principal principal) {
        User user = userService.findByEmail(principal.getName())
            .orElseThrow(() -> new NotFoundException("" + principal.getName()));//todo add message
        Place place = placeService.findByIdOptional(placeId)
            .orElseThrow(() -> new NotFoundException("" + placeId));//todo add message
        return ResponseEntity
            .status(HttpStatus.OK).body(placeCommentService.save(place.getId(), commentDto, user.getEmail()));
    }

    @GetMapping("comments/{id}")
    public ResponseEntity getCommentById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(placeCommentService.findById(id).orElseThrow(() -> new NotFoundException("" + id)));
    }
}
