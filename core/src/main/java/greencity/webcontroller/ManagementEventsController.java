package greencity.webcontroller;

import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventViewDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.enums.TagType;
import greencity.service.EventService;
import greencity.service.TagsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management/events")
public class ManagementEventsController {
    private final EventService eventService;
    private final TagsService tagsService;

    /**
     * Method that returns management page with all {@link EventDto}.
     *
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     */
    @GetMapping
    public String getAllEvents(@RequestParam(required = false, name = "query") String query, Model model,
        @ApiIgnore Pageable pageable, EventViewDto eventViewDto) {
        PageableAdvancedDto<EventDto> allEvents;
        if (!eventViewDto.isEmpty()) {
            allEvents = eventService.getAll(pageable, null);
            model.addAttribute("fields", eventViewDto);
            model.addAttribute("query", "");
        } else {
            allEvents = query == null || query.isEmpty()
                ? eventService.getAll(pageable, null)
                : eventService.searchEventsBy(pageable, query);
            model.addAttribute("fields", new EventViewDto());
            model.addAttribute("query", query);
        }

        model.addAttribute("pageable", allEvents);
        Sort sort = pageable.getSort();
        StringBuilder orderUrl = new StringBuilder("");
        if (!sort.isEmpty()) {
            for (Sort.Order order : sort) {
                orderUrl.append(orderUrl + order.getProperty() + "," + order.getDirection());
            }
            model.addAttribute("sortModel", orderUrl);
        }
        model.addAttribute("eventsTag", tagsService.findByTypeAndLanguageCode(TagType.EVENT, "en"));
        model.addAttribute("pageSize", pageable.getPageSize());
        return "core/management_events";
    }

    /**
     * Method for creating {@link EventDto}.
     *
     * @param addEventDtoRequest dto for {@link EventDto} entity.
     * @param files              of {@link MultipartFile []}
     * @return {@link GenericResponseDto} with of operation and errors fields.
     */
    @ApiOperation( "Save Event.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HttpStatuses.CREATED),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto postEvent(@RequestBody @Valid AddEventDtoRequest addEventDtoRequest,
                              @Valid @RequestPart @Nullable MultipartFile[] files,
                              @ApiIgnore Principal principal) {

        return eventService.save(addEventDtoRequest, principal.getName(), files);
    }
}
