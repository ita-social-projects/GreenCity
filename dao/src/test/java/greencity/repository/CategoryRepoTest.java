package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/category.sql")
class CategoryRepoTest {
    @Autowired
    private CategoryRepo categoryRepo;

    @Test
    void findTest() {
        Category category = categoryRepo.findByName("Food");
        assertEquals(1L, category.getId());
    }
}
