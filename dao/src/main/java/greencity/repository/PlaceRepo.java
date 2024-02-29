package greencity.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import greencity.entity.Place;
import greencity.enums.PlaceStatus;

/**
 * Provides an interface to manage {@link Place} entity.
 */
@Repository
public interface PlaceRepo extends JpaRepository<Place, Long>, JpaSpecificationExecutor<Place> {
    /**
     * Finds all places related to the given {@code PlaceStatus}.
     *
     * @param status   to find by.
     * @param pageable pageable configuration.
     * @return a list of places with the given {@code PlaceStatus}.
     * @author Roman Zahorui
     */
    Page<Place> findAllByStatusOrderByModifiedDateDesc(PlaceStatus status, Pageable pageable);

    /**
     * Method to find all created {@link Place}'s by user id.
     *
     * @param userId - {@code User}'s id.
     * @return list of {@link Place}'s
     */
    @Query(nativeQuery = true, value = "SELECT * FROM places p WHERE p.author_id = :userId")
    List<Place> findAllByUserId(@Param("userId") Long userId);

    /**
     * Method to find average rate.
     *
     * @param id place
     * @return average rate
     */
    @Query(value = "SELECT AVG(r.rate) FROM Estimate r " + "WHERE r.place.id = :id")
    Double getAverageRate(@Param("id") Long id);

    /**
     * Generated javadoc, must be replaced with real one.
     */
    @Query("FROM Place p WHERE p.status = :status")
    List<Place> getPlacesByStatus(@Param("status") PlaceStatus status);

    /**
     * Method return a list {@code Place} depends on the map bounds.
     *
     * @param northEastLat latitude of extreme North-East point of the map.
     * @param northEastLng longitude of extreme North-East point of the map.
     * @param southWestLat latitude of South-West point of the map.
     * @param southWestLng longitude of South-West point of the map.
     * @param status       status of places witch should be presented.
     * @return a list of {@code Place}
     * @author Marian Milian.
     */
    @Query(
        value = "FROM Place p "
            + " LEFT JOIN"
            + " Location l  ON p.location.id = l.id "
            + " WHERE l.lat > :southWestLat  AND l.lat< :northEastLat"
            + " AND l.lng >:southWestLng AND l.lng<:northEastLng "
            + " AND p.status = :status"
            + " ORDER BY p.name")
    List<Place> findPlacesByMapsBounds(
        @Param("northEastLat") Double northEastLat,
        @Param("northEastLng") Double northEastLng,
        @Param("southWestLat") Double southWestLat,
        @Param("southWestLng") Double southWestLng,
        @Param("status") PlaceStatus status);

    /**
     * The method to find all {@link Place}'s which was added between 2 dates and
     * has {@link PlaceStatus}.
     *
     * @param startDate - start date of search
     * @param endDate   - end date of search
     * @param status    - {@link PlaceStatus} of places
     * @return list of {@link Place}'s
     */
    List<Place> findAllByModifiedDateBetweenAndStatus(
        LocalDateTime startDate, LocalDateTime endDate, PlaceStatus status);

    /**
     * Method returns {@link Place} by search query and page.
     *
     * @param pageable    {@link Pageable}.
     * @param searchQuery query to search.
     * @return list of {@link Place}.
     */
    @Query("SELECT p FROM Place p WHERE CONCAT(p.id,'') LIKE LOWER(CONCAT('%', :searchQuery, '%')) "
        + "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    Page<Place> searchBy(Pageable pageable, String searchQuery);

    /**
     * Method to get place by category name.
     *
     * @param category category to search
     * @return - places with searching category
     */
    @Query(nativeQuery = true,
        value = "SELECT p.* FROM places p "
            + "join categories c on c.id = p.category_id "
            + "WHERE c.name IN (:category) "
            + "or c.name_ua IN (:category)")
    List<Place> findPlaceByCategory(String[] category);
}