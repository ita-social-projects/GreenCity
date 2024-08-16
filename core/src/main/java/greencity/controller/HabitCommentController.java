package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.CommentDto;
import greencity.dto.comment.CommentVO;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.user.UserVO;
import greencity.enums.ArticleType;
import greencity.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habits")
public class HabitCommentController {
    private final CommentService commentService;

    /**
     * Method for creating {@link CommentVO}.
     *
     * @param habitId id of {@link HabitVO} to add comment to.
     * @param request dto for {@link CommentVO} entity.
     * @return dto {@link AddCommentDtoResponse}
     */
    @Operation(summary = "Add comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = AddCommentDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @PostMapping("/{habitId}/comments")
    public ResponseEntity<AddCommentDtoResponse> save(@PathVariable Long habitId,
                                                           @Valid @RequestBody AddCommentDtoRequest request,
                                                           @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        // todo: SECURITY CONFIG
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.save(ArticleType.HABIT, habitId, request, userVO));
    }

    /**
     * Method to get certain comment for {@link HabitVO} specified by commentId.
     *
     * @param id specifies {@link CommentDto} to which we search for comments
     * @return comment to certain habit specified by commentId.
     */
    @Operation(summary = "Get comment by id")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = AddCommentDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id,
                                                          @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.findCommentById(id, userVO));
    }

    /**
     * Method to get all active replies to {@link CommentDto} specified by
     * parent comment id.
     *
     * @param parentCommentId id of parent comment {@link CommentDto}
     * @param userVO            {@link UserVO} user who want to get replies.
     * @return Pageable of {@link CommentDto}
     */
    @Operation(description = "Get all active replies to comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("comments/{parentCommentId}/replies/active")
    @ApiPageable
    public ResponseEntity<PageableDto<CommentDto>> findAllActiveReplies(
            @Parameter(hidden = true) Pageable pageable,
            @PathVariable Long parentCommentId,
            @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(commentService.findAllActiveReplies(pageable, parentCommentId, userVO));
    }

    /**
     * Method to count all active comments for certain
     * {@link HabitVO}.
     *
     * @param habitId to specify {@link HabitVO}
     * @return amount of all active comments for certain
     *         {@link HabitVO}
     */
    @Operation(summary = "Count comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),

    })
    @GetMapping("/{habitId}/comments/count")
    public int getCountOfComments(@PathVariable Long habitId) {
        return commentService.countComments(ArticleType.HABIT, habitId);
    }

    /**
     * Method to count all active replies for {@link CommentDto} comment.
     *
     * @param parentCommentId to specify {@link CommentDto}
     * @return amount of all active comments for certain {@link CommentDto}
     */
    @Operation(summary = "Count replies for comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/comments/replies/active/count/{parentCommentId}")
    public int getCountOfActiveReplies(@PathVariable Long parentCommentId) {
        return commentService.countAllActiveReplies(parentCommentId);
    }

    /**
     * Method to get all active comments to {@link HabitVO}
     * specified by habitId.
     *
     * @param habitId id of {@link HabitVO}
     * @return Pageable of {@link CommentDto}
     */
    @Operation(summary = "Get all active comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("comments/active")
    @ApiPageableWithoutSort
    public ResponseEntity<PageableDto<CommentDto>> getAllActiveComments(
            @Parameter(hidden = true) Pageable pageable,
            Long habitId,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getAllActiveComments(pageable, user, habitId, ArticleType.HABIT));
    }
}
