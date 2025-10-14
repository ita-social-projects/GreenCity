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
import greencity.enums.EventStatus;
import greencity.enums.EventTime;
import greencity.enums.EventType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    private Join<Event, Tag> tagsJoin;
    @Mock
    private Join<Tag, TagTranslation> tagTranslationsJoin;

    private final Long userId = 1L;
    private EventSpecification eventSpecification;

    @ParameterizedTest
    @CsvSource(value = {
        "UPCOMING;test;OPEN,CLOSED,JOINED;test;test;ONLINE;2011-12-03T10:15:30+01:00;true;1",
        "PAST;test;CREATED,SAVED;test;test;OFFLINE;2011-12-03T10:15:30+01:00,2011-12-04T10:15:30+01:00;false;1",
        "UPCOMING;test;JOINED,CREATED,SAVED;test;test;ONLINE_OFFLINE;2011-12-03T10:15:30+01:00;true;null"
    }, delimiter = ';', nullValues = "null")
    void toPredicateTest(EventTime eventTime, String cities, String statuses, String tags, String title,
        EventType type, String dateRange, Boolean isFavorite, Long userId) {
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(eventTime, toStringArray(cities),
            toEventStatusArray(statuses), toStringArray(tags), title, type, toZonedDateTimeArray(dateRange),
            isFavorite);
        eventSpecification = new EventSpecification(searchCriteriaList, userId);

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
        when(criteriaBuilderMock.equal(longPathMock, userId)).thenReturn(expected);
        when(criteriaBuilderMock.notEqual(longPathMock, userId)).thenReturn(expected);

        when(stringPathMock.in(any(Object[].class))).thenReturn(expected);

        Predicate predicate = eventSpecification
            .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
    }

    @Test
    void toPredicateWithAllNullValuesTest() {
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(null, null, null,
            null, null, null, null, null);
        eventSpecification = new EventSpecification(searchCriteriaList, userId);

        when(criteriaBuilderMock.conjunction()).thenReturn(expected);
        when(criteriaBuilderMock.and(expected, expected)).thenReturn(expected);

        Predicate predicate = eventSpecification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        assertEquals(expected, predicate);
    }

    @Test
    void toPredicateWithAllEmptyValuesTest() {
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(null, new String[0],
            new EventStatus[0], new String[0], "", null, new ZonedDateTime[0], null);
        eventSpecification = new EventSpecification(searchCriteriaList, null);

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

    private List<SearchCriteria> createSearchCriteriaList(EventTime eventTime, String[] cities, EventStatus[] statuses,
        String[] tags, String title, EventType type, ZonedDateTime[] dateRange, Boolean isFavorite) {
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

    private String[] toStringArray(String stringValue) {
        return stringValue.split(",");
    }

    private EventStatus[] toEventStatusArray(String stringValue) {
        return Arrays.stream(stringValue.split(","))
            .map(EventStatus::valueOf)
            .toArray(EventStatus[]::new);
    }

    private ZonedDateTime[] toZonedDateTimeArray(String stringValue) {
        return Arrays.stream(stringValue.split(","))
            .map(ZonedDateTime::parse)
            .toArray(ZonedDateTime[]::new);
    }
}
