package greencity.repositories;

import greencity.entities.FavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritePlaceRepo extends JpaRepository<FavoritePlace,Long> {
}
