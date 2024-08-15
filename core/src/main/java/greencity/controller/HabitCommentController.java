package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.CommentVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
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
    @Operation(summary = "Add comment.")
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
}
