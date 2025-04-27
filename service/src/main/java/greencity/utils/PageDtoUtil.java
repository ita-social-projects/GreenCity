package greencity.utils;

import greencity.dto.PageableAdvancedDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Utility class for converting {@link Page} objects into
 * {@link PageableAdvancedDto}.
 */
@Component
public class PageDtoUtil {
    /**
     * Converts a Spring Data {@link Page} into a {@link PageableAdvancedDto} to be
     * used in responses.
     *
     * @param page the page of data to convert
     * @param <T>  the type of content inside the page
     * @return a {@link PageableAdvancedDto} with pagination metadata and content
     */
    public <T> PageableAdvancedDto<T> toPageableDto(Page<T> page) {
        return new PageableAdvancedDto<>(
            page.getContent(), page.getTotalElements(),
            page.getNumber(), page.getTotalPages(),
            page.getNumberOfElements(), page.hasPrevious(),
            page.hasNext(), page.isFirst(), page.isLast());
    }
}
