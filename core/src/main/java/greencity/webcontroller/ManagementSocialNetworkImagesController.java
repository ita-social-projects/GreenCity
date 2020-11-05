package greencity.webcontroller;

import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.socialnetwork.SocialNetworkImageRequestDTO;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.entity.SocialNetworkImage;
import greencity.service.SocialNetworkImageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import javax.validation.Valid;
import java.util.List;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

@Controller
@AllArgsConstructor
@RequestMapping("/management/socialnetworkimages")
public class ManagementSocialNetworkImagesController {
    private final SocialNetworkImageService socialNetworkImageService;

    /**
     * Method that returns management page with all {@link SocialNetworkImage}.
     *
     * @param query    Query for searching related data
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     * @author Orest Mamchuk
     */
    @GetMapping
    public String getAllSocialNetworkImages(@RequestParam(required = false, name = "query") String query, Model model,
        @ApiIgnore Pageable pageable) {
        PageableDto<SocialNetworkImageResponseDTO> socialNetworkImages = query == null || query.isEmpty()
            ? socialNetworkImageService.findAll(pageable)
            : socialNetworkImageService.searchBy(pageable, query);
        model.addAttribute("pageable", socialNetworkImages);
        return "core/management_social_network_images";
    }

    /**
     * Method for creating {@link SocialNetworkImage}.
     *
     * @param socialNetworkImageRequestDTO dto for {@link SocialNetworkImage}
     *                                     entity.
     * @param file                         of {@link MultipartFile}
     * @return {@link GenericResponseDto} with of operation and errors fields.
     */
    @ApiOperation(value = "Save SocialNetworkImages.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = GenericResponseDto.class),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @ResponseBody
    @PostMapping("/")
    public GenericResponseDto save(@Valid @RequestPart SocialNetworkImageRequestDTO socialNetworkImageRequestDTO,
        BindingResult bindingResult,
        @ImageValidation @RequestParam(required = false, name = "file") MultipartFile file) {
        if (!bindingResult.hasErrors()) {
            socialNetworkImageService.save(socialNetworkImageRequestDTO, file);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which deteles {@link SocialNetworkImage} by given id.
     *
     * @param id of Social Network Images
     * @return {@link ResponseEntity}
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {
        socialNetworkImageService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    /**
     * Method for deleting {@link SocialNetworkImage} by given id.
     *
     * @param listId list of IDs.
     * @return {@link ResponseEntity}
     */
    @DeleteMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        socialNetworkImageService.deleteAll(listId);
        return ResponseEntity.status(HttpStatus.OK).body(listId);
    }

    /**
     * Method for getting socialnetworkimages by id.
     *
     * @param id of Eco New
     * @return {@link SocialNetworkImageResponseDTO} instance.
     */
    @GetMapping("/find")
    public ResponseEntity<SocialNetworkImageResponseDTO> getEcoNewsById(@RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(socialNetworkImageService.findDtoById(id));
    }

    /**
     * Method which updates {@link SocialNetworkImage}.
     *
     * @param socialNetworkImageResponseDTO of
     *                                      {@link SocialNetworkImageResponseDTO}.
     * @param file                          of {@link MultipartFile}.
     * @return {@link GenericResponseDto} with of operation and errors fields.
     */
    @ApiOperation(value = "Update Econews.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PutMapping("/")
    public GenericResponseDto update(@Valid @RequestPart SocialNetworkImageResponseDTO socialNetworkImageResponseDTO,
        BindingResult bindingResult,
        @ImageValidation @RequestPart(required = false, name = "file") MultipartFile file) {
        if (!bindingResult.hasErrors()) {
            socialNetworkImageService.update(socialNetworkImageResponseDTO, file);
        }
        return buildGenericResponseDto(bindingResult);
    }
}
