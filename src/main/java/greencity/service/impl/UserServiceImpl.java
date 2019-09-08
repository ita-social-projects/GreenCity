package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadEmailException;
import greencity.exception.BadIdException;
import greencity.exception.LastAdminException;
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
            throw new BadEmailException(ErrorMessage.USER_WITH_EMAIL_EXIST + user.getEmail());
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
     */
    @Override
    public PageableDto<UserForListDto> findByPage(Pageable pageable) {
        Page<User> users = repo.findAll(pageable);
        List<UserForListDto> userForListDtos =
            users.getContent().stream()
                .map(user -> modelMapper.map(user, UserForListDto.class))
                .collect(Collectors.toList());
        return new PageableDto<UserForListDto>(
            userForListDtos,
            users.getTotalElements(),
            users.getPageable().getPageNumber());
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
            throw new BadEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL);
        }
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRoleDto updateRole(Long id, ROLE role, String email) {
        checkUpdatableUser(id, email);
        checkLastAdmin(id);
        User user = findById(id);
        user.setRole(role);
        return modelMapper.map(repo.save(user), UserRoleDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserStatusDto updateStatus(Long id, UserStatus userStatus, String email) {
        checkUpdatableUser(id, email);
        checkLastAdmin(id);
        User user = findById(id);
        user.setUserStatus(userStatus);
        return modelMapper.map(repo.save(user), UserStatusDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleDto getRoles() {
        return new RoleDto(ROLE.class.getEnumConstants());
    }

    /**
     * Method which check that, if admin/moderator update role/status of himself, then throw exception.
     *
     * @param id    id of updatable user.
     * @param email email of admin/moderator.
     * @author Rostyslav Khasanov
     */
    private void checkUpdatableUser(Long id, String email) {
        if (id == findIdByEmail(email)) {
            throw new BadIdException(ErrorMessage.USER_CANT_UPDATE_HIMSELF);
        }
    }

    /**
     * Method which check that, if admin/moderator try update role/status of last admin, then throw exception.
     *
     * @param id id of updatable user.
     * @author Rostyslav Khasanov
     */
    private void checkLastAdmin(Long id) {
        if (repo.countByRole(ROLE.ROLE_ADMIN) == 1) {
            if (repo.findByRole(ROLE.ROLE_ADMIN).getId() == id) {
                throw new LastAdminException(ErrorMessage.IMPOSSIBLE_DELETE_LAST_ADMIN);
            }
        }
    }
}
