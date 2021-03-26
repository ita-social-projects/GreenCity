package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtricks.TipsAndTricksVO;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentDto;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO;
import greencity.dto.user.UserVO;
import greencity.service.TipsAndTricksCommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/tipsandtricks/comments")
public class TipsAndTricksCommentController {
    private TipsAndTricksCommentService tipsAndTricksCommentService;

    /**
     * Method for creating {@link TipsAndTricksCommentVO}.
     *
     * @param tipsAndTricksId id of {@link TipsAndTricksVO} to add comment to.
     * @param request         - dto for {@link TipsAndTricksCommentVO} entity.
     * @return dto {@link AddTipsAndTricksCommentDtoResponse}
     */
    @ApiOperation(value = "Add comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddTipsAndTricksCommentDtoResponse.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("{tipsAndTricksId}")
    public ResponseEntity<AddTipsAndTricksCommentDtoResponse> save(@PathVariable Long tipsAndTricksId,
                                                                   @Valid @RequestBody
                                                                       AddTipsAndTricksCommentDtoRequest request,
                                                                   @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(tipsAndTricksCommentService.save(tipsAndTricksId, request, userVO));
    }

    /**
     * Method to get all comments to {@link TipsAndTricksVO} specified by
     * tipsAndTricksId.
     *
     * @param tipsAndTricksId id of {@link TipsAndTricksVO}
     * @return Pageable of {@link TipsAndTricksCommentDto}
     */
    @ApiOperation(value = "Get all comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @GetMapping()
    @ApiPageable
    public ResponseEntity<PageableDto<TipsAndTricksCommentDto>> findAll(@ApiIgnore Pageable pageable,
                                                                        Long tipsAndTricksId,
                                                                        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(tipsAndTricksCommentService.findAllComments(pageable, userVO, tipsAndTricksId));
    }

    /**
     * Method to count comments to certain {@link TipsAndTricksVO}.
     *
     * @param id to specify {@link TipsAndTricksVO}
     * @return amount of comments
     */
    @ApiOperation(value = "Count comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @GetMapping("/count/comments")
    public int getCountOfComments(Long id) {
        return tipsAndTricksCommentService.countComments(id);
    }

    /**
     * Method to get all replies to {@link TipsAndTricksCommentVO} specified by
     * parentCommentId.
     *
     * @param parentCommentId specifies parent comment to all replies
     * @return list of {@link TipsAndTricksCommentDto} replies
     */
    @ApiOperation(value = "Get all replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @GetMapping("replies/{parentCommentId}")
    public ResponseEntity<List<TipsAndTricksCommentDto>> findAllReplies(@PathVariable Long parentCommentId) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(tipsAndTricksCommentService.findAllReplies(parentCommentId));
    }

    /**
     * Method to mark comment as deleted.
     *
     * @param id comment id
     */
    @ApiOperation(value = "Mark comment as deleted.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @DeleteMapping()
    public ResponseEntity<Object> delete(Long id, @ApiIgnore @CurrentUser UserVO userVO) {
        tipsAndTricksCommentService.deleteById(id, userVO);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to update certain {@link TipsAndTricksCommentVO} specified by id.
     *
     * @param id   of {@link TipsAndTricksCommentVO} to update
     * @param text new text of {@link TipsAndTricksCommentVO}
     */
    @ApiOperation(value = "Update comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PatchMapping()
    public void update(Long id, String text, @ApiIgnore @CurrentUser UserVO userVO) {
        tipsAndTricksCommentService.update(text, id, userVO);
    }

    /**
     * Method to like/dislike certain {@link TipsAndTricksCommentVO} specified by
     * id.
     *
     * @param id of {@link TipsAndTricksCommentVO} to like/dislike
     */
    @ApiOperation(value = "Like comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("like")
    public void like(Long id, @ApiIgnore @CurrentUser UserVO userVO) {
        tipsAndTricksCommentService.like(id, userVO);
    }

    /**
     * Method to count likes to certain {@link TipsAndTricksCommentVO}.
     *
     * @param id specifies comment
     * @return amount of likes
     */
    @ApiOperation(value = "Count comment likes.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @GetMapping("/count/likes")
    public int getCountOfLikes(Long id) {
        return tipsAndTricksCommentService.countLikes(id);
    }

    /**
     * Method to count replies to certain {@link TipsAndTricksCommentVO}.
     *
     * @param parentCommentId specifies parent comment to all replies
     * @return amount of replies
     */
    @ApiOperation(value = "Get count of replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @GetMapping("count/replies")
    public int getCountOfReplies(Long parentCommentId) {
        return tipsAndTricksCommentService.countReplies(parentCommentId);
    }
}
