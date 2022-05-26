package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.ValidEventDtoRequest;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;
import greencity.dto.event.EventDto;
import greencity.service.EventService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventsController {
    private final EventService eventService;

    /**
     * Method for creating an event.
     *
     * @return {@link AddEventDtoResponse} instance.
     * @author Max Bohonko.
     */
    @ApiOperation(value = "Create new event")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @PostMapping(value = "/create",
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<EventDto> save(
        @ApiParam(value = SwaggerExampleModel.ADD_EVENT,
            required = true) @ValidEventDtoRequest @RequestPart AddEventDtoRequest addEventDtoRequest,
        @ApiIgnore Principal principal,
        @RequestPart(required = false) @Nullable MultipartFile[] images) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(eventService.save(addEventDtoRequest, principal.getName(), images));
    }

    /**
     * Method for deleting an event.
     *
     * @author Max Bohonko.
     */
    @ApiOperation(value = "Delete event")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventId, @ApiIgnore Principal principal) {
        eventService.delete(eventId, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for getting the event by event id.
     *
     * @return {@link EventDto} instance.
     * @author Max Bohonko.
     */
    @ApiOperation(value = "Get the event")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEvent(eventId));
    }

    /**
     * Method for getting pages of events.
     *
     * @return a page of {@link EventDto} instance.
     * @author Max Bohonko.
     */
    @ApiOperation(value = "Get all events")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @ApiPageable
    @GetMapping
    public ResponseEntity<PageableAdvancedDto<EventDto>> getEvent(@ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAll(pageable));
    }

    /**
     * Method for adding an attender to the event.
     *
     * @author Max Bohonko.
     */
    @ApiOperation(value = "Add an attender to the event")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/addAttender/{eventId}")
    public void addAttender(@PathVariable Long eventId, @ApiIgnore Principal principal) {
        eventService.addAttender(eventId, principal.getName());
    }

    /**
     * Method for removing an attender from the event.
     *
     * @author Max Bohonko.
     */
    @ApiOperation(value = "Remove an attender from the event")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @DeleteMapping("/removeAttender/{eventId}")
    public ResponseEntity<Object> removeAttender(@PathVariable Long eventId, @ApiIgnore Principal principal) {
        eventService.removeAttender(eventId, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
