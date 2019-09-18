package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.filter.UserFilterDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {@link User} entity.
 *
 * @author Nazar Stasyuk and Rostyslav .....
 * @version 1.0
 */
public interface UserService {
    /**
     * Method that allow you to save new {@link User}.
     *
     * @param user a value of {@link User}
     */
    User save(User user);

    /**
     * Method that allow you to find {@link User} by ID.
     *
     * @param id a value of {@link Long}
     * @return {@link User}
     */
    User findById(Long id);

    /**
     * Method that allow you to delete {@link User} by ID.
     *
     * @param id a value of {@link Long}
     */
    void deleteById(Long id);

    /**
     * Method that allow you to find {@link User} by email.
     *
     * @param email a value of {@link String}
     * @return Optional of {@link User}
     */
    Optional<User> findByEmail(String email);

    /**
     * Find User's id by User email.
     *
     * @param email - {@link User} email
     * @return {@link User} id
     * @author Zakhar Skaletskyi
     */
    Long findIdByEmail(String email);

    /**
     * Update {@code ROLE} of user.
     *
     * @param id   {@code User} id.
     * @param role {@code ROLE} for user.
     * @author Rostyslav Khasanov
     */
    UserRoleDto updateRole(Long id, ROLE role, String email);

    /**
     * Update {@code UserStatus} of user.
     *
     * @param id         {@code User} id.
     * @param userStatus {@code UserStatus} for user.
     * @author Rostyslav Khasanov
     */
    UserStatusDto updateStatus(Long id, UserStatus userStatus, String email);

    /**
     * Find by page {@code User}.
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@code PageableDto<UserForDtoList>}.
     * @author Rostyslav Khasanov
     */
    PageableDto findByPage(Pageable pageable);

    /**
     * Get all exists roles.
     *
     * @return a dto of {@code RoleDto}.
     * @author Rostyslav Khasanov
     */
    RoleDto getRoles();

    /**
     * Update last visit of user.
     *
     * @return {@link User}.
     */
    User updateLastVisit(User user);

    /**
     * Update last visit of user.
     *
     * @return {@link User}.
     */
    PageableDto filterByName(String reg, Pageable pageable);

    /**
     * Update last visit of user.
     *
     * @return {@link User}.
     */
    PageableDto<UserForListDto> filterByNameWithCriteria(UserFilterDto userFilterDto, Pageable pageable);
}
