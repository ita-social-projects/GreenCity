package greencity.webcontroller;

import greencity.dto.PageableDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoManagement;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.service.TipsAndTricksService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management/tipsandtricks")
public class ManagementTipsAndTricksController {
    private TipsAndTricksService tipsAndTricksService;

    /**
     * Method for getting all tips & tricks by page.
     *
     * @param model Model that will be configured.
     * @return View template path {@link String}.
     * @author Yurii Savchenko
     */
    @GetMapping
    public String findAll(Model model, @ApiIgnore Pageable pageable) {
        PageableDto<TipsAndTricksDtoResponse> pageableDto =
            tipsAndTricksService.findAll(pageable);
        model.addAttribute("pageable", pageableDto);
        return "core/management_tips_and_tricks";
    }

    /**
     * Method that shows form for updating {@link TipsAndTricksDtoManagement}.
     *
     * @param id    {@link TipsAndTricksDtoManagement}'s id.
     * @param model Model that will be configured and returned to user.
     * @return View template path {@link String}.
     * @author Yurii Savchenko
     */

    @GetMapping("/edit/{id}")
    public String update(@PathVariable("id") Long id,
                         Model model) {
        TipsAndTricksDtoManagement tipsAndTricksDtoManagement =
            tipsAndTricksService.findManagementDtoById(id);
        model.addAttribute("id", id);
        model.addAttribute("tipsAndTricksDtoManagement", tipsAndTricksDtoManagement);
        return "core/update_tips_and_tricks";
    }

    /**
     * Method that updates user data.
     *
     * @param id  {@link TipsAndTricksDtoManagement}'s id.
     * @param tipsAndTricksDtoManagement dto with updated fields.
     * @return View template path {@link String}.
     * @author Yurii Savchenko
     */
    @PostMapping("/edit/{id}")
    public String update(@PathVariable("id") @ModelAttribute("id") Long id,
                         @ModelAttribute("tipsAndTricksDtoManagement")
                         @Valid TipsAndTricksDtoManagement tipsAndTricksDtoManagement,
                         BindingResult result,
                         @RequestParam(required = false, name = "file") MultipartFile file) {
        if (result.hasErrors()) {
            return "core/update_tips_and_tricks";
        }
        tipsAndTricksService.update(tipsAndTricksDtoManagement, file);
        return "redirect:/management/tipsandtricks";
    }

    /**
     * Method for deleting tips & tricks.
     *
     * @param id  {@link TipsAndTricksDtoManagement}'s id.
     * @return View template path {@link String}.
     * @author Yurii Savchenko
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        tipsAndTricksService.delete(id);
        return "redirect:/management/tipsandtricks";
    }
}
