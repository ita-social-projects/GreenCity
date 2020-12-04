package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ValidLanguage;
import greencity.service.TagsService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Locale;

@RestController
@AllArgsConstructor
@RequestMapping("/tags")
public class TagsController {
    private final TagsService tagsService;

    /**
     * Method that finds all tags by language code.
     *
     * @param locale {@link Locale} with needed language code.
     * @return {@link List} of {@link String} of all tags.
     * @author Markiyan Derevetskyi
     */
    @ApiOperation(value = "Find all tags")
    @GetMapping
    @ApiLocale
    public ResponseEntity<List<String>> findAllHabitsTags(@ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(tagsService.findAllTags(locale.getLanguage()));
    }
}
