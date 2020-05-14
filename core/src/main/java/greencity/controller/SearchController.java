package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.constant.HttpStatuses;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.search.SortingType;
import greencity.service.SearchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("")
    public ResponseEntity<SearchResponseDto> search(
        @ApiParam(value = "Query to search") @RequestParam String searchQuery) {
        return ResponseEntity.ok(searchService.search(searchQuery));
    }

    /**
     * Method for search all by page.
     *
     * @return list of {@link SearchResponseDto}.
     */
    @ApiOperation(value = "Search all by page.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @GetMapping("/all")
    public ResponseEntity<SearchResponseDto> searchAll(
        @ApiParam(value = "Query to search", required = true)
        @RequestParam String query,
        @ApiParam(defaultValue = "RELEVANCE", value = "Sort parameter")
        @RequestParam SortingType type,
        @ApiIgnore Pageable page) {
        return ResponseEntity.ok(searchService.search(page, query, type));
    }
}
