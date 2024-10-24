package greencity.webcontroller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ValidEventDtoRequest;
import greencity.client.RestClient;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventRequestDto;
import greencity.dto.filter.FilterEventDto;
import greencity.enums.TagType;
import greencity.service.EventService;
import greencity.service.TagsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import static greencity.constant.SwaggerExampleModel.UPDATE_EVENT;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management/events")
public class ManagementEventController {
    public static final String BACKEND_ADDRESS_ATTRIBUTE = "backendAddress";
    private final EventService eventService;
    private final TagsService tagsService;
    private final RestClient restClient;
    private final ModelMapper getModelMapper;

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
    @ApiLocale
    public String getAllEvents(@RequestParam(required = false, name = "query") String query,
        Model model,
        @Parameter(hidden = true) Pageable pageable,
        FilterEventDto filterEventDto,
        @Parameter(hidden = true) Locale locale) {
        PageableAdvancedDto<EventDto> allEvents;
        if (query != null && !query.isEmpty()) {
            allEvents = eventService.searchEventsBy(pageable, query);
        } else {
            allEvents = eventService.getEventsManagement(pageable, filterEventDto, null);
        }
        model.addAttribute("pageable", allEvents);
        Sort sort = pageable.getSort();
        StringBuilder orderUrl = new StringBuilder();
        if (!sort.isEmpty()) {
            for (Sort.Order order : sort) {
                if (!orderUrl.isEmpty()) {
                    orderUrl.append("&");
                }
                orderUrl.append("sort=").append(order.getProperty()).append(",").append(order.getDirection().name());
            }
        }
        model.addAttribute("filterEventDto", filterEventDto);
        model.addAttribute("sortModel", orderUrl.toString());
        model.addAttribute("eventsTag", tagsService.findByTypeAndLanguageCode(TagType.EVENT, locale.getLanguage()));
        model.addAttribute("pageSize", pageable.getPageSize());
        model.addAttribute(BACKEND_ADDRESS_ATTRIBUTE, backendAddress);
        model.addAttribute("cities",
            eventService.getAllEventsAddresses().stream()
                .map(e -> "en".equals(locale.getLanguage()) ? e.getCityEn() : e.getCityUa())
                .distinct()
                .toList());

        return "core/management_events";
    }

    @GetMapping("/create-event")
    public String getEventCreatePage(Model model, Principal principal) {
        model.addAttribute("addEventDtoRequest", new AddEventDtoRequest());
        model.addAttribute("images", new MultipartFile[] {});
        model.addAttribute(BACKEND_ADDRESS_ATTRIBUTE, backendAddress);
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
        model.addAttribute("images", new MultipartFile[] {});
        eventService.save(addEventDtoRequest, principal.getName(), images);
        return "redirect:/management/events";
    }

    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody List<Long> ids, Principal principal) {
        String email = principal.getName();
        for (Long id : ids) {
            eventService.delete(id, email);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = EventDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PutMapping(
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventDto> update(
        @Parameter(required = true,
            description = UPDATE_EVENT) @ValidEventDtoRequest @RequestPart UpdateEventRequestDto eventDto,
        @Parameter(hidden = true) Principal principal,
        @RequestPart(required = false) @Nullable MultipartFile[] images) {
        return ResponseEntity.status(HttpStatus.OK).body(
            eventService.update(eventDto, principal.getName(), images));
    }

    @GetMapping("/edit/{id}")
    public String editEvent(@PathVariable("id") Long id, Model model, Principal principal) {
        model.addAttribute(BACKEND_ADDRESS_ATTRIBUTE, backendAddress);
        model.addAttribute("author", restClient.findByEmail(principal.getName()).getName());
        model.addAttribute("eventDto", eventService.getEvent(id, principal));
        model.addAttribute("googleMapApiKey", googleMapApiKey);
        return "core/management_edit_event";
    }
}
