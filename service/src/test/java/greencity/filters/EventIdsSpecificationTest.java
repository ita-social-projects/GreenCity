package greencity.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import greencity.entity.User;
import greencity.entity.User_;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventDateLocation_;
import greencity.entity.event.Event_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventIdsSpecificationTest {

    @Mock
    private CriteriaQuery<?> criteriaQueryMock;
    @Mock
    private CriteriaBuilder criteriaBuilderMock;
    @Mock
    private Predicate expected;
    @Mock
    private Root<Event> rootMock;
    @Mock
    private Order orderMock;
    @Mock
    private Path<String> stringPathMock;
    @Mock
    private Path<ZonedDateTime> datePathMock;
    @Mock
    private Path<Long> longPathMock;
    @Mock
    private CriteriaBuilder.Case<Object> caseObjectMock;
    @Mock
    private CriteriaBuilder.Case<Date> caseDateMock;
    @Mock
    private Join<Event, EventDateLocation> datesJoin;
    @Mock
    private Join<Event, User> usersJoin;

    private EventIdsSpecification eventIdsSpecification;

    @Test
    void toPredicateWithUserIdTest() {
        Long userId = 1L;
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        eventIdsSpecification = new EventIdsSpecification(searchCriteriaList, userId);

        doReturn(longPathMock).when(rootMock).get(Event_.ID);
        doReturn(stringPathMock).when(rootMock).get(Event_.ORGANIZER);
        doReturn(longPathMock).when(stringPathMock).get(User_.ID);
        doReturn(datesJoin).when(rootMock).join(Event_.DATES, JoinType.LEFT);
        doReturn(usersJoin).when(rootMock).join(Event_.ATTENDERS, JoinType.LEFT);
        doReturn(usersJoin).when(rootMock).join(Event_.FOLLOWERS, JoinType.LEFT);
        doReturn(datePathMock).when(datesJoin).get(EventDateLocation_.START_DATE);
        doReturn(longPathMock).when(usersJoin).get(User_.ID);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilderMock.equal(longPathMock, userId)).thenReturn(expected);
        when(criteriaBuilderMock.greaterThanOrEqualTo(eq(datePathMock), any(ZonedDateTime.class)))
            .thenReturn(expected);
        when(criteriaBuilderMock.lessThanOrEqualTo(eq(datePathMock), any(ZonedDateTime.class)))
            .thenReturn(expected);
        when(criteriaBuilderMock.selectCase()).thenReturn(caseObjectMock);
        when(caseObjectMock.when(expected, 1)).thenReturn(caseObjectMock);
        when(caseObjectMock.otherwise(0)).thenReturn(caseObjectMock);
        when(criteriaBuilderMock.desc(caseObjectMock)).thenReturn(orderMock);
        when(criteriaBuilderMock.currentDate()).thenReturn(caseDateMock);
        when(criteriaBuilderMock.function("DATE", Date.class, datePathMock)).thenReturn(caseDateMock);
        when(criteriaBuilderMock.function("DATE", Date.class, caseDateMock)).thenReturn(caseDateMock);
        when(criteriaBuilderMock.equal(caseDateMock, caseDateMock)).thenReturn(expected);

        ArgumentCaptor<List<Order>> orderCaptor = ArgumentCaptor.forClass(List.class);
        Predicate predicate = eventIdsSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).orderBy(orderCaptor.capture());
        assertEquals(6, orderCaptor.getValue().size());
    }

    @Test
    void toPredicateWithoutUserIdTest() {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        eventIdsSpecification = new EventIdsSpecification(searchCriteriaList, null);

        doReturn(longPathMock).when(rootMock).get(Event_.ID);
        doReturn(datesJoin).when(rootMock).join(Event_.DATES, JoinType.LEFT);
        doReturn(datePathMock).when(datesJoin).get(EventDateLocation_.START_DATE);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilderMock.greaterThanOrEqualTo(eq(datePathMock), any(ZonedDateTime.class)))
            .thenReturn(expected);
        when(criteriaBuilderMock.lessThanOrEqualTo(eq(datePathMock), any(ZonedDateTime.class)))
            .thenReturn(expected);
        when(criteriaBuilderMock.selectCase()).thenReturn(caseObjectMock);
        when(caseObjectMock.when(expected, 1)).thenReturn(caseObjectMock);
        when(caseObjectMock.otherwise(0)).thenReturn(caseObjectMock);
        when(criteriaBuilderMock.desc(caseObjectMock)).thenReturn(orderMock);
        when(criteriaBuilderMock.currentDate()).thenReturn(caseDateMock);
        when(criteriaBuilderMock.function("DATE", Date.class, datePathMock)).thenReturn(caseDateMock);
        when(criteriaBuilderMock.function("DATE", Date.class, caseDateMock)).thenReturn(caseDateMock);
        when(criteriaBuilderMock.equal(caseDateMock, caseDateMock)).thenReturn(expected);

        ArgumentCaptor<List<Order>> orderCaptor = ArgumentCaptor.forClass(List.class);
        Predicate predicate = eventIdsSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).orderBy(orderCaptor.capture());
        assertEquals(3, orderCaptor.getValue().size());
    }
}
