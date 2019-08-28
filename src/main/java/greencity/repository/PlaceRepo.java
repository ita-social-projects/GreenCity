package greencity.repository;

import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepo extends JpaRepository<Place, Long> {

    Place findByAddress(String address);
    //zakhar
    Place findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT id from Place where email=:email")
    Long findIdByEmail(String email);    //zakhar
   /* //zakhar
    @Query("SELECT p.address,fp.name from Place p inner join p.favoritePlace fp where p.user.id=:id")
    count(comments)
    avg(rate)
    List<OpeningHoursDto> openingHours;
    private Photo photo;
    private LocationDto location;


    List<Place> findWithFavoritePlaceNameByPlaceId(@Param("userId") Long userId);
        */

}
