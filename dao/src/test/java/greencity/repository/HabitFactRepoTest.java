package greencity.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.DaoApplication;
import greencity.entity.HabitFact;
import greencity.utils.ModelUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/habit_fact_translation.sql")
class HabitFactRepoTest {

    @Autowired
    private HabitFactRepo habitFactRepo;

    @Test
    void searchByTest() {
        Pageable pageable = PageRequest.of(0, 2);
        String query = "номер";
        List<HabitFact> habitFacts = List.of(ModelUtils.getHabitFact());

        Page<HabitFact> actual = new PageImpl<>(habitFacts, pageable, habitFacts.size());
        Page<HabitFact> expected = habitFactRepo.searchHabitFactByFilter(pageable, query);

        List<Long> actualIds = actual.getContent().stream().map(HabitFact::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expected.getContent().stream().map(HabitFact::getId)
            .collect(Collectors.toList());

        assertEquals(1, expected.getContent().size());
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void findAllByHabitIdTest() {
        Long habitId = 1L;
        List<HabitFact> actual = List.of(ModelUtils.getHabitFact());
        List<HabitFact> expected = habitFactRepo.findAllByHabitId(habitId);

        List<Long> actualIds = actual.stream().map(HabitFact::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expected.stream().map(HabitFact::getId)
            .collect(Collectors.toList());

        assertEquals(expected.size(), actual.size());
        assertEquals(expectedIds, actualIds);
    }
}
