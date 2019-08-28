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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/** The class provides implementation of the {@code UserService}. */
@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    /** Autowired repository. */
    private UserRepo repo;

    /** Autowired mapper. */
    private ModelMapper modelMapper;

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

    /**
     * Find by page {@code User}.
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@code PageableDto<UserForDtoList>}.
     * @author Rostyslav Khasanov
     */
    @Override
    public PageableDto<UserForListDto> findByPage(Pageable pageable) {
        Page<User> users = repo.findAllByOrderByEmail(pageable);
        List<UserForListDto> userForListDtos =
                users.getContent().stream()
                        .map(user -> modelMapper.map(user, UserForListDto.class))
                        .collect(Collectors.toList());
        PageableDto<UserForListDto> page =
                new PageableDto<>(
                        userForListDtos,
                        users.getTotalElements(),
                        users.getPageable().getPageNumber());
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
//zakhar
    @Override
    public Long findIdByEmail(String email) {
        return repo.findIdByEmail(email);
    }

    //zakhar
    @Override
    public boolean existsByEmail(String email) { //zakhar
        return repo.existsByEmail(email);
    }


    /**
     * Update {@code ROLE} of user.
     *
     * @param id {@code User} id.
     * @param role {@code ROLE} for user.
     * @author Rostyslav Khasanov
     */
    @Override
    public void updateRole(Long id, ROLE role) {
        User user =
                repo.findById(id)
                        .orElseThrow(
                                () -> new BadIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        user.setRole(role);
        repo.save(user);
    }

    /**
     * Update {@code UserStatus} of user.
     *
     * @param id {@code User} id.
     * @param userStatus {@code UserStatus} for user.
     * @author Rostyslav Khasanov
     */
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
