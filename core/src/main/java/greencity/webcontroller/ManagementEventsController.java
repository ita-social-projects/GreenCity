package greencity.webcontroller;

import greencity.client.RestClient;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventViewDto;
import greencity.enums.TagType;
import greencity.service.EventService;
import greencity.service.TagsService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management/events")
public class ManagementEventsController {
    private final EventService eventService;
    private final TagsService tagsService;
    private final RestClient restClient;

    @Value("${google.maps.api.key}")
    private String googleMapApiKey;

    @Value("${address}")
    private String backendAddress;

    /**
     * Method that returns management page with all {@link EventDto}.
     *
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     */
    @GetMapping
    public String getAllEvents(@RequestParam(required = false, name = "query") String query, Model model,
                               @Parameter(hidden = true) Pageable pageable, EventViewDto eventViewDto) {
        PageableAdvancedDto<EventDto> allEvents;
        if (eventViewDto.getId() != null && !eventViewDto.isEmpty()) {
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
        StringBuilder orderUrl = new StringBuilder();
        if (!sort.isEmpty()) {
            for (Sort.Order order : sort) {
                orderUrl.append(orderUrl).append(order.getProperty()).append(",").append(order.getDirection());
            }
            model.addAttribute("sortModel", orderUrl);
        }
        model.addAttribute("eventsTag", tagsService.findByTypeAndLanguageCode(TagType.EVENT, "en"));
        model.addAttribute("pageSize", pageable.getPageSize());
        model.addAttribute("backendAddress", backendAddress);
        return "core/management_events";
    }

    @GetMapping("/create-event")
    public String getEventCreatePage(Model model, Principal principal) {
        model.addAttribute("addEventDtoRequest", new AddEventDtoRequest());
        model.addAttribute("images", new MultipartFile[]{});
        model.addAttribute("backendAddress", backendAddress);
        model.addAttribute("author", restClient.findByEmail(principal.getName()).getName());
        model.addAttribute("googleMapApiKey", googleMapApiKey);
        return "core/management_create_event";
    }

    @PostMapping
    public String createEvent(@RequestPart("addEventDtoRequest") AddEventDtoRequest addEventDtoRequest,
                              @RequestPart("images") MultipartFile[] images,
                              Principal principal,
                              Model model) {
        model.addAttribute("addEventDtoRequest", new AddEventDtoRequest());
        model.addAttribute("images", new MultipartFile[]{});
        eventService.save(addEventDtoRequest, principal.getName(), images);
        return "redirect:/management/events";
    }

    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody List<Long> ids, Principal principal) {
        String email = principal.getName();
        for(Long id :ids){
            eventService.delete(id, email);
        }
        return ResponseEntity.ok().build();
    }
}
