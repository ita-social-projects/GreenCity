package greencity.repository;

import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Provides an interface to manage {@link Place} entity. */
@Repository
public interface PlaceRepo extends JpaRepository<Place, Long> {

    /**
     * Finds all places related to the given {@code PlaceStatus}.
     *
     * @param status to find by.
     * @return a list of places with the given {@code PlaceStatus}.
     *
     * @author Roman Zahorui
     */
    List<Place> findAllByStatusOrderByModifiedDateDesc(PlaceStatus status);

    /**
     * Method to find average rate
     *
     * @param id place
     * @return average rate
     */
    @Query(value = "select avg(r.rate) FROM Rate r " + "where place_id = :id")
    double averageRate(@Param("id") Long id);

    @Query("from Place p where p.status = :status")
    List<Place> getPlacesByStatus(@Param("status") PlaceStatus status);

    /**
     * Method return a list {@code Place} depends on the map bounds
     *
     * @param northEastLat latitude of extreme North-East point of the map
     * @param northEastLng longitude of extreme North-East point of the map
     * @param southWestLat latitude of South-West point of the map
     * @param southWestLng longitude of South-West point of the map
     * @return a list of {@code Place}
     * @author Marian Milian.
     */
    @Query(
            value =
                    "FROM Place p"
                            + " left join"
                            + " Location l  on p.location.id =l.id "
                            + " where l.lat > :southWestLat  and l.lat< :northEastLat"
                            + " AND l.lng >:southWestLng and l.lng<:northEastLng "
                            + " and p.status = 2"
                            + " ORDER BY p.name")
    List<Place> findPlacesByMapsBounds(
            @Param("northEastLat") Double northEastLat,
            @Param("northEastLng") Double northEastLng,
            @Param("southWestLat") Double southWestLat,
            @Param("southWestLng") Double southWestLng);
}
