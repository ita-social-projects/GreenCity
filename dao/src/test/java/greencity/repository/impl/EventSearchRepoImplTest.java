package greencity.repository.impl;

import greencity.dto.filter.FilterEventDto;
import greencity.repository.util.PostgresInitializer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class EventSearchRepoImplTest extends PostgresInitializer {
    private final EventSearchRepoImpl eventSearchRepo;

    @Autowired
    public EventSearchRepoImplTest(EventSearchRepoImpl eventSearchRepo) {
        this.eventSearchRepo = eventSearchRepo;
    }

    @BeforeAll
    static void startContainer() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        postgreSQLContainer.stop();
    }

    @Test
    public void findEventIdsTest() {
        Pageable page = PageRequest.of(0, 2);
        var filter = FilterEventDto.builder().cities(List.of("Kyiv")).build();
        var res = eventSearchRepo.findEventsIds(page, filter, 2L);
        assertFalse(res.isEmpty());
    }
}
