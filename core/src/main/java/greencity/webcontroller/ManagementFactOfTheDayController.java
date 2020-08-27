package greencity.webcontroller;

import greencity.annotations.ApiPageable;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.service.FactOfTheDayService;
import greencity.service.LanguageService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management/factoftheday")
public class ManagementFactOfTheDayController {
    @Autowired
    private FactOfTheDayService factOfTheDayService;
    @Autowired
    private LanguageService languageService;

    /**
     * Returns management page with all facts of the day.
     *
     * @param model ModelAndView that will be configured and returned to user
     * @return model
     */
    @ApiPageable
    @ApiOperation(value = "Get management page with facts of the day.")
    @GetMapping("")
    public String getAllFacts(Model model, @ApiIgnore Pageable pageable) {
        PageableDto<FactOfTheDayDTO> allFactsOfTheDay = factOfTheDayService.getAllFactsOfTheDay(pageable);
        model.addAttribute("pageable", allFactsOfTheDay);
        model.addAttribute("languages",languageService.getAllLanguages());
        return "core/management_fact_of_the_day";
    }
}
