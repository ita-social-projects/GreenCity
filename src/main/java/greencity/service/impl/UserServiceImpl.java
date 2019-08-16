package greencity.service.impl;

import greencity.entity.User;
import greencity.exception.BadIdException;
import greencity.exception.BadUserException;
import greencity.repository.UserRepo;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
