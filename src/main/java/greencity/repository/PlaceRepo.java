package greencity.repository;

import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
;

/** Provides an interface to manage {@link Place} entity. */
@Repository
public interface PlaceRepo extends JpaRepository<Place, Long> {

    /**
     * Finds all places related to the given {@code PlaceStatus}.
     *
     * @param status to find by.
     * @return a list of places with the given {@code PlaceStatus}.
     */
    List<Place> findAllByStatusOrderByModifiedDateDesc(PlaceStatus status);
    @Query("SELECT id from Place where email=:email")
    Long findIdByEmail(String email);    //zakhar

    Place findByAddress(String address);
    //zakhar
    Place findByEmail(String email);
    boolean existsByEmail(String email);
    /**
     * Method to find average rate
     *
     * @param id place
     * @return average rate
     */
    @Query(value = "select avg(r.rate) FROM Rate r " + "where place_id = :id")
    Byte averageRate(@Param("id") Long id);

    @Query("from Place p where p.status = :status")
    List<Place> getPlacesByStatus(@Param("status") PlaceStatus status);

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
