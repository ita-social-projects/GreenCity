package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.tag.NewTagDto;
import greencity.enums.TagType;
import greencity.service.TagsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagsController {
    private final TagsService tagsService;

    /**
     * The method which returns all tags names by type and language code.
     *
     * @param type {@link TagType}.
     * @return list of {@link NewTagDto}.
     */
    @Operation(summary = "Find all tags by type and language code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping
    public ResponseEntity<List<NewTagDto>> findByType(@RequestParam TagType type) {
        return ResponseEntity.ok().body(tagsService.findByType(type));
    }
}
