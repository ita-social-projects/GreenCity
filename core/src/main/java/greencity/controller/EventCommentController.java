package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;
import greencity.enums.CommentStatus;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Locale;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/events/{eventId}/comments")
public class EventCommentController {
    private final EventCommentService eventCommentService;

    /**
     * Method for creating {@link EventCommentVO}.
     *
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
    @PostMapping
    public ResponseEntity<AddEventCommentDtoResponse> save(
        @PathVariable Long eventId,
        @Valid @RequestBody AddEventCommentDtoRequest request,
        @Parameter(hidden = true) @CurrentUser UserVO user,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventCommentService.save(eventId, request, user, locale));
    }

    /**
     * Method to get certain comment to {@link EventVO} specified by commentId.
     *
     * @param commentId specifies {@link EventCommentDto} to which we search for
     *                  comments
     * @return comment to certain event specified by commentId.
     */
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<EventCommentDto> getEventCommentById(
        @PathVariable Long eventId,
        @PathVariable Long commentId,
        @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity.ok().body(eventCommentService.getEventCommentById(eventId, commentId, userVO));
    }

    /**
     * Method to count all active comments for certain
     * {@link greencity.dto.event.EventVO}.
     *
     * @return amount of all active comments for certain
     *         {@link greencity.dto.event.EventVO}
     */
    @Operation(summary = "Count comments.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @GetMapping("/count")
    public int getCountOfComments(@PathVariable Long eventId) {
        return eventCommentService.countComments(eventId);
    }

    /**
     * Method to get all comments to {@link greencity.dto.event.EventVO}
     * specified by eventId.
     *
     * @return Pageable of {@link greencity.dto.eventcomment.EventCommentDto}
     */
    @Operation(summary = "Get all comments.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST)))
    })
    @GetMapping
    @ApiPageableWithoutSort
    public ResponseEntity<PageableDto<EventCommentDto>> getAllComments(
        @Parameter(hidden = true) Pageable pageable,
        @PathVariable Long eventId,
        @RequestParam List<CommentStatus> statuses,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.ok().body(eventCommentService.getAllComments(pageable, user, eventId, statuses));
    }

    /**
     * Method to update certain {@link greencity.dto.eventcomment.EventCommentVO}
     * specified by eventId.
     *
     * @param commentId   comment if to update
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
    @PutMapping("/{commentId}")
    public ResponseEntity<Object> update(
        @PathVariable Long eventId,
        @PathVariable Long commentId,
        @RequestBody @Valid @Size(min = 1, max = 8000) String commentText,
        @Parameter(hidden = true) @CurrentUser UserVO user,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        eventCommentService.update(commentText, eventId, commentId, user, locale);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for deleting {@link greencity.dto.eventcomment.EventCommentVO} by its
     * id.
     *
     * @param commentId {@link greencity.dto.eventcomment.EventCommentVO} id which
     *                  will be deleted.
     * @return id of deleted {@link greencity.dto.eventcomment.EventCommentVO}.
     */
    @Operation(summary = "Mark comment as deleted.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Object> delete(
        @PathVariable Long eventId,
        @PathVariable Long commentId,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.delete(eventId, commentId, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to get all replies to {@link EventCommentDto} specified by
     * parent comment id.
     *
     * @param parentCommentId id of parent comment {@link EventCommentDto}
     * @param user            {@link UserVO} user who want to get replies.
     * @param statuses statuses of comments.
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
    @ApiPageable
    @GetMapping("/{parentCommentId}/replies")
    public ResponseEntity<PageableDto<EventCommentDto>> findAllReplies(
        @Parameter(hidden = true) Pageable pageable,
        @PathVariable Long eventId,
        @PathVariable Long parentCommentId,
        @RequestParam List<CommentStatus> statuses,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.ok()
            .body(eventCommentService.findAllReplies(pageable, eventId, parentCommentId, statuses, user));
    }

    /**
     * Method to count all replies for {@link EventCommentDto} comment.
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
    @GetMapping("/{parentCommentId}/replies/count")
    public int getCountOfActiveReplies(@PathVariable Long parentCommentId, @PathVariable Long eventId) {
        return eventCommentService.countAllActiveReplies(eventId, parentCommentId);
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
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<Object> like(
        @PathVariable Long commentId,
        @PathVariable Long eventId,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.like(eventId, commentId, user);
        return ResponseEntity.ok().build();
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
    @GetMapping("/{commentId}/likes/count")
    public ResponseEntity<AmountCommentLikesDto> countLikes(
        @PathVariable Long commentId,
        @PathVariable Long eventId,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.ok().body(eventCommentService.countLikes(eventId, commentId, user));
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
