package greencity.webcontroller;

import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.genericresponse.GenericResponseDto;
import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoManagement;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.TipsAndTricks;
import greencity.service.TipsAndTricksService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management/tipsandtricks")
public class ManagementTipsAndTricksController {
    private TipsAndTricksService tipsAndTricksService;

    /**
     * Method for getting tips & tricks by page.
     *
     * @param model Model that will be configured.
     * @return View template path {@link String}.
     * @author Yurii Savchenko
     */
    @GetMapping
    public String findAll(@RequestParam(required = false, name = "query") String query,
                          Model model, @ApiIgnore Pageable pageable) {
        PageableDto<TipsAndTricksDtoResponse> pageableDto = query == null || query.isEmpty()
            ? tipsAndTricksService.findAll(pageable) : tipsAndTricksService.searchBy(pageable, query);
        model.addAttribute("pageable", pageableDto);
        return "core/management_tips_and_tricks";
    }

    /**
     * Method for getting tips & tricks by id.
     *
     * @return {@link TipsAndTricksDtoManagement} instance.
     */
    @ApiOperation(value = "Get tips & tricks by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = TipsAndTricksDtoManagement.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/find")
    public ResponseEntity<TipsAndTricksDtoManagement> getTipsAndTricksById(
        @RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(tipsAndTricksService.findManagementDtoById(id));
    }

    /**
     * Method for deleting tips & tricks.
     *
     * @param id {@link TipsAndTricksDtoManagement}'s id.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Delete tips & tricks.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {
        tipsAndTricksService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    /**
     * Method for deleting {@link TipsAndTricks} by given id.
     *
     * @param listId list of IDs.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Delete all tips & tricks by given IDs.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        tipsAndTricksService.deleteAll(listId);
        return ResponseEntity.status(HttpStatus.OK).body(listId);
    }

    /**
     * Method which updates {@link TipsAndTricks}.
     *
     * @param tipsAndTricksDtoManagement of {@link TipsAndTricksDtoManagement}.
     * @param file                       of {@link MultipartFile}.
     * @return {@link GenericResponseDto} with of operation and errors fields.
     */
    @ApiOperation(value = "Update tips & tricks.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PutMapping("/")
    public GenericResponseDto update(@Valid
                                     @RequestPart TipsAndTricksDtoManagement tipsAndTricksDtoManagement,
                                     BindingResult bindingResult,
                                     @ImageValidation
                                     @RequestPart(required = false, name = "file") MultipartFile file) {
        if (bindingResult.hasErrors()) {
            return buildGenericResponseDto(bindingResult);
        }
        tipsAndTricksService.update(tipsAndTricksDtoManagement, file);
        return new GenericResponseDto();
    }

    /**
     * Method for creating {@link TipsAndTricks}.
     *
     * @param tipsAndTricksDtoRequest dto for {@link TipsAndTricks} entity.
     * @param file                    of {@link MultipartFile}
     * @return {@link GenericResponseDto} with of operation and errors fields.
     */
    @ApiOperation(value = "Save tips & tricks.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = GenericResponseDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @ResponseBody
    @PostMapping("/")
    public GenericResponseDto save(@Valid
                                   @RequestPart TipsAndTricksDtoRequest tipsAndTricksDtoRequest,
                                   BindingResult bindingResult,
                                   @ImageValidation
                                   @RequestParam(required = false, name = "file") MultipartFile file,
                                   @ApiIgnore Principal principal) {
        if (bindingResult.hasErrors()) {
            return buildGenericResponseDto(bindingResult);
        }
        tipsAndTricksService.save(tipsAndTricksDtoRequest, file, principal.getName());
        return new GenericResponseDto();
    }
}
