package greencity.controller;

import greencity.annotations.ValidLanguage;
import greencity.dto.tag.TagDto;
import greencity.enums.TagType;
import greencity.service.TagsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/tags")
@AllArgsConstructor
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
    @GetMapping("/search")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Found all tags by type and language code")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<List<TagDto>> findByTypeAndLanguageCode(@ApiIgnore @ValidLanguage Locale locale,
        @RequestParam TagType type) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(tagsService.findByTypeAndLanguageCode(type, locale.getLanguage()));
    }
}