package greencity.service.impl;

<<<<<<< HEAD
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserForListDto;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
=======
import greencity.entity.User;
>>>>>>> dev
import greencity.exception.BadIdException;
import greencity.exception.BadUserException;
import greencity.repository.UserRepo;
import greencity.service.UserService;
<<<<<<< HEAD
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

=======
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

>>>>>>> dev
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
<<<<<<< HEAD
    public List<UserForListDto> findAll(Pageable pageable) {
        Page<User> users = repo.findAllByOrderByEmail(pageable);
        return users.getContent().stream().map(UserForListDto::new).collect(Collectors.toList());
=======
    public List<User> findAll() {
        return null;
>>>>>>> dev
    }

    @Override
    public void deleteById(Long id) {
        User user = findById(id);
<<<<<<< HEAD
        repo.delete(user);
=======
         repo.delete(user);
>>>>>>> dev
    }

    @Override
    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }
<<<<<<< HEAD

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
    public void blockUser(Long id) {
        User user =
                repo.findById(id)
                        .orElseThrow(
                                () -> new BadIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        user.setIsBlocked(true);
        repo.save(user);
    }

    @Override
    public void banUser(Long id) {
        User user =
                repo.findById(id)
                        .orElseThrow(
                                () -> new BadIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        user.setIsBanned(true);
        repo.save(user);
    }
=======
>>>>>>> dev
}
