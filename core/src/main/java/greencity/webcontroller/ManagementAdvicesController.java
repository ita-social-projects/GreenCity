package greencity.webcontroller;

import greencity.dto.PageableDto;
import greencity.dto.advice.AdviceVO;
import greencity.entity.Advice;
import greencity.service.AdviceService;
import greencity.service.LanguageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management/advice")
public class ManagementAdvicesController {
    private final AdviceService adviceService;
    private final LanguageService languageService;

    /**
     * Method that returns management page with all {@link Advice}'s.
     * @param model {@link Model} - for passing data between controller and view
     * @param pageable {@link Pageable}
     * @return name of template {@link String}
     * @author Markiyan Derevetskyi
     * */
    @GetMapping
    public String findAllAdvices(Model model, @ApiIgnore Pageable pageable) {
        PageableDto<AdviceVO> allAdvices = adviceService.getAllAdvices(pageable);
        model.addAttribute("pageable", allAdvices);
        model.addAttribute("languages", languageService.getAllLanguages());

        return "core/management_advices";
    }
}
