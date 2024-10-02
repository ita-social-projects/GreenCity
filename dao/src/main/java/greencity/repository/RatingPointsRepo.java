package greencity.repository;

import greencity.constant.ErrorMessage;
import greencity.entity.RatingPoints;
import greencity.exception.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RatingPointsRepo extends JpaRepository<RatingPoints, Long> {
    /**
     * Finds a RatingPoints entity by its name.
     *
     * @param name the name of the RatingPoints entity to find.
     * @return an Optional containing the RatingPoints entity if found, or an empty
     *         Optional if not found.
     */
    Optional<RatingPoints> findByName(String name);

    /**
     * Finds a RatingPoints entity by its name or throws a NotFoundException if not
     * found. This method simplifies the retrieval of a RatingPoints entity by its
     * name and ensures that a descriptive exception is thrown when the entity is
     * not found.
     *
     * @param name the name of the RatingPoints entity to find.
     * @return the found RatingPoints entity.
     */
    default RatingPoints findByNameOrThrow(String name) {
        return findByName(name)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.RATING_POINTS_NOT_FOUND_BY_NAME + name));
    }
}
