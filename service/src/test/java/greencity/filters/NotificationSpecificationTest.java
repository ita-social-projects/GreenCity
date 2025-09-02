package greencity.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import greencity.entity.Notification;
import greencity.entity.Notification_;
import greencity.entity.User;
import greencity.entity.User_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationSpecificationTest {

    @Mock
    private CriteriaQuery<?> criteriaQueryMock;
    @Mock
    private CriteriaBuilder criteriaBuilderMock;
    @Mock
    private Predicate expected;
    @Mock
    private Root<Notification> rootMock;
    @Mock
    private Order orderMock;
    @Mock
    private Path<String> stringPathMock;
    @Mock
    private Path<ZonedDateTime> datePathMock;
    @Mock
    private Path<Long> longPathMock;
    @Mock
    private Join<Notification, User> usersJoin;

    private NotificationSpecification notificationSpecification;

    @Test
    void toPredicateTest() {
        String userId = "1";
        String type = "test";
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(userId, type);
        notificationSpecification = new NotificationSpecification(searchCriteriaList);

        doReturn(datePathMock).when(rootMock).get(Notification_.TIME);
        doReturn(usersJoin).when(rootMock).join(Notification_.TARGET_USER);
        doReturn(longPathMock).when(usersJoin).get(User_.ID);
        doReturn(stringPathMock).when(rootMock).get(Notification_.NOTIFICATION_TYPE);
        doReturn(stringPathMock).when(stringPathMock).as(String.class);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilderMock.or(any(Predicate[].class))).thenReturn(expected);
        when(criteriaBuilderMock.desc(datePathMock)).thenReturn(orderMock);
        when(criteriaBuilderMock.equal(longPathMock, userId)).thenReturn(expected);
        when(criteriaBuilderMock.equal(stringPathMock, '%' + type + '%')).thenReturn(expected);

        Predicate predicate = notificationSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).orderBy(any(Order.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", ","})
    void toPredicateWithAllEmptyValuesTest(String emptyArray) {
        String emptyValue = "";
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(emptyValue, emptyArray);
        notificationSpecification = new NotificationSpecification(searchCriteriaList);

        doReturn(datePathMock).when(rootMock).get(Notification_.TIME);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilderMock.desc(datePathMock)).thenReturn(orderMock);

        Predicate predicate = notificationSpecification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).orderBy(any(Order.class));
    }

    @Test
    void toPredicateWithEmptyCriteriaTest() {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        notificationSpecification = new NotificationSpecification(searchCriteriaList);

        doReturn(datePathMock).when(rootMock).get(Notification_.TIME);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.desc(datePathMock)).thenReturn(orderMock);

        Predicate predicate = notificationSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaQueryMock).orderBy(any(Order.class));
        verify(criteriaBuilderMock, times(1)).conjunction();
    }

    private List<SearchCriteria> createSearchCriteriaList(String userId, String type) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        SpecificationTestUtils.setValue(searchCriteriaList, Notification_.TARGET_USER, userId);
        SpecificationTestUtils.setValue(searchCriteriaList, Notification_.NOTIFICATION_TYPE, type);
        return searchCriteriaList;
    }
}
