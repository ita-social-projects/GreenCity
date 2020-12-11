package greencity.webcontroller;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.tag.TagPostDto;
import greencity.dto.tag.TagVO;
import greencity.dto.tag.TagViewDto;
import greencity.service.LanguageService;
import greencity.service.TagsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/management/tags")
public class ManagementTagsController {
    private final TagsService tagsService;
    private final LanguageService languageService;

    @GetMapping
    public String findAll(Model model, Pageable pageable) {
        PageableAdvancedDto<TagVO> tags = tagsService.findAll(pageable);
        List<LanguageDTO> languages = languageService.getAllLanguages();

        model.addAttribute("tags", tags);
        model.addAttribute("languages", languages);

        return "core/management_tags";
    }

    @ResponseBody
    @PostMapping
    public GenericResponseDto save(@Valid @RequestBody TagPostDto tagPostDto,
        BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            tagsService.save(tagPostDto);
        }

        return buildGenericResponseDto(bindingResult);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagVO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(tagsService.findById(id));
    }

    @DeleteMapping
    public ResponseEntity<List<Long>> bulkDelete(@RequestBody List<Long> ids) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(tagsService.bulkDelete(ids));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(tagsService.deleteById(id));
    }

    @PutMapping("/{id}")
    @ResponseBody
    public GenericResponseDto updateTag(@Valid @RequestBody TagPostDto tagPostDto, BindingResult bindingResult,
        @PathVariable Long id) {
        if (!bindingResult.hasErrors()) {
            tagsService.update(tagPostDto, id);
        }

        return buildGenericResponseDto(bindingResult);
    }

    @PostMapping("/search")
    public String search(Model model, @ApiIgnore Pageable pageable, TagViewDto tagViewDto) {
        PageableAdvancedDto<TagVO> foundTags = tagsService.search(pageable, tagViewDto);

        model.addAttribute("tags", foundTags);
        model.addAttribute("languages", languageService.getAllLanguages());
        model.addAttribute("fields", tagViewDto);

        return "core/management_tags";
    }
}
