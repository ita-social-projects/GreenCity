package greencity.service;

import greencity.dto.search.SearchRequestDto;
import greencity.dto.search.SearchResponseDto;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage search functionality.
 *
 * @author Kovaliv Taras
 * @version 1.0
 */
public interface SearchService {
    /**
     * Method that allow you to search {@link SearchResponseDto}.
     *
     * @param searchQuery query to search
     * @return {@link SearchResponseDto}
     */
    SearchResponseDto search(String searchQuery);

    /**
     * Method that allow you to search all results in {@link SearchResponseDto} by page .
     *
     * @param page             page of request
     * @param searchRequestDto search query and sort method
     * @return {@link SearchResponseDto} with results of search
     */
    SearchResponseDto searchAll(Pageable page, SearchRequestDto searchRequestDto);
}
