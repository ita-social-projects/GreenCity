package greencity.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageableAdvancedDto<T> {
    private List<T> page;

    private long totalElements;

    private int currentPage;

    private int totalPages;

    private int number;

    private boolean hasPrevious;

    private boolean hasNext;

    private boolean first;

    private boolean last;
}
