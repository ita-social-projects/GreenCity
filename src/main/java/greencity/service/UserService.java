package greencity.service;

<<<<<<< HEAD
import greencity.dto.user.UserForListDto;
import greencity.entity.User;

import greencity.entity.enums.ROLE;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
=======
import greencity.entity.User;

import java.util.List;
>>>>>>> dev

public interface UserService {
    User save(User user);

    User update(User user);

    User findById(Long id);

<<<<<<< HEAD
    void deleteById(Long id);

    User findByEmail(String email);

    void updateRole(Long id, ROLE role);

    void blockUser(Long id);

    void banUser(Long id);

    List<UserForListDto> findAll(Pageable pageable);
=======
    List<User> findAll();

    void deleteById(Long id);

    User findByEmail(String email);
>>>>>>> dev
}
