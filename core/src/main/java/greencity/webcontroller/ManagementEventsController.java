package greencity.webcontroller;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventViewDto;
import greencity.enums.TagType;
import greencity.service.EventService;
import greencity.service.TagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
     * Method that perform soft or hard delete of the event.
     * If no attenders are signed to an event then hard delete, if they are, then soft delete.
     *
     * @return View template path {@link String}.
     */
    @PostMapping("/delete")
    public String deleteEvent(@RequestParam Long id) {
        eventService.disableEvent(id);
        return "redirect:/management/events";
    }
}
