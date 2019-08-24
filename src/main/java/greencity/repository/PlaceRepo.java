package greencity.repository;

import greencity.entity.Place;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepo extends JpaRepository<Place, Long> {
    @Query(
            value =
                    "FROM Place p"
                            + " left join"
                            + " Location l  on p.location.id =l.id "
                            + " where l.lat > :northEastLat  and l.lat<:southWestLat"
                            + " AND l.lng >:southWestLng and l.lng<:northEastLng "
                            + " and p.status = 2"
                            + " ORDER BY p.name")
    List<Place> findPlacesByMapsBounds(
            @Param("northEastLat") Double northEastLat,
            @Param("northEastLng") Double northEastLng,
            @Param("southWestLat") Double southWestLat,
            @Param("southWestLng") Double southWestLng);
}
