package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.search.SearchResponseDto;
import greencity.service.SearchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("")
    public ResponseEntity<List<SearchResponseDto>> search(
        @ApiParam(value = "Query to serch") @RequestParam String searchQuery) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.search(searchQuery));
    }
}
