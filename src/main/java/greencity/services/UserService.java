package greencity.services;

import greencity.entities.Place;
import greencity.entities.User;

public interface UserService {
    User findById(Long id);

    User findByAddedPlacesContains(Place place);
}
