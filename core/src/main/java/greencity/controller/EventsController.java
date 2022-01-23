package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;
import greencity.service.EventService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventsController {

    private final EventService eventService;
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/create")
    public ResponseEntity<AddEventDtoResponse> save(@RequestBody AddEventDtoRequest addEventDtoRequest, @ApiIgnore Principal principal){
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.save(addEventDtoRequest, principal.getName()));
    }

    @PostMapping("/addAttender/{eventId}")
    public void save(@PathVariable Long eventId, @ApiIgnore Principal principal){
        eventService.addAttender(eventId, principal.getName());
    }
}
