package greencity.repository;

import greencity.entity.FavoritePlace;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("classpath:sql/favorite_place.sql")
class FavoritePlaceRepoTest {

    @Autowired
    FavoritePlaceRepo favoritePlaceRepo;

    @Test
    void findAllByUserEmailTest() {
        List<FavoritePlace> placeList = favoritePlaceRepo.findAllByUserEmail("test@email.com");
        assertEquals(2, placeList.size());
        assertEquals(1, placeList.get(0).getId());
    }

    @Test
    void findByPlaceIdAndUserEmailTest() {
        FavoritePlace place = favoritePlaceRepo.findByPlaceIdAndUserEmail(1L, "test@email.com");
        assertEquals(1, place.getId());
        assertEquals("Favorite place", place.getName());
    }

    @Test
    void findByPlaceIdTest() {
        FavoritePlace place = favoritePlaceRepo.findByPlaceId(1L);
        assertEquals(1, place.getId());
        assertEquals("Favorite place", place.getName());
    }
}
