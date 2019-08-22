package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.user.UserForListDto;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadIdException;
import greencity.exception.BadUserException;
import greencity.repository.UserRepo;
import greencity.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
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
    public PageableDto<UserForListDto> findByPage(Pageable pageable) {
        Page<User> users = repo.findAllByOrderByEmail(pageable);
        List<UserForListDto> userForListDtos =
                users.getContent().stream().map(UserForListDto::new).collect(Collectors.toList());
        PageableDto<UserForListDto> page =
                new PageableDto<>(userForListDtos, users.getTotalPages());
        return page;
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
    public void updateUserStatus(Long id, UserStatus userStatus) {
        User user =
                repo.findById(id)
                        .orElseThrow(
                                () -> new BadIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        user.setUserStatus(userStatus);
        repo.save(user);
    }
}
