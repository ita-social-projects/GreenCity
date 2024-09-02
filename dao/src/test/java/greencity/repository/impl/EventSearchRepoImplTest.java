package greencity.repository.impl;

import greencity.dto.filter.FilterEventDto;
import greencity.entity.Tag;
import greencity.entity.Tag_;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventSearchRepoImplTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @InjectMocks
    private EventSearchRepoImpl eventSearchRepo;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eventSearchRepo, "entityManager", entityManager);
        ReflectionTestUtils.setField(eventSearchRepo, "criteriaBuilder", criteriaBuilder);
    }

    @Test
    void findEventsIdsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        FilterEventDto filterEventDto = FilterEventDto.builder()
            .time(EventTime.PAST)
            .cities(List.of("Kyiv"))
            .statuses(List.of(EventStatus.OPEN, EventStatus.CLOSED, EventStatus.JOINED, EventStatus.CREATED,
                EventStatus.SAVED))
            .tags(List.of("SOCIAL"))
            .title("111")
            .type(EventType.ONLINE)
            .build();

        CriteriaQuery<Long> criteriaQuery = mock(CriteriaQuery.class);
        Root<Event> eventRoot = mock(Root.class);
        ListJoin<Event, EventDateLocation> datesJoin = mock(ListJoin.class);
        ListJoin<Tag, TagTranslation> tagsTranslationJoin = mock(ListJoin.class);
        Join<EventDateLocation, Address> addressJoin = mock(Join.class);
        ListJoin<Event, Tag> tagsJoin = mock(ListJoin.class);
        TypedQuery<Long> typedQuery = mock(TypedQuery.class);
        CriteriaBuilder.Case<Object> selectCase = mock(CriteriaBuilder.Case.class);
        Path<Object> city = mock(Path.class);
        Path<Object> tagName = mock(Path.class);

        when(criteriaBuilder.createQuery(Long.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Event.class)).thenReturn(eventRoot);

        when(criteriaQuery.select(eventRoot.get(Event_.ID))).thenReturn(criteriaQuery);
        when(eventRoot.join(Event_.dates, JoinType.LEFT)).thenReturn(datesJoin);
        when(eventRoot.join(Event_.dates, JoinType.LEFT)).thenReturn(datesJoin);
        when(eventRoot.join(Event_.dates, JoinType.LEFT).join(EventDateLocation_.address)).thenReturn(addressJoin);
        when(addressJoin.get(Address_.CITY_EN)).thenReturn(city);
        when(eventRoot.join(Event_.tags)).thenReturn(tagsJoin);
        when(tagsJoin.join(Tag_.tagTranslations)).thenReturn(tagsTranslationJoin);
        when(tagsTranslationJoin.get(TagTranslation_.NAME)).thenReturn(tagName);

        when(criteriaQuery.where((Predicate) null)).thenReturn(criteriaQuery);

        when(criteriaBuilder.selectCase()).thenReturn(selectCase);
        when(selectCase.when(any(), anyInt())).thenReturn(selectCase);
        when(selectCase.otherwise(anyInt())).thenReturn(selectCase);
        when(criteriaBuilder.function(eq("DATE"), eq(Date.class), any())).thenReturn(mock(Expression.class));
        when(criteriaBuilder.currentDate()).thenReturn(mock(Expression.class));

        List<Long> eventIds = List.of(1L, 2L, 3L);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(eventIds);

        Page<Long> result = eventSearchRepo.findEventsIds(pageable, filterEventDto, null);

        assertEquals(3, result.getTotalElements());
    }
}