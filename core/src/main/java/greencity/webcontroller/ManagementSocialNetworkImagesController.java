package greencity.webcontroller;

import greencity.dto.PageableDto;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.service.SocialNetworkImageService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/management/socialnetworkimages")
public class ManagementSocialNetworkImagesController {
    private final SocialNetworkImageService socialNetworkImageService;

    /**
     * Method that returns management page with all
     * {@link SocialNetworkImageResponseDTO}.
     *
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     * @author Orest Mamchuk
     */
    @GetMapping
    public String getAllSocialNetworkImages(Model model,
        @Parameter(hidden = true) Pageable pageable) {
        PageableDto<SocialNetworkImageResponseDTO> socialNetworkImages = socialNetworkImageService.findAll(pageable);
        model.addAttribute("pageable", socialNetworkImages);
        return "core/management_social_network_images";
    }

    // /**
    // * Method for creating SocialNetworkImage.
    // *
    // * @param socialNetworkImageRequestDTO dto for SocialNetworkImage
    // * entity.
    // * @param file of {@link MultipartFile}
    // * @return {@link GenericResponseDto} with of operation and errors fields.
    // */
    // @Operation(summary = "Save SocialNetworkImages.")
    // @ApiResponses(value = {
    // @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
    // content = @Content(schema = @Schema(implementation =
    // GenericResponseDto.class))),
    // @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    // })
    // @ResponseBody
    // @PostMapping("/")
    // public GenericResponseDto save(@Valid @RequestPart
    // SocialNetworkImageRequestDTO socialNetworkImageRequestDTO,
    // BindingResult bindingResult,
    // @ImageValidation @RequestParam(required = false, name = "file") MultipartFile
    // file) {
    // if (!bindingResult.hasErrors()) {
    // socialNetworkImageService.save(socialNetworkImageRequestDTO, file);
    // }
    // return buildGenericResponseDto(bindingResult);
    // }

    /**
     * Method which deteles SocialNetworkImage by given id.
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
     * Method for deleting {@link SocialNetworkImageVO} by given id.
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

    // /**
    // * Method which updates {@link SocialNetworkImageVO}.
    // *
    // * @param socialNetworkImageResponseDTO of
    // * {@link SocialNetworkImageResponseDTO}.
    // * @param file of {@link MultipartFile}.
    // * @return {@link GenericResponseDto} with of operation and errors fields.
    // */
    // @Operation(summary = "Update Econews.")
    // @ApiResponses(value = {
    // @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
    // @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    // })
    // @ResponseBody
    // @PutMapping("/")
    // public GenericResponseDto update(@Valid @RequestPart
    // SocialNetworkImageResponseDTO socialNetworkImageResponseDTO,
    // BindingResult bindingResult,
    // @ImageValidation @RequestPart(required = false, name = "file") MultipartFile
    // file) {
    // if (!bindingResult.hasErrors()) {
    // socialNetworkImageService.update(socialNetworkImageResponseDTO, file);
    // }
    // return buildGenericResponseDto(bindingResult);
    // }
}
