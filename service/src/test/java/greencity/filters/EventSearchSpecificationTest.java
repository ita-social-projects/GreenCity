package greencity.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import greencity.entity.event.Event;
import greencity.entity.event.Event_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventSearchSpecificationTest {

    @Mock
    private CriteriaQuery<?> criteriaQueryMock;
    @Mock
    private CriteriaBuilder criteriaBuilderMock;
    @Mock
    private Predicate expected;
    @Mock
    private Root<Event> rootMock;
    @Mock
    private Path<String> stringPathMock;

    private final Long userId = 1L;
    private EventSearchSpecification eventSearchSpecification;

    @Test
    void toPredicateTest() {
        String text = "test";
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(text);
        eventSearchSpecification = new EventSearchSpecification(searchCriteriaList, userId);

        doReturn(stringPathMock).when(rootMock).get(Event_.TITLE);
        doReturn(stringPathMock).when(rootMock).get(Event_.DESCRIPTION);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.lower(stringPathMock)).thenReturn(stringPathMock);
        when(criteriaBuilderMock.like(stringPathMock, String.format("%%%s%%", text.toLowerCase())))
            .thenReturn(expected);
        when(criteriaBuilderMock.or(expected, expected)).thenReturn(expected);
        when(criteriaBuilderMock.or(any(Predicate[].class))).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);

        Predicate predicate = eventSearchSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).distinct(true);
    }

    @Test
    void toPredicateWithAllEmptyValuesTest() {
        String emptyValue = "";
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(emptyValue);
        eventSearchSpecification = new EventSearchSpecification(searchCriteriaList, userId);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);

        Predicate predicate = eventSearchSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).distinct(true);
    }

    @Test
    void toPredicateWithEmptyCriteriaTest() {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        eventSearchSpecification = new EventSearchSpecification(searchCriteriaList, userId);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);

        Predicate predicate = eventSearchSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).distinct(true);
        verify(criteriaBuilderMock, times(2)).conjunction();
    }

    private List<SearchCriteria> createSearchCriteriaList(String text) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        SpecificationTestUtils.setValue(searchCriteriaList, "text", text);
        return searchCriteriaList;
    }
}
