package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.tag.NewTagDto;
import greencity.dto.tag.TagDto;
import greencity.enums.TagType;
import greencity.service.TagsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@AllArgsConstructor
@RequestMapping("/tags")
public class TagsController {
    private final TagsService tagsService;

    /**
     * The method which returns all tags' names by type and language code.
     *
     * @param locale {@link Locale}
     * @param type   {@link TagType}
     * @return list of {@link TagDto} (tag's names).
     * @author Markiyan Derevetskyi
     */
    @ApiOperation(value = "Find all tags by type and language code")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK)
    })
    @GetMapping("/search")
    @ApiLocale
    public ResponseEntity<List<TagDto>> findByTypeAndLanguageCode(@ApiIgnore @ValidLanguage Locale locale,
        @RequestParam TagType type) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(tagsService.findByTypeAndLanguageCode(type, locale.getLanguage()));
    }

    /**
     * The method which returns all tags' names by type and language code.
     *
     * @param type {@link TagType}
     * @return list of {@link NewTagDto} (tag's names).
     */
    @ApiOperation(value = "Find all tags by type and language code")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK)
    })
    @GetMapping("/v2/search")
    public ResponseEntity<List<NewTagDto>> findByType(@RequestParam TagType type) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(tagsService.findByType(type));
    }
}
