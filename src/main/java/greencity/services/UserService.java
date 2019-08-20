package greencity.services;

import greencity.entities.Place;
import greencity.entities.User;

/**
 * Provides the interface to manage {@link User} entity.
 * */
public interface UserService {

    /**
     * Just finds a user by it's {@code id}.
     *
     * @param id of the user to find for.
     * @return the {@link User} object with given {@code id}.
     */
    User findById(Long id);

    /**
     * Finds a user who added the given {@code place}.
     * @param place added by the requested user.
     * @return the {@link User} object or null.
     */
    User findByAddedPlacesContains(Place place);
}
