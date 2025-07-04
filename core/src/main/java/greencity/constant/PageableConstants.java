package greencity.constant;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageableConstants {
    public static final Integer DEFAULT_PAGE = 0;
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final Sort DEFAULT_SORT = Sort.unsorted();
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String SORT = "sort";
    public static final Integer MAX_PAGE_SIZE = 100;
}
