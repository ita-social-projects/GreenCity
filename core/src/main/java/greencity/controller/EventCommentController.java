package greencity.controller;

import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/events/comments")
public class EventCommentController {
    private final EventCommentService eventCommentService;

    /**
     * Method for creating {@link greencity.dto.eventcomment.EventCommentVO}.
     *
     * @param eventId id of {@link greencity.dto.event.EventVO} to add comment to.
     * @param request - dto for {@link greencity.dto.eventcomment.EventCommentVO}
     *                entity.
     * @return dto {@link greencity.dto.eventcomment.AddEventCommentDtoResponse}
     */
    @ApiOperation(value = "Add comment.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddEventCommentDtoRequest.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
    })
    @PostMapping("{eventId}")
    public ResponseEntity<AddEventCommentDtoResponse> save(@PathVariable Long eventId,
        @Valid @RequestBody AddEventCommentDtoRequest request,
        @ApiIgnore @CurrentUser UserVO user) {
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
    public ResponseEntity<EventCommentDto> getEventCommentById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(eventCommentService.getEventCommentById(id));
    }

    /**
     * Method to count all active comments for certain
     * {@link greencity.dto.event.EventVO}.
     *
     * @param eventId to specify {@link greencity.dto.event.EventVO}
     * @return amount of all active comments for certain
     *         {@link greencity.dto.event.EventVO}
     */
    @ApiOperation(value = "Count comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
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
    @ApiOperation(value = "Get all active comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/active")
    @ApiPageableWithoutSort
    public ResponseEntity<PageableDto<EventCommentDto>> getAllActiveComments(@ApiIgnore Pageable pageable,
        Long eventId,
        @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(eventCommentService.getAllActiveComments(pageable, user, eventId));
    }

    /**
     * Method to update certain {@link greencity.dto.eventcomment.EventCommentVO} specified by id.
     *
     * @param id of {@link greencity.dto.eventcomment.EventCommentVO} to update
     * @param commentText edited text of {@link greencity.dto.eventcomment.EventCommentVO}
     */
    @ApiOperation(value = "Update comment.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping()
    public void update(Long id, @RequestParam @NotBlank String commentText, @ApiIgnore @CurrentUser UserVO user) {
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
    @ApiOperation(value = "Delete event comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{eventCommentId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventCommentId, @ApiIgnore @CurrentUser UserVO user) {
        eventCommentService.delete(eventCommentId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
