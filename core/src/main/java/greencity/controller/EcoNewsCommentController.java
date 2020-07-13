package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.entity.User;
import greencity.service.EcoNewsCommentService;
import greencity.service.EcoNewsService;
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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/econews/comments")
public class EcoNewsCommentController {
    private final EcoNewsCommentService ecoNewsCommentService;
    private final UserService userService;
    private final EcoNewsService ecoNewsService;

    /**
     * Method for creating {@link greencity.entity.EcoNewsComment}.
     *
     * @param econewsId id of {@link greencity.entity.EcoNews} to add comment to.
     * @param request   - dto for {@link greencity.entity.EcoNewsComment} entity.
     * @return dto {@link greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse}
     */
    @ApiOperation(value = "Add comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddEcoNewsCommentDtoResponse.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @PostMapping("{econewsId}")
    public ResponseEntity<AddEcoNewsCommentDtoResponse> save(@PathVariable Long econewsId,
                                                             @Valid @RequestBody AddEcoNewsCommentDtoRequest request,
                                                             @ApiIgnore @AuthenticationPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getName());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ecoNewsCommentService.save(econewsId, request, user));
    }

    /**
     * Method to get all comments to {@link greencity.entity.EcoNews} specified by ecoNewsId.
     *
     * @param ecoNewsId id of {@link greencity.entity.EcoNews}
     * @return Pageable of {@link EcoNewsCommentDto}
     */
    @ApiOperation(value = "Get all comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("")
    @ApiPageable
    public ResponseEntity<PageableDto<EcoNewsCommentDto>> findAll(@ApiIgnore Pageable pageable,
                                                                  Long ecoNewsId,
                                                                  @ApiIgnore @AuthenticationPrincipal
                                                                      Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName());
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ecoNewsCommentService.findAllComments(pageable, user, ecoNewsId));
    }

    /**
     * Method to count comments to certain {@link greencity.entity.EcoNews}.
     *
     * @param id to specify {@link greencity.entity.EcoNews}
     * @return amount of comments
     */
    @ApiOperation(value = "Count comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/count/comments")
    public int getCountOfComments(Long id) {
        return ecoNewsService.findById(id).getEcoNewsComments().size();
    }

    /**
     * Method to get all replies to {@link greencity.entity.EcoNewsComment} specified by parentCommentId.
     *
     * @param parentCommentId specifies parent comment to all replies
     * @return list of {@link EcoNewsCommentDto} replies
     */
    @ApiOperation(value = "Get all replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK)
    })
    @GetMapping("replies/{parentCommentId}")
    public ResponseEntity<List<EcoNewsCommentDto>> findAllReplies(@PathVariable Long parentCommentId,
                                                                  @ApiIgnore @AuthenticationPrincipal
                                                                      Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName());
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ecoNewsCommentService.findAllReplies(parentCommentId, user));
    }

    /**
     * Method to count replies to certain {@link greencity.entity.EcoNewsComment}.
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
        return ecoNewsCommentService.countReplies(parentCommentId);
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
        ecoNewsCommentService.deleteById(id, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to update certain {@link greencity.entity.EcoNewsComment} specified by id.
     *
     * @param id   of {@link greencity.entity.EcoNewsComment} to update
     * @param text new text of {@link greencity.entity.EcoNewsComment}
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
        ecoNewsCommentService.update(text, id, user);
    }

    /**
     * Method to like/dislike certain {@link greencity.entity.EcoNewsComment} specified by id.
     *
     * @param id of {@link greencity.entity.EcoNewsComment} to like/dislike
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
        ecoNewsCommentService.like(id, user);
    }

    /**
     * Method to count likes to certain {@link greencity.entity.EcoNewsComment}.
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
        return ecoNewsCommentService.countLikes(id);
    }

    /**
     * Method to like/dislike comment and count likes.
     *
     * @param id specifies comment
     * @return amount of likes
     */
    @MessageMapping("/likeAndCount")
    @SendTo("/topic/comment")
    public int greeting(Long id, @ApiIgnore @AuthenticationPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getName());
        ecoNewsCommentService.like(id, user);
        return ecoNewsCommentService.countLikes(id);
    }
}
