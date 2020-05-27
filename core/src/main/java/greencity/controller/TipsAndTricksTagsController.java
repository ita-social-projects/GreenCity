package greencity.controller;

import greencity.service.TipsAndTricksTagsService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tipsandtricksTags")
@AllArgsConstructor
public class TipsAndTricksTagsController {
    private final TipsAndTricksTagsService tipsAndTricksTagsService;

    /**
     * The method which returns all tips & tricks tags.
     *
     * @return list of {@link String} (tag's names).
     */
    @GetMapping
    public ResponseEntity<List<String>> findAllTipsAndTricksTags() {
        return ResponseEntity.status(HttpStatus.OK).body(tipsAndTricksTagsService.findAll());
    }
}
