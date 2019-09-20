package greencity.controller;

import greencity.entity.enums.EntityType;
import greencity.service.PlaceCommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PlaceCommentController {
    /**
     * Autowired CommentService instance.
     */
    private PlaceCommentService placeCommentService;

    @GetMapping("/place/{placeId}")
    public ResponseEntity getAllByPlaceId(@PathVariable Long placeId) {
        return ResponseEntity.status(HttpStatus.OK).body(placeCommentService.findAllByPlaceId(placeId));
    }

//    @GetMapping("comments/{id}")
//    public ResponseEntity getCommentById(@PathVariable Long id) {
//        return ResponseEntity.status(HttpStatus.OK)
//            .body(placeCommentService.);
//    }
}
