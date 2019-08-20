package greencity.repositories;

import greencity.entities.Place;
import greencity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByAddedPlacesContains(Place place);
}
