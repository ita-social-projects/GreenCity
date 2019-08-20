package greencity.services;

import greencity.dto.user.PlaceAuthorDto;
import greencity.entities.Place;
import greencity.entities.User;
import greencity.mapping.UserMapper;
import greencity.repositories.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private UserRepo userRepo;

    @Override
    public User findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public User findByAddedPlacesContains(Place place) {
        return userRepo.findByAddedPlacesContains(place);
    }
}
