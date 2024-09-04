package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.user.UserSearchDto;
import greencity.dto.user.UserVO;
import greencity.enums.CommentStatus;
import greencity.service.EcoNewsCommentService;
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
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/eco-news/{ecoNewsId}/comments")
public class EcoNewsCommentController {
    private final EcoNewsCommentService ecoNewsCommentService;

    /**
     * Method for creating {@link EcoNewsCommentVO}.
     *
     * @param ecoNewsId id of {@link EcoNewsVO} to add comment to.
     * @param request   - dto for {@link EcoNewsCommentVO} entity.
     * @return dto {@link AddEcoNewsCommentDtoResponse}
     */
    @Operation(summary = "Add comment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
            content = @Content(schema = @Schema(implementation = AddEcoNewsCommentDtoResponse.class))),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @PostMapping
    public ResponseEntity<AddEcoNewsCommentDtoResponse> save(@PathVariable Long ecoNewsId,
        @Valid @RequestBody AddEcoNewsCommentDtoRequest request,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ecoNewsCommentService.save(ecoNewsId, request, user));
    }

    /**
     * Method to count not deleted comments to certain {@link EcoNewsVO}.
     *
     * @param ecoNewsId to specify {@link EcoNewsVO}
     * @return amount of comments
     */
    @Operation(summary = "Count comments.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @GetMapping("/count")
    public int getCountOfComments(@PathVariable Long ecoNewsId) {
        return ecoNewsCommentService.countOfComments(ecoNewsId);
    }

    /**
     * Method to get all replies to {@link EcoNewsCommentVO} specified by
     * parentCommentId.
     *
     * @param parentCommentId specifies parent comment to all replies
     * @return Pageable of {@link EcoNewsCommentDto} replies
     */
    @Operation(summary = "Get all replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @ApiPageable
    @GetMapping("/{parentCommentId}/replies")
    public ResponseEntity<PageableDto<EcoNewsCommentDto>> findAllReplies(
        @Parameter(hidden = true) Pageable pageable,
        @PathVariable Long parentCommentId,
        @PathVariable Long ecoNewsId,
        @RequestParam(required = false) List<CommentStatus> statuses,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ecoNewsCommentService.findAllReplies(pageable, ecoNewsId, parentCommentId, statuses, user));
    }

    /**
     * Method to count replies to certain {@link EcoNewsCommentVO}.
     *
     * @param parentCommentId specifies parent comment to all replies
     * @return amount of replies
     */
    @Operation(summary = "Get count of replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @GetMapping("/{parentCommentId}/replies/count")
    public int getCountOfReplies(@PathVariable Long parentCommentId, @PathVariable Long ecoNewsId) {
        return ecoNewsCommentService.countReplies(ecoNewsId, parentCommentId);
    }

    /**
     * Method to mark comment as deleted.
     *
     * @param commentId comment id
     */
    @Operation(summary = "Mark comment as deleted.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Object> delete(
        @PathVariable Long commentId,
        @Parameter(hidden = true) @CurrentUser UserVO user,
        @PathVariable Long ecoNewsId) {
        ecoNewsCommentService.deleteById(ecoNewsId, commentId, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to update certain {@link EcoNewsCommentVO} specified by id.
     *
     * @param commentId   of {@link EcoNewsCommentVO} to update
     * @param commentText new text of {@link EcoNewsCommentVO}
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
    public void update(
        @PathVariable Long commentId,
        @RequestBody @Valid @Size(min = 1, max = 8000) String commentText,
        @Parameter(hidden = true) @CurrentUser UserVO user,
        @PathVariable Long ecoNewsId) {
        ecoNewsCommentService.update(ecoNewsId, commentText, commentId, user);
    }

    /**
     * Method to like/dislike certain {@link EcoNewsCommentVO} specified by id.
     *
     * @param commentId of {@link EcoNewsCommentVO} to like/dislike
     */
    @Operation(summary = "Like comment.")
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
    public void like(
        @PathVariable Long commentId,
        @Parameter(hidden = true) @CurrentUser UserVO user,
        @PathVariable Long ecoNewsId) {
        ecoNewsCommentService.like(ecoNewsId, commentId, user);
    }

    /**
     * Method to like/dislike comment and count likes.
     *
     * @param amountCommentLikesDto dto with id and count likes for comments.
     */
    @MessageMapping("/likeAndCount")
    public void getCountOfLike(@Payload AmountCommentLikesDto amountCommentLikesDto) {
        ecoNewsCommentService.countLikes(amountCommentLikesDto);
    }

    /**
     * Method to get comments to {@link EcoNewsVO} specified by ecoNewsId and status
     * of comment.
     *
     * @param ecoNewsId id of {@link EcoNewsVO}
     * @return Pageable of {@link EcoNewsCommentDto}
     * @author Taras Dovganyuk
     */
    @Operation(summary = "Get all comments.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST)))
    })
    @ApiPageableWithoutSort
    @GetMapping
    public ResponseEntity<PageableDto<EcoNewsCommentDto>> getAllComments(
        @Parameter(hidden = true) Pageable pageable,
        @PathVariable Long ecoNewsId,
        @RequestParam(required = false) List<CommentStatus> statuses,
        @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ecoNewsCommentService.findAllComments(pageable, user, ecoNewsId, statuses));
    }

    /**
     * Method for getting all users available to tag in comment by search query.
     *
     * @param searchUsers dto with current user ID and search query
     *                    {@link UserSearchDto}.
     * @author Anton Bondar
     */
    @MessageMapping("/getUsersToTagInComment")
    public void getUsersToTagInComment(@Payload UserSearchDto searchUsers) {
        ecoNewsCommentService.searchUsers(searchUsers);
    }
}
