package greencity.service;

import greencity.dto.search.SearchResponseDto;

/**
 * Provides the interface to manage search functionality.
 *
 * @author Kovaliv Taras
 * @author Zhurakovskyi Yurii
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
     * Method that allow you to search all {@link SearchResponseDto}.
     *
     * @param searchQuery query to search all
     * @return {@link SearchResponseDto}
     */
    SearchResponseDto searchAll(String searchQuery);
}
