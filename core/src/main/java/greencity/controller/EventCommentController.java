package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/events/comments")
public class EventCommentController {
    private final EventCommentService eventCommentService;

    /**
     * Method for creating {@link EventCommentVO}.
     *
     * @param eventId id of {@link EventVO} to add comment to.
     * @param request dto for {@link EventCommentVO} entity.
     * @return dto {@link AddEventCommentDtoResponse}
     */
    @Operation(summary = "Add comment.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
            content = @Content(schema = @Schema(implementation = AddEventCommentDtoResponse.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @PostMapping("/{eventId}")
    public ResponseEntity<AddEventCommentDtoResponse> save(@PathVariable Long eventId,
        @Valid @RequestBody AddEventCommentDtoRequest request,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(eventCommentService.save(eventId, request, user));
    }

    /**
     * Method to get certain comment to {@link EventVO} specified by commentId.
     *
     * @param id specifies {@link EventCommentDto} to which we search for comments
     * @return comment to certain event specified by commentId.
     */
    @GetMapping("{id}")
    public ResponseEntity<EventCommentDto> getEventCommentById(@PathVariable Long id,
        @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(eventCommentService.getEventCommentById(id, userVO));
    }

    /**
     * Method to count all active comments for certain
     * {@link greencity.dto.event.EventVO}.
     *
     * @param eventId to specify {@link greencity.dto.event.EventVO}
     * @return amount of all active comments for certain
     *         {@link greencity.dto.event.EventVO}
     */
    @Operation(summary = "Count comments.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @GetMapping("/count/{eventId}")
    public int getCountOfComments(@PathVariable Long eventId) {
        return eventCommentService.countComments(eventId);
    }

    /**
     * Method to get all active comments to {@link greencity.dto.event.EventVO}
     * specified by eventId.
     *
     * @param eventId id of {@link greencity.dto.event.EventVO}
     * @return Pageable of {@link greencity.dto.eventcomment.EventCommentDto}
     */
    @Operation(summary = "Get all active comments.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST)))
    })
    @GetMapping("/active")
    @ApiPageableWithoutSort
    public ResponseEntity<PageableDto<EventCommentDto>> getAllActiveComments(
        @Parameter(hidden = true) Pageable pageable,
        Long eventId,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(eventCommentService.getAllActiveComments(pageable, user, eventId));
    }

    /**
     * Method to update certain {@link greencity.dto.eventcomment.EventCommentVO}
     * specified by id.
     *
     * @param id          of {@link greencity.dto.eventcomment.EventCommentVO} to
     *                    update
     * @param commentText edited text of
     *                    {@link greencity.dto.eventcomment.EventCommentVO}
     */
    @Operation(summary = "Update comment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PatchMapping()
    public void update(@RequestParam Long id,
        @RequestBody @Valid @Size(min = 1, max = 8000) String commentText,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.update(commentText, id, user);
    }

    /**
     * Method for deleting {@link greencity.dto.eventcomment.EventCommentVO} by its
     * id.
     *
     * @param eventCommentId {@link greencity.dto.eventcomment.EventCommentVO} id
     *                       which will be deleted.
     * @return id of deleted {@link greencity.dto.eventcomment.EventCommentVO}.
     * @author Oleh Vatulaik.
     */
    @Operation(summary = "Mark comment as deleted.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @DeleteMapping("/{eventCommentId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventCommentId,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.delete(eventCommentId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to get all active replies to {@link EventCommentDto} specified by
     * parent comment id.
     *
     * @param parentCommentId id of parent comment {@link EventCommentDto}
     * @param user            {@link UserVO} user who want to get replies.
     * @return Pageable of {@link EventCommentDto}
     */
    @Operation(description = "Get all active replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("replies/active/{parentCommentId}")
    @ApiPageable
    public ResponseEntity<PageableDto<EventCommentDto>> findAllActiveReplies(
        @Parameter(hidden = true) Pageable pageable,
        @PathVariable Long parentCommentId,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(eventCommentService.findAllActiveReplies(pageable, parentCommentId, user));
    }

    /**
     * Method to count all active replies for {@link EventCommentDto} comment.
     *
     * @param parentCommentId to specify {@link EventCommentDto}
     * @return amount of all active comments for certain {@link EventCommentDto}
     */
    @Operation(summary = "Count replies for comment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/replies/active/count/{parentCommentId}")
    public int getCountOfActiveReplies(@PathVariable Long parentCommentId) {
        return eventCommentService.countAllActiveReplies(parentCommentId);
    }

    /**
     * Method to like/dislike certain {@link EventCommentDto} comment specified by
     * id.
     *
     * @param commentId of {@link EventCommentDto} to like/dislike
     */
    @Operation(summary = "Like/dislike comment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PostMapping("/like")
    public void like(@RequestParam("commentId") Long commentId, @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.like(commentId, user);
    }

    /**
     * Method to count likes for comment.
     *
     * @param commentId id of {@link EventCommentDto} comment whose likes must be
     *                  counted
     * @param user      {@link UserVO} user who want to get amount of likes for
     *                  comment.
     * @return amountCommentLikesDto dto with id and count likes for comments.
     */
    @Operation(summary = "Count likes for comment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/likes/count/{commentId}")
    public ResponseEntity<AmountCommentLikesDto> countLikes(@PathVariable("commentId") Long commentId,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(eventCommentService.countLikes(commentId, user));
    }

    /**
     * Method to like/dislike comment and count likes.
     *
     * @param amountCommentLikesDto dto with id and count likes for comments.
     */
    @MessageMapping("/eventCommentLikeAndCount")
    public void likeAndCount(@Payload AmountCommentLikesDto amountCommentLikesDto) {
        eventCommentService.eventCommentLikeAndCount(amountCommentLikesDto);
    }
}
