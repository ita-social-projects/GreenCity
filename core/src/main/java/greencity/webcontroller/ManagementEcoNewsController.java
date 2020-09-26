package greencity.webcontroller;

import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsDto;
import greencity.service.EcoNewsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management/eco-news")
public class ManagementEcoNewsController {
    private final EcoNewsService ecoNewsService;

    @GetMapping
    public String getAllEcoNews(Model model, @ApiIgnore Pageable pageable) {
        PageableDto<EcoNewsDto> allEcoNews = ecoNewsService.findAll(pageable);
        model.addAttribute("pageable", allEcoNews);
        return "core/management_eco_news";
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {
        ecoNewsService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        ecoNewsService.deleteAll(listId);
        return ResponseEntity.status(HttpStatus.OK).body(listId);
    }

    @GetMapping("/find")
    public ResponseEntity<EcoNewsDto> getTipsAndTricksById(@RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.findDtoById(id));
    }
}
