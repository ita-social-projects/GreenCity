package greencity.services;

import greencity.entities.Place;
import greencity.entities.User;
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
    private UserRepo userRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public User findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findByAddedPlacesContains(Place place) {
        return userRepo.findByAddedPlacesContains(place);
    }
}
