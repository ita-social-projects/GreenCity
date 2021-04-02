package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.service.PlaceCommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@AllArgsConstructor
public class PlaceCommentController {
    /**
     * Autowired CommentService instance.
     */
    private PlaceCommentService placeCommentService;

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
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
    })
    @PostMapping("/place/{placeId}/comments")
    public ResponseEntity<Object> save(@PathVariable Long placeId,
        @Valid @RequestBody AddCommentDto addCommentDto,
        @ApiIgnore @AuthenticationPrincipal Principal principal) {
        return ResponseEntity
            .status(HttpStatus.CREATED).body(placeCommentService.save(placeId, addCommentDto, principal.getName()));
    }

    /**
     * Method return comment by id.
     *
     * @param id Comment id
     * @return CommentDto
     * @author Marian Milian
     */
    @ApiOperation(value = "Get comment by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = CommentReturnDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)

    })
    @GetMapping("comments/{id}")
    public ResponseEntity<Object> getCommentById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(placeCommentService.findById(id));
    }

    /**
     * Method return comment by id. Parameter pageable ignored because swagger ui
     * shows the wrong params, instead they are explained in the
     * {@link ApiPageable}.
     *
     * @param pageable pageable configuration
     * @return PageableDto
     * @author Rostyslav Khasanov
     */
    @ApiPageable
    @ApiOperation(value = "Get comments by page")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PageableDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 500, message = HttpStatuses.INTERNAL_SERVER_ERROR)
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
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("comments")
    public ResponseEntity<Object> delete(Long id) {
        placeCommentService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
