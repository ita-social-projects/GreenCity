package greencity.services.impl;

import greencity.constant.ErrorMessage;
import greencity.entities.User;
import greencity.entities.enums.ROLE;
import greencity.exceptions.BadIdException;
import greencity.exceptions.BadUserException;
import greencity.repositories.UserRepo;
import greencity.services.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepo repo;

    @Override
    public User save(User user) {
        if (findByEmail(user.getEmail()) != null) {
            throw new BadUserException("We have user with this email " + user.getEmail());
        }
        return repo.save(user);
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new BadIdException("No user with this id: " + id));
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        User user = findById(id);
        repo.delete(user);
    }

    @Override
    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public void updateRole(Long id, ROLE role) {
        User user =
                repo.findById(id)
                        .orElseThrow(
                                () -> new BadIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        user.setRole(role);
        repo.save(user);
    }

    @Override
    public void blockIUser(Long id) {
        User user =
                repo.findById(id)
                        .orElseThrow(
                                () -> new BadIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        user.setIsBanned(true);
        repo.save(user);
    }
}
