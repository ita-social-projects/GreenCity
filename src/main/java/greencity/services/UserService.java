package greencity.services;

import greencity.entities.User;

import greencity.entities.enums.ROLE;
import java.util.List;

public interface UserService {
    User save(User user);

    User update(User user);

    User findById(Long id);

    List<User> findAll();

    void deleteById(Long id);

    User findByEmail(String email);

    void updateRole(Long id, ROLE role);

    void blockIUser(Long id)
}
