package greencity.service.impl;

import static greencity.constant.ErrorMessage.*;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.dto.habitstatistic.HabitIdDto;
import greencity.dto.user.*;
import greencity.entity.*;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.GoalStatus;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.exceptions.*;
import greencity.mapping.HabitMapper;
import greencity.mapping.UserGoalToResponseDtoMapper;
import greencity.repository.*;
import greencity.repository.options.UserFilter;
import greencity.service.UserService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
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
    private final UserRepo userRepo;
    private final UserGoalRepo userGoalRepo;
    private final GoalRepo goalRepo;
    private final HabitDictionaryRepo habitDictionaryRepo;
    private final CustomGoalRepo customGoalRepo;
    private final HabitRepo habitRepo;
    private final HabitStatisticRepo habitStatisticRepo;

    /**
     * Autowired mapper.
     */
    private ModelMapper modelMapper;
    private UserGoalToResponseDtoMapper userGoalToResponseDtoMapper;
    private HabitMapper habitMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findById(Long id) {
        return userRepo.findById(id)
            .orElseThrow(() -> new BadIdException(USER_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserForListDto> findByPage(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);
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
        userRepo.delete(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public Long findIdByEmail(String email) {
        log.info(LogMessage.IN_FIND_ID_BY_EMAIL, email);
        return userRepo.findIdByEmail(email).orElseThrow(
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
        return modelMapper.map(userRepo.save(user), UserRoleDto.class);
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
        return modelMapper.map(userRepo.save(user), UserStatusDto.class);
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
        return userRepo.save(updatable);
    }

    /**
     * {@inheritDoc}
     */
    public PageableDto<UserForListDto> getUsersByFilter(FilterUserDto filterUserDto, Pageable pageable) {
        Page<User> users = userRepo.findAll(new UserFilter(filterUserDto), pageable);
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
            userRepo.findByEmail(email).orElseThrow(() -> new BadEmailException(USER_NOT_FOUND_BY_EMAIL + email)),
            UserUpdateDto.class
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User update(UserUpdateDto dto, String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new BadEmailException(USER_NOT_FOUND_BY_EMAIL + email));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmailNotification(dto.getEmailNotification());
        return userRepo.save(user);
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
        return modelMapper.map(availableGoals, new TypeToken<List<GoalDto>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<UserGoalResponseDto> saveUserGoals(User user, BulkSaveUserGoalDto bulkDto) {
        List<UserGoalDto> goals = bulkDto.getUserGoals();
        List<UserCustomGoalDto> customGoals = bulkDto.getUserCustomGoal();
        if (goals == null && customGoals != null) {
            saveCustomGoalsForUserGoals(user, customGoals);
        }
        if (goals != null && customGoals == null) {
            saveGoalForUserGoal(user, goals);
        }
        if (goals != null && customGoals != null) {
            saveGoalForUserGoal(user, goals);
            saveCustomGoalsForUserGoals(user, customGoals);
        }
        return user.getUserGoals().stream()
            .map(userGoal -> userGoalToResponseDtoMapper.convertToDto(userGoal))
            .collect(Collectors.toList());
    }

    /**
     * Method save user goals with goal dictionary.
     *
     * @param user  {@link User} current user
     * @param goals list {@link UserGoalDto} for saving
     * @author Bogdan Kuzenko
     */
    private void saveGoalForUserGoal(User user, List<UserGoalDto> goals) {
        for (UserGoalDto el : goals) {
            UserGoal userGoal = modelMapper.map(el, UserGoal.class);
            userGoal.setUser(user);
            user.getUserGoals().add(userGoal);
            userGoalRepo.saveAll(user.getUserGoals());
        }
    }

    /**
     * Metgod save user goals with custom goal dictionary.
     *
     * @param user        {@link User} current user
     * @param customGoals list {@link UserCustomGoalDto} for saving
     * @author Bogdan Kuzenko
     */
    private void saveCustomGoalsForUserGoals(User user, List<UserCustomGoalDto> customGoals) {
        for (UserCustomGoalDto el1 : customGoals) {
            UserGoal userGoal = modelMapper.map(el1, UserGoal.class);
            userGoal.setUser(user);
            user.getUserGoals().add(userGoal);
        }
        userGoalRepo.saveAll(user.getUserGoals());
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko
     */
    @Override
    public List<Long> deleteUserGoals(String ids) {
        List<Long> arrayId = Arrays.stream(ids.split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList());

        List<Long> deleted = new ArrayList<>();
        for (Long id : arrayId) {
            deleted.add(delete(id));
        }
        return deleted;
    }

    /**
     * Method for deleting user goal by id.
     *
     * @param id {@link UserGoal} id.
     * @return id of deleted {@link UserGoal}
     * @author Bogdan Kuzenko
     */
    private Long delete(Long id) {
        UserGoal userGoal = userGoalRepo
            .findById(id).orElseThrow(() -> new NotFoundException(USER_GOAL_NOT_FOUND + id));
        userGoalRepo.delete(userGoal);
        return id;
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
            if (userGoal.getStatus().equals(GoalStatus.DONE)) {
                userGoal.setStatus(GoalStatus.ACTIVE);
                userGoal.setDateCompleted(null);
                userGoalRepo.save(userGoal);
            } else if (userGoal.getStatus().equals(GoalStatus.ACTIVE)) {
                userGoal.setStatus(GoalStatus.DONE);
                userGoal.setDateCompleted(LocalDateTime.now());
                userGoalRepo.save(userGoal);
            }
        } else {
            throw new UserGoalStatusNotUpdatedException(USER_HAS_NO_SUCH_GOAL + goalId);
        }
        return userGoalToResponseDtoMapper.convertToDto(userGoal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateUserRefreshToken(String refreshTokenKey, Long id) {
        return userRepo.updateUserRefreshToken(refreshTokenKey, id);
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
        if (id.equals(user.getId())) {
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

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko
     */
    @Transactional
    @Override
    public List<HabitDictionaryDto> getAvailableHabitDictionary(User user) {
        List<HabitDictionary> availableHabitDictionary = habitDictionaryRepo.findAvailableHabitDictionaryByUser(user);
        if (availableHabitDictionary.isEmpty()) {
            throw new UserHasNoAvailableHabitDictionaryException(USER_HAS_NO_AVAILABLE_HABIT_DICTIONARY);
        }
        return modelMapper.map(availableHabitDictionary, new TypeToken<List<HabitDictionaryDto>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitCreateDto> createUserHabit(User user, List<HabitIdDto> habitIdDto) {
        if (checkHabitId(user.getId(), habitIdDto)) {
            List<Habit> habits = habitRepo.saveAll(convertToHabit(habitIdDto, user));
            return convertToHabitCreateDto(habits);
        } else {
            throw new BadIdException(ErrorMessage.HABIT_IS_SAVED);
        }
    }

    /**
     * Method convert HabitIdDto to Habit.
     *
     * @param habitIdDtos {@link HabitIdDto}
     * @param user current user.
     * @return list habits.
     */
    private List<Habit> convertToHabit(List<HabitIdDto> habitIdDtos, final User user) {
        return habitIdDtos.stream()
            .map(HabitIdDto::getHabitDictionaryId)
            .map(id -> habitMapper.convertToEntity(id, user))
            .collect(Collectors.toList());
    }

    /**
     * Method convert {@link Habit} in list {@link HabitCreateDto}.
     *
     * @param habits - list {@link Habit}.
     * @return List {@link HabitCreateDto}
     */
    private List<HabitCreateDto> convertToHabitCreateDto(List<Habit> habits) {
        return habits
            .stream()
            .map(habitMapper::convertToDto)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko
     */
    @Transactional
    @Override
    public List<CustomGoalResponseDto> getAvailableCustomGoals(User user) {
        List<CustomGoal> allAvailableCustomGoalsForUser = customGoalRepo.findAllAvailableCustomGoalsForUser(user);
        if (allAvailableCustomGoalsForUser.isEmpty()) {
            throw new UserHasNoAvailableCustomGoalsException(USER_HAS_NO_AVAILABLE_CUSTOM_GOALS);
        }
        return modelMapper.map(allAvailableCustomGoalsForUser, new TypeToken<List<CustomGoalResponseDto>>() {
        }.getType());
    }
}

    /**
     * Method check is in user habit.
     *
     * @param userId Id current user.
     * @param habitIdDtos {@link HabitIdDto}
     * @return Boolean
     */
    private Boolean checkHabitId(Long userId, List<HabitIdDto> habitIdDtos) {
        List<Habit> habits = habitRepo.findByUserIdAndStatusHabit(userId);
        if (!habits.isEmpty()) {
            for (Habit habit : habits) {
                for (HabitIdDto habitIdDto : habitIdDtos) {
                    if (habitIdDto.getHabitDictionaryId().equals(habit.getHabitDictionary().getId())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteHabitByUserIdAndHabitDictionary(Long userId, Long habitId) {
        if (habitId == null) {
            throw new NotDeletedException(ErrorMessage.DELETE_LIST_ID_CANNOT_BE_EMPTY);
        }
        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new BadIdException(ErrorMessage.HABIT_NOT_FOUND_BY_USER_ID_AND_HABIT_DICTIONARY_ID));
        int countHabit = habitRepo.countHabitByUserId(userId);
        if (habitStatisticRepo.findAllByHabitId(habit.getId()).size() > 0 && countHabit > 1) {
            habitRepo.updateHabitStatusById(habit.getId(),false);
        } else if (countHabit > 1) {
            habitRepo.deleteById(habit.getId());
        } else {
            throw new NotDeletedException(ErrorMessage.NOT_DELETE_LAST_HABIT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDefaultHabit(User user) {
        if (habitRepo.findByUserIdAndStatusHabit(user.getId()).isEmpty()) {
            HabitIdDto habitIdDto = new HabitIdDto();
            habitIdDto.setHabitDictionaryId(1L);
            createUserHabit(user, Collections.singletonList(habitIdDto));
        }
    }
}