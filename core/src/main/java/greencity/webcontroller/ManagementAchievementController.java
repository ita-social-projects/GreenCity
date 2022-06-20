package greencity.webcontroller;

import greencity.annotations.ImageValidation;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.AchievementDto;
import greencity.dto.achievement.AchievementManagementDto;
import greencity.dto.achievement.AchievementPostDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.language.LanguageDTO;
import greencity.enums.UserActionType;
import greencity.service.AchievementCategoryService;
import greencity.service.AchievementService;
import greencity.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management/achievement")
public class ManagementAchievementController {
    private final AchievementService achievementService;
    private final AchievementCategoryService achievementCategoryService;
    private final LanguageService languageService;

    /**
     * Method that returns management page with all {@link AchievementVO}.
     *
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     * @author Orest Mamchuk
     */
    @GetMapping
    public String getAllAchievement(@RequestParam(required = false, name = "query") String query, Pageable pageable,
        Model model) {
        Pageable paging = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").descending());
        PageableAdvancedDto<AchievementDto> allAchievements = query == null || query.isEmpty()
            ? achievementService.findAll(paging)
            : achievementService.searchAchievementBy(paging, query);
        List<LanguageDTO> languages = languageService.getAllLanguages();

        languages.stream()
            .filter(language -> "ru".equalsIgnoreCase(language.getCode()))
            .findFirst().ifPresent(languages::remove);

        model.addAttribute("pageable", allAchievements);
        model.addAttribute("categoryList", achievementCategoryService.findAll());
        model.addAttribute("languages", languages);
        model.addAttribute("conditionList", UserActionType.values());
        return "core/management_achievement";
    }

    /**
     * Method for creating {@link AchievementVO}.
     *
     * @param achievementPostDto dto for {@link AchievementVO} entity.
     * @return {@link GenericResponseDto} with of operation and errors fields.
     * @author Orest Mamchuk
     */
    @PostMapping
    @ResponseBody
    public GenericResponseDto saveAchievement(@Valid @RequestPart AchievementPostDto achievementPostDto,
        @ImageValidation @RequestParam MultipartFile file, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            achievementService.save(achievementPostDto, file);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which deteles {@link AchievementVO} by given id.
     *
     * @param id of {@link AchievementVO}
     * @return {@link ResponseEntity}
     * @author Orest Mamchuk
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteAchievementById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(achievementService.delete(id));
    }

    /**
     * Method for deleting {@link AchievementVO} by given id.
     *
     * @param listId list of IDs.
     * @return {@link ResponseEntity}
     * @author Orest Mamchuk
     */
    @DeleteMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        achievementService.deleteAll(listId);
        return ResponseEntity.status(HttpStatus.OK).body(listId);
    }

    /**
     * Method for getting econews by id.
     *
     * @param id of Eco New
     * @return {@link AchievementVO} instance.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AchievementVO> getAchievementById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(achievementService.findById(id));
    }

    /**
     * Method which updates {@link AchievementVO}.
     *
     * @param achievementManagementDto of {@link AchievementManagementDto}.
     * @return {@link GenericResponseDto} with of operation and errors fields.
     */
    @PutMapping
    @ResponseBody
    public GenericResponseDto update(@Valid @RequestPart AchievementManagementDto achievementManagementDto,
        @ImageValidation @RequestParam(required = false) MultipartFile file, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            achievementService.update(achievementManagementDto, file);
        }
        return buildGenericResponseDto(bindingResult);
    }
}
