package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentDto;
import greencity.entity.User;
import greencity.service.TipsAndTricksCommentService;
import greencity.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/tipsandtricks/comments")
public class TipsAndTricksCommentController {
    private UserService userService;
    private TipsAndTricksCommentService tipsAndTricksCommentService;

    /**
     * Method for creating {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param tipsAndTricksId id of {@link greencity.entity.TipsAndTricks} to add comment to.
     * @param request         - dto for {@link greencity.entity.TipsAndTricksComment} entity.
     * @return dto {@link greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse}
     */
    @ApiOperation(value = "Add comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddTipsAndTricksCommentDtoResponse.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @PostMapping("{tipsAndTricksId}")
    public ResponseEntity<AddTipsAndTricksCommentDtoResponse> save(@PathVariable Long tipsAndTricksId,
                                                                   @Valid @RequestBody
                                                                       AddTipsAndTricksCommentDtoRequest request,
                                                                   @ApiIgnore @AuthenticationPrincipal
                                                                       Principal principal) {
        User user = userService.findByEmail(principal.getName());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(tipsAndTricksCommentService.save(tipsAndTricksId, request, user));
    }

    /**
     * Method to get all comments to {@link greencity.entity.TipsAndTricks} specified by tipsAndTricksId.
     *
     * @param tipsAndTricksId id of {@link greencity.entity.TipsAndTricks}
     * @return Pageable of {@link TipsAndTricksCommentDto}
     */
    @ApiOperation(value = "Get all comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("")
    @ApiPageable
    public ResponseEntity<PageableDto<TipsAndTricksCommentDto>> findAll(@ApiIgnore Pageable pageable,
                                                                        Long tipsAndTricksId,
                                                                        @ApiIgnore @AuthenticationPrincipal
                                                                            Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName());
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(tipsAndTricksCommentService.findAllComments(pageable, user, tipsAndTricksId));
    }

    /**
     * Method to count comments to certain {@link greencity.entity.TipsAndTricks}.
     *
     * @param id to specify {@link greencity.entity.TipsAndTricks}
     * @return amount of comments
     */
    @ApiOperation(value = "Count comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/count/comments")
    public int getCountOfComments(Long id) {
        return tipsAndTricksCommentService.countComments(id);
    }

    /**
     * Method to get all replies to {@link greencity.entity.TipsAndTricksComment} specified by parentCommentId.
     *
     * @param parentCommentId specifies parent comment to all replies
     * @return list of {@link TipsAndTricksCommentDto} replies
     */
    @ApiOperation(value = "Get all replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK)
    })
    @GetMapping("replies/{parentCommentId}")
    public ResponseEntity<List<TipsAndTricksCommentDto>> findAllReplies(@PathVariable Long parentCommentId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(tipsAndTricksCommentService.findAllReplies(parentCommentId));
    }

    /**
     * Method to mark comment as deleted.
     *
     * @param id comment id
     */
    @ApiOperation(value = "Mark comment as deleted.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @DeleteMapping("")
    public ResponseEntity<Object> delete(Long id, @ApiIgnore @AuthenticationPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getName());
        tipsAndTricksCommentService.deleteById(id, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to update certain {@link greencity.entity.TipsAndTricksComment} specified by id.
     *
     * @param id   of {@link greencity.entity.TipsAndTricksComment} to update
     * @param text new text of {@link greencity.entity.TipsAndTricksComment}
     */
    @ApiOperation(value = "Update comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @PatchMapping("")
    public void update(Long id, String text, @ApiIgnore @AuthenticationPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getName());
        tipsAndTricksCommentService.update(text, id, user);
    }

    /**
     * Method to like/dislike certain {@link greencity.entity.TipsAndTricksComment} specified by id.
     *
     * @param id of {@link greencity.entity.TipsAndTricksComment} to like/dislike
     */
    @ApiOperation(value = "Like comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("like")
    public void like(Long id, @ApiIgnore @AuthenticationPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getName());
        tipsAndTricksCommentService.like(id, user);
    }

    /**
     * Method to count likes to certain {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param id specifies comment
     * @return amount of likes
     */
    @ApiOperation(value = "Count comment likes.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/count/likes")
    public int getCountOfLikes(Long id) {
        return tipsAndTricksCommentService.countLikes(id);
    }

    /**
     * Method to count replies to certain {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param parentCommentId specifies parent comment to all replies
     * @return amount of replies
     */
    @ApiOperation(value = "Get count of replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("count/replies")
    public int getCountOfReplies(Long parentCommentId) {
        return tipsAndTricksCommentService.countReplies(parentCommentId);
    }
}