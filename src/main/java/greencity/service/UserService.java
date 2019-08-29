package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserPageableDto;
import greencity.entity.User;

import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import org.springframework.data.domain.Pageable;

/** Provides the interface to manage {@code User} entity. */
public interface UserService {

    /**
     * Save {@code User}.
     *
     * @param user User object.
     * @return {@code User}
     */
    User save(User user);

    /**
     * Find {@code User} by id.
     *
     * @param id id of user.
     * @return {@code User}
     */
    User findById(Long id);

    /**
     * Delete {@code User} by id.
     *
     * @param id id of user.
     */
    void deleteById(Long id);

    /**
     * Find {@code User} by email.
     *
     * @param email email of user.
     * @return {@code User}
     */
    User findByEmail(String email);

    /**
     * Update {@code ROLE} of user.
     *
     * @param id {@code User} id.
     * @param role {@code ROLE} for user.
     * @author Rostyslav Khasanov
     */
    void updateRole(Long id, ROLE role);

    /**
     * Update {@code UserStatus} of user.
     *
     * @param id {@code User} id.
     * @param userStatus {@code UserStatus} for user.
     * @author Rostyslav Khasanov
     */
    void updateUserStatus(Long id, UserStatus userStatus);

    /**
     * Find by page {@code User}.
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@code PageableDto<UserForDtoList>}.
     * @author Rostyslav Khasanov
     */
    UserPageableDto findByPage(Pageable pageable);
}
