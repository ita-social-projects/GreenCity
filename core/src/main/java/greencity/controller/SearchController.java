package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageableWithLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.service.SearchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Locale;

@RestController
@RequestMapping("/search")
@AllArgsConstructor
public class SearchController {
    private final SearchService searchService;

    /**
     * Method for search.
     *
     * @param searchQuery query to search.
     * @return list of {@link SearchResponseDto}.
     */
    @ApiOperation(value = "Search.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ApiLocale
    @GetMapping("")
    public ResponseEntity<SearchResponseDto> search(
        @ApiParam(value = "Query to search") @RequestParam String searchQuery,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.search(searchQuery, locale.getLanguage()));
    }

    /**
     * Method for search.
     *
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchNewsDto} instances.
     */
    @ApiOperation(value = "Search Eco news.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/econews")
    @ApiPageableWithLocale
    public ResponseEntity<PageableDto<SearchNewsDto>> searchEcoNews(
        @ApiIgnore Pageable pageable,
        @ApiParam(value = "Query to search") @RequestParam String searchQuery,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(searchService.searchAllNews(pageable, searchQuery, locale.getLanguage()));
    }
}
