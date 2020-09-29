package greencity.webcontroller;

import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsDtoManagement;
import greencity.dto.genericresponse.FieldErrorDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.entity.EcoNews;
import greencity.service.EcoNewsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management/eco-news")
public class ManagementEcoNewsController {
    private final EcoNewsService ecoNewsService;

    /**
     * Method that returns management page with all {@link EcoNews}.
     *
     * @param model Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     */
    @GetMapping
    public String getAllEcoNews(@RequestParam(required = false, name = "query") String query, Model model,
                                @ApiIgnore Pageable pageable) {
        PageableDto<EcoNewsDto> allEcoNews =
                query == null || query.isEmpty() ? ecoNewsService.findAll(pageable)
                        : ecoNewsService.searchEcoNewsBy(pageable, query);
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
     * @param file of {@link MultipartFile}
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
        if (bindingResult.hasErrors()) {
            GenericResponseDto genericResponseDto = new GenericResponseDto();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                genericResponseDto.getErrors().add(
                        new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return genericResponseDto;
        }
        ecoNewsService.save(addEcoNewsDtoRequest, file, principal.getName());
        return GenericResponseDto.builder().errors(new ArrayList<>()).build();
    }

    /**
     * Method which updates {@link EcoNews}.
     *
     * @param ecoNewsDtoManagement of {@link EcoNewsDtoManagement}.
     * @param file                       of {@link MultipartFile}.
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
        if (bindingResult.hasErrors()) {
            GenericResponseDto genericResponseDto = new GenericResponseDto();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                genericResponseDto.getErrors().add(
                        new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return genericResponseDto;
        }
        ecoNewsService.update(ecoNewsDtoManagement, file);
        return GenericResponseDto.builder().errors(new ArrayList<>()).build();
    }
}
