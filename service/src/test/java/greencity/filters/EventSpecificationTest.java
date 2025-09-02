package greencity.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import greencity.entity.Tag;
import greencity.entity.Tag_;
import greencity.entity.User;
import greencity.entity.User_;
import greencity.entity.event.Address;
import greencity.entity.event.Address_;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventDateLocation_;
import greencity.entity.event.Event_;
import greencity.entity.localization.TagTranslation;
import greencity.entity.localization.TagTranslation_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventSpecificationTest {

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
    @Mock
    private Path<Boolean> booleanPathMock;
    @Mock
    private Path<ZonedDateTime> datePathMock;
    @Mock
    private Path<Long> longPathMock;
    @Mock
    private Join<Event, EventDateLocation> datesJoin;
    @Mock
    private Join<EventDateLocation, Address> addressJoin;
    @Mock
    private Join<Event, User> usersJoin;
    @Mock
    private Join<Tag, Tag> tagsJoin;
    @Mock
    private Join<Tag, TagTranslation> tagTranslationsJoin;

    private final Long userId = 1L;
    private EventSpecification eventSpecification;

    @ParameterizedTest
    @CsvSource(value = {
        "UPCOMING;test;OPEN,CLOSED,JOINED,CREATED,SAVED;test;test;ONLINE;2011-12-03T10:15:30+01:00;true;1",
        "PAST;test;UNKNOWN;test;test;OFFLINE;2011-12-03T10:15:30+01:00,2011-12-04T10:15:30+01:00;false;1",
        "else;test;JOINED,CREATED,SAVED;test;test;ONLINE_OFFLINE;2011-12-03T10:15:30+01:00;true;"
    }, delimiter = ';')
    void toPredicateTest(String eventTime, String cities, String statuses, String tags, String title,
        String type, String dateRange, String isFavorite, String userId) {
        Long id = userId == null ? null : Long.parseLong(userId);
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(eventTime, cities, statuses, tags,
            title, type, dateRange, isFavorite);
        eventSpecification = new EventSpecification(searchCriteriaList, id);

        doReturn(datesJoin).when(rootMock).join(Event_.DATES, JoinType.LEFT);
        doReturn(addressJoin).when(datesJoin).join(EventDateLocation_.ADDRESS);
        doReturn(usersJoin).when(rootMock).join(Event_.ORGANIZER, JoinType.LEFT);
        doReturn(usersJoin).when(rootMock).join(Event_.ATTENDERS, JoinType.LEFT);
        doReturn(usersJoin).when(rootMock).join(Event_.FOLLOWERS, JoinType.LEFT);
        doReturn(usersJoin).when(rootMock).join(Event_.FOLLOWERS);
        doReturn(tagsJoin).when(rootMock).join(Event_.TAGS);
        doReturn(tagTranslationsJoin).when(tagsJoin).join(Tag_.TAG_TRANSLATIONS);

        doReturn(stringPathMock).when(rootMock).get("eventTime");
        doReturn(stringPathMock).when(rootMock).get("cities");
        doReturn(stringPathMock).when(rootMock).get("statuses");
        doReturn(stringPathMock).when(rootMock).get(Event_.TAGS);
        doReturn(stringPathMock).when(rootMock).get(Event_.TITLE);
        doReturn(stringPathMock).when(rootMock).get(Event_.TYPE);
        doReturn(stringPathMock).when(rootMock).get("statuses");
        doReturn(stringPathMock).when(rootMock).get("statuses");
        doReturn(stringPathMock).when(rootMock).get("dateRange");
        doReturn(stringPathMock).when(rootMock).get("isFavorite");
        doReturn(booleanPathMock).when(rootMock).get(Event_.IS_OPEN);
        doReturn(datePathMock).when(datesJoin).get(EventDateLocation_.START_DATE);
        doReturn(datePathMock).when(datesJoin).get(EventDateLocation_.FINISH_DATE);
        doReturn(stringPathMock).when(addressJoin).get(Address_.CITY_EN);
        doReturn(stringPathMock).when(addressJoin).get(Address_.CITY_UK);
        doReturn(stringPathMock).when(tagTranslationsJoin).get(TagTranslation_.NAME);
        doReturn(longPathMock).when(usersJoin).get(User_.ID);

        when(stringPathMock.as(String.class)).thenReturn(stringPathMock);
        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.disjunction()).thenReturn(expected);
        when(criteriaBuilderMock.like(stringPathMock, String.format("%%%s%%", title)))
            .thenReturn(expected);
        when(criteriaBuilderMock.like(stringPathMock, String.format("%%%s%%", type)))
            .thenReturn(expected);
        when(criteriaBuilderMock.greaterThan(eq(datePathMock), any(ZonedDateTime.class))).thenReturn(expected);
        when(criteriaBuilderMock.lessThan(eq(datePathMock), any(ZonedDateTime.class))).thenReturn(expected);
        when(criteriaBuilderMock.greaterThanOrEqualTo(eq(datePathMock), any(ZonedDateTime.class)))
            .thenReturn(expected);
        when(criteriaBuilderMock.lessThanOrEqualTo(eq(datePathMock), any(ZonedDateTime.class)))
            .thenReturn(expected);

        when(criteriaBuilderMock.upper(stringPathMock)).thenReturn(stringPathMock);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilderMock.or(expected, expected)).thenReturn(expected);
        when(criteriaBuilderMock.or(expected)).thenReturn(expected);
        when(criteriaBuilderMock.or(any(Predicate[].class))).thenReturn(expected);
        when(criteriaBuilderMock.isTrue(booleanPathMock)).thenReturn(expected);
        when(criteriaBuilderMock.isFalse(booleanPathMock)).thenReturn(expected);
        when(criteriaBuilderMock.equal(longPathMock, id)).thenReturn(expected);
        when(criteriaBuilderMock.notEqual(longPathMock, id)).thenReturn(expected);

        when(stringPathMock.in(any(Object[].class))).thenReturn(expected);

        Predicate predicate = eventSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", ","})
    void toPredicateWithAllEmptyValuesTest(String emptyArray) {
        String emptyValue = "";
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(emptyValue, emptyArray, emptyArray,
            emptyArray, emptyValue, emptyValue, emptyArray, emptyValue);
        eventSpecification = new EventSpecification(searchCriteriaList, userId);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);

        Predicate predicate = eventSpecification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
    }

    @Test
    void toPredicateWithEmptyCriteriaTest() {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        eventSpecification = new EventSpecification(searchCriteriaList, userId);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);

        Predicate predicate = eventSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
        verify(criteriaBuilderMock, times(1)).conjunction();
    }

    private List<SearchCriteria> createSearchCriteriaList(String eventTime, String cities, String statuses,
        String tags, String title, String type, String dateRange, String isFavorite) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        SpecificationTestUtils.setValue(searchCriteriaList, "eventTime", eventTime);
        SpecificationTestUtils.setValue(searchCriteriaList, "cities", cities);
        SpecificationTestUtils.setValue(searchCriteriaList, "statuses", statuses);
        SpecificationTestUtils.setValue(searchCriteriaList, Event_.TAGS, tags);
        SpecificationTestUtils.setValue(searchCriteriaList, Event_.TITLE, title);
        SpecificationTestUtils.setValue(searchCriteriaList, Event_.TYPE, type);
        SpecificationTestUtils.setValue(searchCriteriaList, "dateRange", dateRange);
        SpecificationTestUtils.setValue(searchCriteriaList, "isFavorite", isFavorite);
        return searchCriteriaList;
    }
}
