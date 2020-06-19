package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.comment.CommentReturnDto;
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

    @ApiOperation(value = "Add comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = CommentReturnDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("{econewsId}")
    public ResponseEntity<Object> save(@PathVariable Long econewsId,
                                       @Valid @RequestBody AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest,
                                       @ApiIgnore @AuthenticationPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getName());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ecoNewsCommentService.save(econewsId, addEcoNewsCommentDtoRequest, user));
    }

    @ApiOperation(value = "Get all comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
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

    @ApiOperation(value = "Count comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/count/comments")
    public int getCountOfComments(Long id) {
        return ecoNewsService.findById(id).getEcoNewsComments().size();
    }

    @ApiOperation(value = "Get all replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
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

    @ApiOperation(value = "Get count of replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("count/replies")
    public int getCountOfReplies(Long parentCommentId) {
        return ecoNewsCommentService.countReplies(parentCommentId);
    }

    /**
     * Method that delete comment by id.
     *
     * @param id comment id
     */
    @ApiOperation(value = "Delete comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("")
    public ResponseEntity<Object> delete(Long id, @ApiIgnore @AuthenticationPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getName());
        ecoNewsCommentService.deleteById(id, user);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Update comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("")
    public void update(Long id, String text, @ApiIgnore @AuthenticationPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getName());
        ecoNewsCommentService.update(text, id, user);
    }

    @ApiOperation(value = "Like comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("like")
    public void like(Long id, @ApiIgnore @AuthenticationPrincipal Principal principal) {
        User user = userService.findByEmail(principal.getName());
        ecoNewsCommentService.like(id, user);
    }

    @ApiOperation(value = "Count comment likes.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/count/likes")
    public int getCountOfLikes(Long id) {
        return ecoNewsCommentService.countLikes(id);
    }
}
