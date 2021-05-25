package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.Photo;
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
@Sql("classpath:sql/photo.sql")
class PhotoRepoTest {
    @Autowired
    private PhotoRepo photoRepo;

    @Test
    void findTest() {
        Photo photo = photoRepo.findByName("image").get();
        assertEquals(1L, photo.getId());
    }
}
