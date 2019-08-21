package greencity.repository;

import greencity.entity.FavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritePlaceRepo extends JpaRepository<FavoritePlace, Long> {
    List<FavoritePlace> findAllByUserId (Long id);
}
