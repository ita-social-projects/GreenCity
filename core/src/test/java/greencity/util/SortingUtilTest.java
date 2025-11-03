package greencity.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortingUtilTest {

    @Test
    void buildSortingUrlWithEmptySortTest() {
        Sort sort = Sort.unsorted();
        String result = SortingUtil.buildSortingUrl(sort);
        assertEquals("", result);
    }

    @Test
    void buildSortingUrlWithSingleSortTest() {
        Sort sort = Sort.by(Sort.Direction.ASC, "title");
        String result = SortingUtil.buildSortingUrl(sort);
        assertEquals("title,ASC", result);
    }

    @Test
    void buildSortingUrlWithSingleSortDescTest() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        String result = SortingUtil.buildSortingUrl(sort);
        assertEquals("id,DESC", result);
    }

    @Test
    void buildSortingUrlWithMultipleSortTest() {
        Sort sort = Sort.by(Sort.Direction.ASC, "title")
            .and(Sort.by(Sort.Direction.DESC, "id"));
        String result = SortingUtil.buildSortingUrl(sort);
        assertEquals("title,ASC&sort=id,DESC", result);
    }

    @Test
    void buildSortingUrlWithMultipleSortSameDirectionTest() {
        Sort sort = Sort.by(Sort.Direction.ASC, "title")
            .and(Sort.by(Sort.Direction.ASC, "createdDate"));
        String result = SortingUtil.buildSortingUrl(sort);
        assertEquals("title,ASC&sort=createdDate,ASC", result);
    }

    @Test
    void buildSortingUrlWithThreeSortPropertiesTest() {
        Sort sort = Sort.by(Sort.Direction.ASC, "title")
            .and(Sort.by(Sort.Direction.DESC, "id"))
            .and(Sort.by(Sort.Direction.ASC, "status"));
        String result = SortingUtil.buildSortingUrl(sort);
        assertEquals("title,ASC&sort=id,DESC&sort=status,ASC", result);
    }
}