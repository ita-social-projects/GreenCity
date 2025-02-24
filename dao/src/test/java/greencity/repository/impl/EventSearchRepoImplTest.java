package greencity.repository.impl;

import greencity.ModelUtils;
import greencity.dto.filter.FilterEventDto;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventDateLocation_;
import greencity.entity.event.Event_;
import greencity.enums.EventTime;
import greencity.repository.util.PostgresInitializer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventSearchRepoImplTest extends PostgresInitializer {
    private final EventSearchRepoImpl eventSearchRepo;
    private final EntityManager entityManager;
    private final CriteriaBuilder builder;

    private static final ModelMapper MAPPER = new ModelMapper();
    private static final Pageable PAGE = PageRequest.of(0, 1);
    private static final String TRUNCATE_SQL = "TRUNCATE TABLE events RESTART IDENTITY CASCADE";

    @Autowired
    public EventSearchRepoImplTest(EventSearchRepoImpl eventSearchRepo,
        EntityManager entityManager) {
        this.eventSearchRepo = eventSearchRepo;
        this.entityManager = entityManager;
        this.builder = entityManager.getCriteriaBuilder();
    }

    @BeforeAll
    static void startContainer() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        postgreSQLContainer.stop();
    }

    @BeforeEach
    void setup() {
        ModelUtils.getListEventDto().forEach(dto -> {
            var event = MAPPER.map(dto, Event.class);
            event.getDates().forEach(date -> date.setEvent(event));
            entityManager.merge(event);
        });
        entityManager.flush();
    }

    @AfterEach
    void teardown() {
        entityManager.createNativeQuery(TRUNCATE_SQL).executeUpdate();
    }

    @Nested
    class EventTimeTests {
        private CriteriaQuery<Long> query;
        private Root<Event> root;
        private ListJoin<Event, EventDateLocation> datesJoin;

        @BeforeEach
        void setup() {
            this.query = builder.createQuery(Long.class);
            this.root = query.from(Event.class);
            this.datesJoin = root.join(Event_.dates, JoinType.LEFT);
        }

        @Test
        void findEventIdsForUpcomingEventReturnsIds() {
            query.select(root.get(Event_.ID)).where(builder.greaterThan(
                datesJoin.get(EventDateLocation_.FINISH_DATE), ZonedDateTime.now()));

            var expectedRes = entityManager.createQuery(query).getResultList();
            assertFalse(expectedRes.isEmpty());

            var res = eventSearchRepo.findEventsIds(PAGE, createEventFilterDto(EventTime.UPCOMING), 2L);
            assertFalse(res.isEmpty());

            assertIterableEquals(expectedRes, res);
        }

        @Test
        void findEventIdsForPastEventReturnsIds() {
            query.select(root.get(Event_.ID)).where(builder.lessThan(
                datesJoin.get(EventDateLocation_.FINISH_DATE), ZonedDateTime.now()));

            var expectedRes = entityManager.createQuery(query).getResultList();
            assertFalse(expectedRes.isEmpty());

            var res = eventSearchRepo.findEventsIds(PAGE, createEventFilterDto(EventTime.PAST), 2L);
            assertFalse(res.isEmpty());

            assertIterableEquals(expectedRes, res.toList());
        }
    }

    private static FilterEventDto createEventFilterDto(EventTime time) {
        return FilterEventDto.builder()
            .time(time)
            .build();
    }
}
