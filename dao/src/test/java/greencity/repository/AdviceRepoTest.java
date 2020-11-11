package greencity.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.DaoApplication;
import greencity.ModelUtils;
import greencity.entity.Advice;
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
@Sql("classpath:sql/advice_translation.sql")
class AdviceRepoTest {
    @Autowired
    private AdviceRepo adviceRepo;

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 3);
        List<Advice> advices = ModelUtils.getAdvices();
        Page<Advice> actual = new PageImpl<>(advices, pageable, advices.size());
        Page<Advice> expected = adviceRepo.findAll(pageable);

        List<Long> actualIds = actual.getContent().stream().map(Advice::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expected.getContent().stream().map(Advice::getId)
            .collect(Collectors.toList());

        assertEquals(expected.getContent().size(), 3);
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void searchBy() {
        Pageable pageable = PageRequest.of(0, 2);
        String query = "При";
        List<Advice> advices = ModelUtils.getAdvices();
        advices.remove(1);

        Page<Advice> actual = new PageImpl<>(advices, pageable, advices.size());
        Page<Advice> expected = adviceRepo.filterByAllFields(pageable, query);

        List<Long> actualIds = actual.getContent().stream().map(Advice::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expected.getContent().stream().map(Advice::getId)
            .collect(Collectors.toList());

        assertEquals(expected.getContent().size(), 2);
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void findAllByHabitId() {
        Long habitId = 1L;
        List<Advice> actual = List.of(ModelUtils.getAdvice());
        List<Advice> expected = adviceRepo.findAllByHabitId(habitId);

        List<Long> actualIds = actual.stream().map(Advice::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expected.stream().map(Advice::getId)
            .collect(Collectors.toList());

        assertEquals(expected.size(), actual.size());
        assertEquals(expectedIds, actualIds);
    }
}
