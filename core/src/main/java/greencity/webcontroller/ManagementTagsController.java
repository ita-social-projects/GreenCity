package greencity.webcontroller;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.tag.TagVO;
import greencity.service.LanguageService;
import greencity.service.TagsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/management/tags")
public class ManagementTagsController {
    private final TagsService tagsService;
    private final LanguageService languageService;

    @GetMapping
    public String findAllTags(Model model, Pageable pageable) {
        PageableAdvancedDto<TagVO> tags = tagsService.findAll(pageable);
        List<LanguageDTO> languages = languageService.getAllLanguages();

        model.addAttribute("tags", tags);
        model.addAttribute("languages", languages);

        return "core/management_tags";
    }
}
