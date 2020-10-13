package greencity.webcontroller;

import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsDtoManagement;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.entity.EcoNews;
import greencity.service.EcoNewsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/management/eco-news")
public class ManagementEcoNewsController {
    private final EcoNewsService ecoNewsService;

    /**
     * Method that returns management page with all {@link EcoNews}.
     *
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     */
    @GetMapping
    public String getAllEcoNews(@RequestParam(required = false, name = "query") String query, Model model,
                                @ApiIgnore Pageable pageable) {
        PageableDto<EcoNewsDto> allEcoNews = query == null || query.isEmpty()
                ? ecoNewsService.findAll(pageable) : ecoNewsService.searchEcoNewsBy(pageable, query);
        model.addAttribute("pageable", allEcoNews);
        return "core/management_eco_news";
    }

    /**
     * Method which deteles {@link EcoNews} and {@link greencity.entity.FactOfTheDayTranslation} by given id.
     *
     * @param id of Eco New
     * @return {@link ResponseEntity}
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {
        ecoNewsService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    /**
     * Method for deleting {@link EcoNews} by given id.
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
    public ResponseEntity<EcoNewsDto> getEcoNewsById(@RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.findDtoById(id));
    }

    /**
     * Method for creating {@link EcoNews}.
     *
     * @param addEcoNewsDtoRequest dto for {@link EcoNews} entity.
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
    public GenericResponseDto saveEcoNews(@Valid
                                          @RequestPart AddEcoNewsDtoRequest addEcoNewsDtoRequest,
                                          BindingResult bindingResult,
                                          @ImageValidation
                                          @RequestParam(required = false, name = "file") MultipartFile file,
                                          @ApiIgnore Principal principal) {
        if (!bindingResult.hasErrors()) {
            ecoNewsService.save(addEcoNewsDtoRequest, file, principal.getName());
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which updates {@link EcoNews}.
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
    public GenericResponseDto update(@Valid
                                     @RequestPart EcoNewsDtoManagement ecoNewsDtoManagement,
                                     BindingResult bindingResult,
                                     @ImageValidation
                                     @RequestPart(required = false, name = "file") MultipartFile file) {
        if (!bindingResult.hasErrors()) {
            ecoNewsService.update(ecoNewsDtoManagement, file);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Returns  management page with Eco news filtered data.
     *
     * @param model                   ModelAndView that will be configured and returned to user.
     * @param ecoNewsViewDto used for receive parameters for filters from UI.
     */
    @PostMapping(value = "", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String filterData(Model model,
                             @PageableDefault(value = 20) @ApiIgnore Pageable pageable,
                             EcoNewsViewDto ecoNewsViewDto) {
        Pageable paging = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(), Sort.by("creationDate").descending());
        PageableDto<EcoNewsDto> pageableDto =
                ecoNewsService.getFilteredDataForManagementByPage(paging, ecoNewsViewDto);
        model.addAttribute("pageable", pageableDto);
        model.addAttribute("fields", ecoNewsViewDto);
        return "core/management_eco_news";
    }
}
