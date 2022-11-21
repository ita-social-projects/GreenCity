package greencity.webcontroller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.ImageValidation;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.*;
import greencity.dto.factoftheday.FactOfTheDayTranslationVO;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.tag.TagDto;
import greencity.dto.user.UserVO;
import greencity.service.EcoNewsService;
import greencity.service.TagsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Set;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management/eco-news")
public class ManagementEcoNewsController {
    private final EcoNewsService ecoNewsService;
    private final TagsService tagsService;

    /**
     * Method that returns management page with all {@link EcoNewsVO}.
     *
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     */
    @GetMapping
    public String getAllEcoNews(@RequestParam(required = false, name = "query") String query, Model model,
        @ApiIgnore Pageable pageable, EcoNewsViewDto ecoNewsViewDto) {
        PageableAdvancedDto<EcoNewsDto> allEcoNews;
        if (!ecoNewsViewDto.isEmpty()) {
            allEcoNews = ecoNewsService.getFilteredDataForManagementByPage(pageable, ecoNewsViewDto);
            model.addAttribute("fields", ecoNewsViewDto);
            model.addAttribute("query", "");
        } else {
            allEcoNews = query == null || query.isEmpty()
                ? ecoNewsService.findAll(pageable)
                : ecoNewsService.searchEcoNewsBy(pageable, query);
            model.addAttribute("fields", new EcoNewsViewDto());
            model.addAttribute("query", query);
        }

        model.addAttribute("pageable", allEcoNews);
        Sort sort = pageable.getSort();
        StringBuilder orderUrl = new StringBuilder("");
        if (!sort.isEmpty()) {
            for (Sort.Order order : sort) {
                orderUrl.append(orderUrl.toString() + order.getProperty() + "," + order.getDirection());
            }
            model.addAttribute("sortModel", orderUrl);
        }
        model.addAttribute("ecoNewsTag", tagsService.findAllEcoNewsTags("en"));
        model.addAttribute("pageSize", pageable.getPageSize());
        return "core/management_eco_news";
    }

    /**
     * Method which detele {@link EcoNewsVO} and {@link FactOfTheDayTranslationVO}
     * by given id.
     *
     * @param id of Eco New
     * @return {@link ResponseEntity}
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id, @ApiIgnore @CurrentUser UserVO user) {
        ecoNewsService.delete(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    /**
     * Method for deleting {@link EcoNewsVO} by given id.
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
     * Method finds {@link EcoNewsVO} with all translations by given id.
     *
     * @param id of {@link EcoNewsVO}.
     * @return {@link EcoNewsDto}.
     */
    @ApiOperation(value = "Find econews by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitManagementDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @ApiLocale
    @GetMapping("/find/{id}")
    public ResponseEntity<EcoNewsDto> getEcoNewsById(@PathVariable Long id,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ecoNewsService.findDtoByIdAndLanguage(id, locale.getLanguage()));
    }

    /**
     * Returns management page with single {@link EcoNewsVO}.
     *
     * @param id of {@link EcoNewsVO}.
     * @return {@link EcoNewsDto}.
     */
    @ApiOperation(value = "Find econew's page by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = EcoNewsDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @ApiLocale
    @GetMapping("/{id}")
    public String getEcoNewsPage(@PathVariable("id") Long id,
        @ApiIgnore Locale locale, Model model) {
        EcoNewsDto econew = ecoNewsService.findDtoByIdAndLanguage(id, locale.getLanguage());
        model.addAttribute("econew", econew);
        ZonedDateTime time = econew.getCreationDate();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd , yyyy");
        model.addAttribute("time", time.format(format));
        model.addAttribute("ecoNewsTag", tagsService.findAllEcoNewsTags("en"));
        return "core/management_eco_new";
    }

    /**
     * Method for getting all econews tag.
     *
     * @return {@link TagDto} instance.
     */
    @GetMapping("/tags")
    public ResponseEntity<List<TagDto>> getAllEcoNewsTag() {
        return ResponseEntity.status(HttpStatus.OK).body(tagsService.findAllEcoNewsTags("en"));
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
    @PostMapping("/save")
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

    /**
     * Method for getting list of users who liked post by post id.
     *
     * @return list of {@link UserVO} instances.
     */
    @ApiOperation(value = "Get list of users who liked the post by post id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/{id}/likes")
    @ResponseBody
    public Set<UserVO> getLikesByEcoNewsId(@PathVariable Long id,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ecoNewsService.findUsersWhoLikedPost(id);
    }

    /**
     * Method for getting list of users who disliked post by post id.
     *
     * @return list of {@link UserVO} instances.
     */
    @ApiOperation(value = "Get list of users who disliked the post by post id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/{id}/dislikes")
    @ResponseBody
    public Set<UserVO> getDislikesByEcoNewsId(@PathVariable Long id,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ecoNewsService.findUsersWhoDislikedPost(id);
    }
}
