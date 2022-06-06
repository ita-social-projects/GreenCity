package greencity.webcontroller;

import greencity.annotations.CurrentUser;
import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.*;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventViewDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.tag.TagDto;
import greencity.dto.user.UserVO;
import greencity.enums.TagType;
import greencity.service.EcoNewsService;
import greencity.service.EventService;
import greencity.service.TagsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management/events")
public class ManagementEventsController {
    private final EcoNewsService ecoNewsService;
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
            allEvents = eventService.getAll(pageable);
            model.addAttribute("fields", eventViewDto);
            model.addAttribute("query", "");
        } else {
            allEvents = query == null || query.isEmpty()
                ? eventService.getAll(pageable)
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
     * Method which deteles {@link EventDto} by given id.
     *
     * @param id of Event
     * @return {@link ResponseEntity}
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id, @ApiIgnore @CurrentUser UserVO user) {
        ecoNewsService.delete(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    /**
     * Method for deleting {@link EventDto} by given list of Ids.
     *
     * @param listId list of IDs.
     * @return {@link ResponseEntity}
     */
    @DeleteMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        ecoNewsService.deleteAll(listId);
        return ResponseEntity.status(HttpStatus.OK).body(listId);
    }

    /**
     * Method for getting econews by id.
     *
     * @param id of Eco New
     * @return {@link EcoNewsDto} instance.
     */
    @GetMapping("/find")
    public ResponseEntity<EventDto> getEventsById(@RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEvent(id));
    }

    /**
     * Method for getting all econews tag.
     *
     * @return {@link TagDto} instance.
     */
    @GetMapping("/tags")
    public ResponseEntity<List<TagDto>> getAllEventsTag() {
        return ResponseEntity.status(HttpStatus.OK).body(tagsService.findByTypeAndLanguageCode(TagType.EVENT, "en"));
    }

    /**
     * Method for creating {@link EcoNewsVO}.
     *
     * @param addEcoNewsDtoRequest dto for {@link EcoNewsVO} entity.
     * @param file                 of {@link MultipartFile}
     * @return {@link GenericResponseDto} with of operation and errors fields.
     */
    @ApiOperation(value = "Save EcoNews.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = GenericResponseDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @ResponseBody
    @PostMapping("/")
    public GenericResponseDto saveEcoNews(@Valid @RequestPart AddEcoNewsDtoRequest addEcoNewsDtoRequest,
        BindingResult bindingResult,
        @ImageValidation @RequestParam(required = false, name = "file") MultipartFile file,
        @ApiIgnore Principal principal) {
        if (!bindingResult.hasErrors()) {
            ecoNewsService.save(addEcoNewsDtoRequest, file, principal.getName());
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which updates {@link EcoNewsVO}.
     *
     * @param ecoNewsDtoManagement of {@link EcoNewsDtoManagement}.
     * @param file                 of {@link MultipartFile}.
     * @return {@link GenericResponseDto} with of operation and errors fields.
     */
    @ApiOperation(value = "Update Econews.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PutMapping("/")
    public GenericResponseDto update(@Valid @RequestPart EcoNewsDtoManagement ecoNewsDtoManagement,
        BindingResult bindingResult,
        @ImageValidation @RequestPart(required = false, name = "file") MultipartFile file) {
        if (!bindingResult.hasErrors()) {
            ecoNewsService.update(ecoNewsDtoManagement, file);
        }
        return buildGenericResponseDto(bindingResult);
    }
}
