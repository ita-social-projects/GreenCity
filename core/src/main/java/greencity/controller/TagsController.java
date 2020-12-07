package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ValidLanguage;
import greencity.dto.tag.TagVO;
import greencity.service.TagsService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/tags")
@AllArgsConstructor
public class TagsController {
    private final TagsService tagsService;

    /**
     * The method which returns all tags' names by type and language code.
     *
     * @param locale {@link Locale}
     * @param type {@link String}
     * @return list of {@link String} (tag's names).
     * @author Markiyan Derevetskyi
     */
    @ApiOperation(value = "Find all tags by type and language code")
    @GetMapping("/search")
    @ApiLocale
    public ResponseEntity<List<String>> findByTypeAndLanguageCode(@ApiIgnore @ValidLanguage Locale locale,
        @RequestParam String type) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(tagsService.findByTypeAndLanguageCode(type, locale.getLanguage()));
    }
}
