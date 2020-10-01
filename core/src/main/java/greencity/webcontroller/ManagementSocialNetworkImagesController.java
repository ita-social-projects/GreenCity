package greencity.webcontroller;

import greencity.dto.PageableDto;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.entity.SocialNetworkImage;
import greencity.service.SocialNetworkImageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/management/socialnetworkimages")
public class ManagementSocialNetworkImagesController {
    private final SocialNetworkImageService socialNetworkImageService;
    /**
     * Method that returns management page with all {@link SocialNetworkImage}.
     *
     * @param query Query for searching related data
     * @param model Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     * @author Orest Mamchuk
     */
    @GetMapping
    public String getAllSocialNetworkImages(@RequestParam(required = false, name = "query") String query, Model model,
                                            @ApiIgnore Pageable pageable) {
        PageableDto<SocialNetworkImageResponseDTO> socialNetworkImages =
                query == null || query.isEmpty() ? socialNetworkImageService.findAll(pageable)
                        : socialNetworkImageService.searchBy(pageable, query);
        model.addAttribute("pageable", socialNetworkImages);
        return "core/management_social_network_images";
    }

    /**
     * Method which deteles {@link SocialNetworkImage}  by given id.
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
}
