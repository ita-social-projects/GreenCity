package greencity.repositories;

import greencity.entities.Place;
import greencity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link User} entity.
 * */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    /**
     * Find a {@code User} by it's email.
     *
     * @param email of a user.
     * @return the {@code User} with the given email.
     */
    User findByEmail(String email);

    /**
     * Find a {@code User} by a place which he added.
     *
     * @param place to find by.
     * @return the {@code User} which added the place.
     */
    User findByAddedPlacesContains(Place place);
}
