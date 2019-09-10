package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserPageableDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadIdException;
import greencity.exception.UserAlreadyRegisteredException;
import greencity.repository.UserRepo;
import greencity.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code UserService}.
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    /**
     * Autowired repository.
     */
    private UserRepo repo;

    /**
     * Autowired mapper.
     */
    private ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public User save(User user) {
        if (findByEmail(user.getEmail()) != null) {
            throw new UserAlreadyRegisteredException(ErrorMessage.USER_WITH_EMAIL_EXIST + user.getEmail());
        }
        return repo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new BadIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     *
     * @author Rostyslav Khasanov
     */
    @Override
    public UserPageableDto findByPage(Pageable pageable) {
        Page<User> users = repo.findAllByOrderByEmail(pageable);
        List<UserForListDto> userForListDtos =
            users.getContent().stream()
                .map(user -> modelMapper.map(user, UserForListDto.class))
                .collect(Collectors.toList());
        return new UserPageableDto(
            userForListDtos,
            users.getTotalElements(),
            users.getPageable().getPageNumber(),
            ROLE.class.getEnumConstants());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        User user = findById(id);
        repo.delete(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public Long findIdByEmail(String email) {
        log.info(LogMessage.IN_FIND_ID_BY_EMAIL, email);
        Long id = repo.findIdByEmail(email);
        if (id == null) {
            throw new UserAlreadyRegisteredException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL);
        }
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Rostyslav Khasanov
     */
    @Override
    public UserRoleDto updateRole(Long id, ROLE role) {
        User user = findById(id);
        user.setRole(role);
        return modelMapper.map(repo.save(user), UserRoleDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Rostyslav Khasanov
     */
    @Override
    public UserStatusDto updateStatus(Long id, UserStatus userStatus) {
        User user = findById(id);
        user.setUserStatus(userStatus);
        return modelMapper.map(repo.save(user), UserStatusDto.class);
    }
}
