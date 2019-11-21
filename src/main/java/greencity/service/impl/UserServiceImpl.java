package greencity.service.impl;

import static greencity.constant.ErrorMessage.*;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.user.*;
import greencity.entity.Goal;
import greencity.entity.User;
import greencity.entity.UserGoal;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.GoalStatus;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.*;
import greencity.mapping.UserGoalToResponseDtoMapper;
import greencity.repository.GoalRepo;
import greencity.repository.UserGoalRepo;
import greencity.repository.UserRepo;
import greencity.repository.options.UserFilter;
import greencity.service.UserService;
import greencity.service.UserValidationService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserRepo repo;
    private final UserGoalRepo userGoalRepo;
    private final GoalRepo goalRepo;

    /**
     * Autowired mapper.
     */
    private ModelMapper modelMapper;
    private UserGoalToResponseDtoMapper userGoalToResponseDtoMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public User save(User user) {
        if (findByEmail(user.getEmail()).isPresent()) {
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
            .orElseThrow(() -> new BadIdException(USER_NOT_FOUND_BY_ID + id));
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
        return new PageableDto<>(
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
    public Optional<User> findByEmail(String email) {
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
        return repo.findIdByEmail(email).orElseThrow(
            () -> new BadEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRoleDto updateRole(Long id, ROLE role, String email) {
        checkUpdatableUser(id, email);
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
        accessForUpdateUserStatus(id, email);
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
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public List<EmailNotification> getEmailNotificationsStatuses() {
        return Arrays.asList(EmailNotification.class.getEnumConstants());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updateLastVisit(User user) {
        User updatable = findById(user.getId());
        log.info(updatable.getLastVisit() + "s");
        updatable.setLastVisit(LocalDateTime.now());
        return repo.save(updatable);
    }

    /**
     * {@inheritDoc}
     */
    public PageableDto<UserForListDto> getUsersByFilter(FilterUserDto filterUserDto, Pageable pageable) {
        Page<User> users = repo.findAll(new UserFilter(filterUserDto), pageable);
        List<UserForListDto> userForListDtos =
            users.getContent().stream()
                .map(user -> modelMapper.map(user, UserForListDto.class))
                .collect(Collectors.toList());
        return new PageableDto<>(
            userForListDtos,
            users.getTotalElements(),
            users.getPageable().getPageNumber());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserUpdateDto getUserUpdateDtoByEmail(String email) {
        return modelMapper.map(
            repo.findByEmail(email).orElseThrow(() -> new BadEmailException(USER_NOT_FOUND_BY_EMAIL + email)),
            UserUpdateDto.class
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User update(UserUpdateDto dto, String email) {
        User user = repo.findByEmail(email).orElseThrow(() -> new BadEmailException(USER_NOT_FOUND_BY_EMAIL + email));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmailNotification(dto.getEmailNotification());
        return repo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<UserGoalResponseDto> getUserGoals(User user) {
        List<UserGoalResponseDto> userGoalResponseDto = userGoalRepo
            .findAllByUserId(user.getId())
            .stream()
            .map(userGoal -> userGoalToResponseDtoMapper.convertToDto(userGoal))
            .collect(Collectors.toList());
        if (userGoalResponseDto.isEmpty()) {
            throw new UserHasNoGoalsException(USER_HAS_NO_GOALS);
        }
        return userGoalResponseDto;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<GoalDto> getAvailableGoals(User user) {
        List<Goal> availableGoals = goalRepo.findAvailableGoalsByUser(user);
        if (availableGoals.isEmpty()) {
            throw new UserHasNoAvailableGoalsException(USER_HAS_NO_AVAILABLE_GOALS);
        }
        return modelMapper.map(availableGoals, new TypeToken<List<GoalDto>>(){}.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<UserGoalResponseDto> saveUserGoals(User user, BulkSaveUserGoalDto bulkDto) {
        List<UserGoalDto> dto = bulkDto.getUserGoals();
        List<String> errorMessages = new ArrayList<>();
        for (UserGoalDto el : dto) {
            UserGoal userGoal = modelMapper.map(el, UserGoal.class);
            userGoal.setUser(user);
            List<UserGoal> duplicates =
                user.getUserGoals().stream().filter(o -> o.equals(userGoal)).collect(Collectors.toList());
            if (!duplicates.isEmpty()) {
                errorMessages.add(userGoal.getGoal().getText());
            } else {
                user.getUserGoals().add(userGoal);
            }
        }
        userGoalRepo.saveAll(user.getUserGoals());
        if (!errorMessages.isEmpty()) {
            throw new UserGoalNotSavedException(USER_GOAL_WHERE_NOT_SAVED + errorMessages.toString());
        }
        return user.getUserGoals()
            .stream()
            .map(userGoal -> userGoalToResponseDtoMapper.convertToDto(userGoal))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserGoalResponseDto updateUserGoalStatus(User user, Long goalId) {
        UserGoal userGoal;
        if (user.getUserGoals().stream().anyMatch(o -> o.getId().equals(goalId))) {
            userGoal = userGoalRepo.getOne(goalId);
            userGoal.setStatus(GoalStatus.DONE);
            userGoal.setDateCompleted(LocalDateTime.now());
            userGoalRepo.save(userGoal);
        } else {
            throw new UserGoalStatusNotUpdatedException(USER_HAS_NO_SUCH_GOAL + goalId);
        }
        return userGoalToResponseDtoMapper.convertToDto(userGoal);
    }

    /**
     * Method which check that, if admin/moderator update role/status of himself, then throw exception.
     *
     * @param id    id of updatable user.
     * @param email email of admin/moderator.
     * @author Rostyslav Khasanov
     */
    private void checkUpdatableUser(Long id, String email) {
        User user = findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        if (id == user.getId()) {
            throw new BadUpdateRequestException(ErrorMessage.USER_CANT_UPDATE_HIMSELF);
        }
    }

    /**
     * Method which check that, if moderator trying update status of admins or moderators, then throw exception.
     *
     * @param id    id of updatable user.
     * @param email email of admin/moderator.
     * @author Rostyslav Khasanov
     */
    private void accessForUpdateUserStatus(Long id, String email) {
        User user = findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        if (user.getRole() == ROLE.ROLE_MODERATOR) {
            ROLE role = findById(id).getRole();
            if ((role == ROLE.ROLE_MODERATOR) || (role == ROLE.ROLE_ADMIN)) {
                throw new LowRoleLevelException(ErrorMessage.IMPOSSIBLE_UPDATE_USER_STATUS);
            }
        }
    }
}
