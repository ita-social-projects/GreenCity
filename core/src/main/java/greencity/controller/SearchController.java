package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageableWithLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventsDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    @Operation(summary = "Search.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @ApiLocale
    @GetMapping("")
    public ResponseEntity<SearchResponseDto> search(
        @Parameter(description = "Query to search") @RequestParam String searchQuery,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.search(searchQuery, locale.getLanguage()));
    }

    /**
     * Method for searching.
     *
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchNewsDto} instances.
     */
    @Operation(summary = "Search Eco news.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER,
            content = @Content(examples = @ExampleObject(HttpStatuses.SEE_OTHER))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/econews")
    @ApiPageableWithLocale
    public ResponseEntity<PageableDto<SearchNewsDto>> searchEcoNews(
        @Parameter(hidden = true) Pageable pageable,
        @Parameter(description = "Query to search") @RequestParam String searchQuery,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(searchService.searchAllNews(pageable, searchQuery, locale.getLanguage()));
    }

    /**
     * Method for searching events.
     *
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchEventsDto} instances.
     * @author Anton Bondar
     */
    @Operation(summary = "Search Events.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/events")
    @ApiPageableWithLocale
    public ResponseEntity<PageableDto<SearchEventsDto>> searchEvents(
        @Parameter(hidden = true) Pageable pageable,
        @Parameter(description = "Query to search") @RequestParam String searchQuery,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(searchService.searchAllEvents(pageable, searchQuery, locale.getLanguage()));
    }
}
