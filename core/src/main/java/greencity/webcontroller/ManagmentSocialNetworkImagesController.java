package greencity.webcontroller;

import greencity.dto.PageableDto;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.socialnetwork.SocialNetworkResponseDTO;
import greencity.service.SocialNetworkImageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management/socialnetworkimages")
public class ManagmentSocialNetworkImagesController {
    private final SocialNetworkImageService socialNetworkImageService;

    /**
     * Method that returns management page with all {@link greencity.entity.SocialNetworkImage}.
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
        PageableDto<SocialNetworkImageResponseDTO> socialNetworkImages =
                query == null || query.isEmpty() ? socialNetworkImageService.findAll(pageable) : socialNetworkImageService.searchBy(pageable, query);
        model.addAttribute("pageable", socialNetworkImages);
        return "core/management_social_network_images";
    }
}
