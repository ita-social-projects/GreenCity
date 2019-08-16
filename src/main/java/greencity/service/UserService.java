package greencity.service;

import greencity.dto.user.UserForListDto;
import greencity.entity.User;

import greencity.entity.enums.ROLE;
import java.util.List;

public interface UserService {
    User save(User user);

    User update(User user);

    User findById(Long id);

    List<UserForListDto> findAll();

    void deleteById(Long id);

    User findByEmail(String email);

    void updateRole(Long id, ROLE role);

    void blockIUser(Long id);
}
