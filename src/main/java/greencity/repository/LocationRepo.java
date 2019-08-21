package greencity.repository;

import greencity.entity.Location;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepo extends JpaRepository<Location, Long> {

  @Query(
      value =
          "FROM Location l"
              + " where (l.lat > :northEastLat  and l.lat<:southWestLat)"
              + " AND (l.lng >:northEastLng and l.lng<:southWestLng)")
  List<Location> findLocationsByMapsBounds(
      @Param("northEastLat") Double northEastLat,
      @Param("northEastLng") Double northEastLng,
      @Param("southWestLat") Double southWestLat,
      @Param("southWestLng") Double southWestLng);
}
