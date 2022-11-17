package greencity.webcontroller;

import greencity.annotations.ImageValidation;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventViewDto;
import greencity.enums.TagType;
import greencity.service.EventService;
import greencity.service.TagsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;
import java.time.ZoneId;

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

    @ResponseBody
    @PostMapping(value = "/create")
    public AddEventDtoRequest postEvent(@RequestBody @Valid AddEventDtoRequest addEventDtoRequest,
        @ImageValidation MultipartFile[] files,
        Principal principal) {

        eventService.save(addEventDtoRequest, principal.getName(), files);
        return addEventDtoRequest;
    }
}
