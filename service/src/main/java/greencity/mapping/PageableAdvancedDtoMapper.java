package greencity.mapping;

import greencity.dto.PageableAdvancedDto;
import org.modelmapper.AbstractConverter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageableAdvancedDtoMapper<T> extends AbstractConverter<Page<T>, PageableAdvancedDto<T>> {
    @Override
    public PageableAdvancedDto<T> convert(Page<T> page) {
        return new PageableAdvancedDto<>(
            page.toList(),
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages(),
            page.getNumber(),
            page.hasPrevious(),
            page.hasNext(),
            page.isFirst(),
            page.isLast());
    }
}
