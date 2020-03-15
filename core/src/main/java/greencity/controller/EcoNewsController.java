package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.AppConstant;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.SearchCriteriaEcoNewsDto;
import greencity.entity.EcoNews;
import greencity.service.EcoNewsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/econews")
public class EcoNewsController {
    private final EcoNewsService ecoNewsService;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public EcoNewsController(EcoNewsService ecoNewsService) {
        this.ecoNewsService = ecoNewsService;
    }

    /**
     * Method for creating {@link EcoNews}.
     *
     * @param addEcoNewsDtoRequest - dto for {@link EcoNews} entity.
     * @return dto {@link AddEcoNewsDtoResponse} instance.
     * @author Yuriy Olkhovskyi & Kovaliv Taras.
     */
    @ApiOperation(value = "Add new eco news.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = EcoNews.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping
    public ResponseEntity<AddEcoNewsDtoResponse> save(@RequestBody AddEcoNewsDtoRequest addEcoNewsDtoRequest,
                                                      @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ecoNewsService.save(addEcoNewsDtoRequest, principal.getName()));
    }

    /**
     * Method for getting three last eco news.
     *
     * @return list of {@link EcoNewsDto} instances.
     * @author Yuriy Olkhovskyi.
     */
    @ApiOperation(value = "Get three last eco news.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/newest")
    public ResponseEntity<List<EcoNewsDto>> getThreeLastEcoNews(
        @ApiParam(value = "Code of the needed language.",
            defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE)
        @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE) String language
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.getThreeLastEcoNews(language));
    }

    /**
     * Method for getting all eco news by page.
     *
     * @return PageableDto of {@link EcoNewsDto} instances.
     * @author Yuriy Olkhovskyi & Kovaliv Taras.
     */
    @ApiOperation(value = "Find all eco news by page.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("")
    @ApiPageable
    public ResponseEntity<PageableDto<EcoNewsDto>> findAll(
        @ApiIgnore Pageable page,
        @ApiParam(value = "Code of the needed language.", defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE)
        @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE) String language) {
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.findAll(page, language));
    }

    /**
     * Method for deleting {@link EcoNews} by its id.
     *
     * @param econewsId - {@link EcoNews} id which will be deleted.
     * @return id of deleted {@link EcoNews}.
     * @author Yuriy Olkhovskyi.
     */
    @ApiOperation(value = "Delete eco news.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/{econewsId}")
    public ResponseEntity<Object> delete(@PathVariable Long econewsId) {
        ecoNewsService.delete(econewsId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for getting all eco news by tags and language.
     *
     * @param searchCriteriaEcoNewsDto - - dto for search {@link EcoNewsDto} by tags and language
     * @return list of {@link EcoNewsDto} instances.
     * @author Kovaliv Taras.
     */
    @ApiOperation(value = "Get eco news by tags")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/tags")
    @ApiPageable
    public ResponseEntity<PageableDto<EcoNewsDto>> getEcoNews(@ApiIgnore Pageable page,
                                                              @RequestBody SearchCriteriaEcoNewsDto searchCriteriaEcoNewsDto) {
        if (searchCriteriaEcoNewsDto.getTags() == null || searchCriteriaEcoNewsDto.getTags().size() == 0) {
            return ResponseEntity.status(HttpStatus.OK).body(
                ecoNewsService.findAll(page, searchCriteriaEcoNewsDto.getLanguage().getCode()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(ecoNewsService.find(page, searchCriteriaEcoNewsDto));
    }
}
