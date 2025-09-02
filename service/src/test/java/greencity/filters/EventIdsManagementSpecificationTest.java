package greencity.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
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
class EventIdsManagementSpecificationTest {

    @Mock
    private CriteriaQuery<?> criteriaQueryMock;
    @Mock
    private CriteriaBuilder criteriaBuilderMock;
    @Mock
    private Predicate expected;
    @Mock
    private Root<Event> rootMock;
    @Mock
    private Path<Long> longPathMock;

    @Test
    void toPredicateTest() {
        Long userId = 1L;
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        EventIdsManagementSpecification eventIdsManagementSpecification =
            new EventIdsManagementSpecification(searchCriteriaList, userId);

        doReturn(longPathMock).when(rootMock).get(Event_.ID);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);

        Predicate predicate = eventIdsManagementSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).multiselect(longPathMock);
    }
}
