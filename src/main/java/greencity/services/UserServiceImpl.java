package greencity.services;

import greencity.entities.Place;
import greencity.entities.User;
import greencity.exceptions.BadIdException;
import greencity.repositories.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code UserService}.
 * */
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    /** Autowired repository.*/
    private UserRepo repo;

    /**
     * {@inheritDoc}
     */
    @Override
    public User getById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new BadIdException("No user with this id: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByAddedPlace(Place place) {
        return repo.findByAddedPlacesContains(place);
    }
}
