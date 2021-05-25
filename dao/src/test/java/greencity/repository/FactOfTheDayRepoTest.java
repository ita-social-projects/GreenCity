package greencity.repository;

import greencity.entity.FactOfTheDay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("classpath:sql/fact_of_the_day.sql")
class FactOfTheDayRepoTest {

    @Autowired
    FactOfTheDayRepo factOfTheDayRepo;

    @Test
    void searchByTest() {
        Pageable pageable = PageRequest.of(0, 2);
        FactOfTheDay factOfTheDay = factOfTheDayRepo.searchBy(pageable, "trans")
            .get()
            .findFirst()
            .get();
        assertEquals(1, factOfTheDay.getId());
    }

    @Test
    void getRandomFactOfTheDay() {
        FactOfTheDay factOfTheDay = factOfTheDayRepo.getRandomFactOfTheDay().get();
        assertEquals(1, factOfTheDay.getId());
    }
}
