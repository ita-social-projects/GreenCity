package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableDto;
import greencity.dto.filter.UserFilterDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.UserRepo;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import greencity.repository.options.UserFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    @Value("300000")
    private long timeAfterLastActivity;

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(UserVO userVO) {
        userRepo.save(modelMapper.map(userVO, User.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO findById(Long id) {
        return userRepo.findById(id)
            .map(user -> modelMapper.map(user, UserVO.class))
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO findByEmail(String email) {
        return userRepo.findByEmail(email)
            .map(user -> modelMapper.map(user, UserVO.class))
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserVO> findNotDeactivatedByEmail(String email) {
        Optional<User> notDeactivatedByEmail = userRepo.findNotDeactivatedByEmail(email);
        return Optional.of(modelMapper.map(notDeactivatedByEmail, UserVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findIdByEmail(String email) {
        log.info(LogMessage.IN_FIND_ID_BY_EMAIL, email);
        return userRepo.findIdByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserLastActivityTime(Long userId, Date userLastActivityTime) {
        userRepo.updateUserLastActivityTime(userId, userLastActivityTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserStatusDto updateStatus(Long id, UserStatus userStatus, String email) {
        checkUpdatableUser(id, email);
        accessForUpdateUserStatus(id, email);
        UserVO userVO = findById(id);
        userVO.setUserStatus(userStatus);
        userRepo.updateUserStatus(id, String.valueOf(userStatus));
        return modelMapper.map(userVO, UserStatusDto.class);
    }

    /**
     * Update {@code ROLE} of user.
     *
     * @param id   {@link UserVO} id.
     * @param role {@link Role} for user.
     * @return {@link UserRoleDto}
     * @deprecated updates like this on User entity should be handled in
     *             GreenCityUser via RestClient.
     */
    @Deprecated
    @Override
    public UserRoleDto updateRole(Long id, Role role, String email) {
        checkUpdatableUser(id, email);
        User user = userRepo.findById(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        user.setRole(role);
        userRepo.save(user);
        return modelMapper.map(user, UserRoleDto.class);
    }

    /**
     * Method which check that, if admin/moderator update role/status of himself,
     * then throw exception.
     *
     * @param id    id of updatable user.
     * @param email email of admin/moderator.
     */
    protected void checkUpdatableUser(Long id, String email) {
        UserVO user = findByEmail(email);
        if (id.equals(user.getId())) {
            throw new BadUpdateRequestException(ErrorMessage.USER_CANT_UPDATE_HIMSELF);
        }
    }

    /**
     * Method which check that, if moderator trying update status of admins or
     * moderators, then throw exception.
     *
     * @param id    id of updatable user.
     * @param email email of admin/moderator.
     */
    private void accessForUpdateUserStatus(Long id, String email) {
        UserVO user = findByEmail(email);
        if (user.getRole() == Role.ROLE_MODERATOR) {
            Role role = findById(id).getRole();
            if ((role == Role.ROLE_MODERATOR) || (role == Role.ROLE_ADMIN)) {
                throw new LowRoleLevelException(ErrorMessage.IMPOSSIBLE_UPDATE_USER_STATUS);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkIfTheUserIsOnline(Long userId) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        Optional<Timestamp> lastActivityTime = userRepo.findLastActivityTimeById(userId);
        if (lastActivityTime.isPresent()) {
            LocalDateTime userLastActivityTime = lastActivityTime.get().toLocalDateTime();
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime lastActivityTimeZDT = ZonedDateTime.of(userLastActivityTime, ZoneId.systemDefault());
            long result = now.toInstant().toEpochMilli() - lastActivityTimeZDT.toInstant().toEpochMilli();
            return result <= timeAfterLastActivity;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInitialsById(Long userId) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        UserVO userVO = modelMapper.map(optionalUser.get(), UserVO.class);
        String name = userVO.getName();
        String initials = name.contains(" ") ? String.valueOf(name.charAt(0))
            .concat(String.valueOf(name.charAt(name.indexOf(" ") + 1)))
            : String.valueOf(name.charAt(0));
        return initials.toUpperCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> getSixFriendsWithTheHighestRating(Long userId) {
        return userRepo.getSixFriendsWithTheHighestRating(userId).stream()
            .map(user -> modelMapper.map(user, UserVO.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEventOrganizerRating(Long eventOrganizerId, Double rate) {
        userRepo.updateUserEventOrganizerRating(eventOrganizerId, rate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserManagementVO> getAllUsersByCriteria(String criteria, String role, String status,
        Pageable pageable) {
        UserFilterDto filterUserDto = createUserFilterDto(criteria, role, status);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        var listOfUsers = userRepo.findAllManagementVo(new UserFilter(filterUserDto), sortedPageable);

        return new PageableDto<>(
            listOfUsers.getContent(),
            listOfUsers.getTotalElements(),
            listOfUsers.getPageable().getPageNumber(),
            listOfUsers.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserRating(Long userId, Double rating) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        userRepo.updateUserRating(userId, rating);
    }

    private UserFilterDto createUserFilterDto(String criteria, String role, String status) {
        if (status != null) {
            status = status.equals("all") ? null : status;
        }
        if (role != null) {
            role = role.equals("all") ? null : role;
        }
        return new UserFilterDto(criteria, role, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> findByEmails(List<String> emails) {
        return userRepo.findAllByEmailIn(emails).stream()
            .map(u -> modelMapper.map(u, UserVO.class))
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> getUsersIdByEmailPreferenceAndEmailPeriodicity(EmailPreference emailPreference,
        EmailPreferencePeriodicity periodicity) {
        return userRepo.findAllByEmailPreferenceAndEmailPeriodicity(emailPreference.name(), periodicity.name()).stream()
            .map(u -> modelMapper.map(u, UserVO.class))
            .toList();
    }
}