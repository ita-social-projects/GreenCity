package greencity.service;

import greencity.dto.search.SearchResponseDto;

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
}
