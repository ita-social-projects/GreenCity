package greencity.repository;

import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritePlaceRepo extends JpaRepository<FavoritePlace, Long> {
    //zakhar
    List<FavoritePlace> findAllByUserEmail(String email);
    //zakhar
    boolean existsByPlaceIdAndUserEmail(Long placeId, String userEmail);
    //zakhar
    void deleteByPlaceIdAndUserEmail(Long placeId, String email);
    //zakhar
    FavoritePlace findByUserAndPlace(User user, Place Place);

}
