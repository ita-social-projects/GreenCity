package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.DiscountValue;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/discount_values.sql")
class DiscountValuesRepoTest {
    @Autowired
    private DiscountValuesRepo discountValuesRepo;

    @Test
    void findAllTest() {
        Set<DiscountValue> discountValues = discountValuesRepo.findAllByPlaceId(5L);
        List<Long> discountIds =
            discountValues.stream()
                .map(DiscountValue::getId)
                .collect(Collectors.toList());

        assertEquals(5L, discountIds.get(0));
        assertEquals(6L, discountIds.get(1));
    }

    @Test
    void deleteAllTest() {
        discountValuesRepo.deleteAllByPlaceId(1L);
        Set<DiscountValue> discountValues = discountValuesRepo.findAllByPlaceId(1L);

        assertTrue(discountValues.isEmpty());
    }
}
