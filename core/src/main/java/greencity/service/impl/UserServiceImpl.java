package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.dto.habitstatistic.HabitDictionaryDto;
import greencity.dto.habitstatistic.HabitIdDto;
import greencity.dto.user.BulkSaveUserGoalDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UserCustomGoalDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserGoalDto;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserUpdateDto;
import greencity.entity.*;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.GoalStatus;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.entity.localization.GoalTranslation;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserGoalStatusNotUpdatedException;
import greencity.exception.exceptions.UserHasNoAvailableGoalsException;
import greencity.exception.exceptions.UserHasNoAvailableHabitDictionaryException;
import greencity.exception.exceptions.UserHasNoGoalsException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.repository.HabitDictionaryTranslationRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitStatisticRepo;
import greencity.repository.UserGoalRepo;
import greencity.repository.UserRepo;
import greencity.repository.options.UserFilter;
import greencity.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static greencity.constant.ErrorMessage.USER_GOAL_NOT_FOUND;
import static greencity.constant.ErrorMessage.USER_HAS_NO_AVAILABLE_GOALS;
import static greencity.constant.ErrorMessage.USER_HAS_NO_AVAILABLE_HABIT_DICTIONARY;
import static greencity.constant.ErrorMessage.USER_HAS_NO_GOALS;
import static greencity.constant.ErrorMessage.USER_HAS_NO_SUCH_GOAL;
import static greencity.constant.ErrorMessage.USER_NOT_FOUND_BY_EMAIL;
import static greencity.constant.ErrorMessage.USER_NOT_FOUND_BY_ID;

/**
 * The class provides implementation of the {@code UserService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    /**
     * Autowired repository.
     */
    private final UserRepo userRepo;
    private final UserGoalRepo userGoalRepo;
    private final CustomGoalRepo customGoalRepo;
    private final HabitRepo habitRepo;
    private final HabitService habitService;
    private final HabitStatisticRepo habitStatisticRepo;
    private final GoalTranslationRepo goalTranslationRepo;
    private final HabitDictionaryService habitDictionaryService;
    private final HabitDictionaryTranslationRepo habitDictionaryTranslationRepo;
    private final FileService fileService;

    private final String defaultProfilePicture = "https://storage.cloud.google.com"
            + "/staging.greencity-c5a3a.appspot.com"
            + "/d333665a-9269-49ef-bc77-2d8d4090290f";

    /**
     * Autowired mapper.
     */
    private final ModelMapper modelMapper;

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
            .orElseThrow(() -> new WrongIdException(USER_NOT_FOUND_BY_ID + id));
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
    public User findByEmail(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        return optionalUser.orElse(null);
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
            () -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
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
            userRepo.findByEmail(email).orElseThrow(() -> new WrongEmailException(USER_NOT_FOUND_BY_EMAIL + email)),
            UserUpdateDto.class
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User update(UserUpdateDto dto, String email, MultipartFile image) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(USER_NOT_FOUND_BY_EMAIL + email));
        user.setName(dto.getName());
        user.setEmailNotification(dto.getEmailNotification());

        String url = image != null ? fileService.upload(image).toString() : defaultProfilePicture;
        user.setProfilePicturePath(url);
        return userRepo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<UserGoalResponseDto> getUserGoals(Long userId, String language) {
        List<UserGoalResponseDto> userGoalResponseDtos = userGoalRepo
            .findAllByUserId(userId)
            .stream()
            .map(userGoal -> modelMapper.map(userGoal, UserGoalResponseDto.class))
            .collect(Collectors.toList());
        if (userGoalResponseDtos.isEmpty()) {
            throw new UserHasNoGoalsException(USER_HAS_NO_GOALS);
        }
        userGoalResponseDtos.stream().forEach(el -> setTextForAnyUserGoal(el, userId, language));
        return userGoalResponseDtos;
    }

    /**
     * Method for setting text either for UserGoal with localization or for CustomGoal.
     *
     * @param userId id of the current user.
     * @param dto    {@link UserGoalResponseDto}
     */
    private UserGoalResponseDto setTextForAnyUserGoal(UserGoalResponseDto dto, Long userId, String language) {
        String text = userGoalRepo.findGoalByUserGoalId(dto.getId()).isPresent()
            ? goalTranslationRepo.findByUserIdLangAndUserGoalId(userId, language, dto.getId()).getText()
            : customGoalRepo.findByUserGoalIdAndUserId(dto.getId(), userId).getText();
        dto.setText(text);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<GoalDto> getAvailableGoals(Long userId, String language) {
        List<GoalTranslation> goalTranslations = goalTranslationRepo
            .findAvailableByUserId(userId, language);
        if (goalTranslations.isEmpty()) {
            throw new UserHasNoAvailableGoalsException(USER_HAS_NO_AVAILABLE_GOALS);
        }
        return goalTranslations
            .stream()
            .map(g -> modelMapper.map(g, GoalDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<UserGoalResponseDto> saveUserGoals(Long userId, BulkSaveUserGoalDto bulkDto, String language) {
        List<UserGoalDto> goalDtos = bulkDto.getUserGoals();
        List<UserCustomGoalDto> customGoalDtos = bulkDto.getUserCustomGoal();
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new WrongIdException(USER_NOT_FOUND_BY_ID + userId));
        if (goalDtos == null && customGoalDtos != null) {
            saveCustomGoalsForUserGoals(user, customGoalDtos);
        }
        if (goalDtos != null && customGoalDtos == null) {
            saveGoalForUserGoal(user, goalDtos);
        }
        if (goalDtos != null && customGoalDtos != null) {
            saveGoalForUserGoal(user, goalDtos);
            saveCustomGoalsForUserGoals(user, customGoalDtos);
        }
        return getUserGoals(userId, language);
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
     * Method save user goals with custom goal dictionary.
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
    @Transactional
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
    public UserGoalResponseDto updateUserGoalStatus(Long userId, Long goalId, String language) {
        UserGoal userGoal;
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new WrongIdException(USER_NOT_FOUND_BY_ID + userId));
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
        UserGoalResponseDto updatedUserGoal = modelMapper.map(userGoal, UserGoalResponseDto.class);
        setTextForAnyUserGoal(updatedUserGoal, userId, language);
        return updatedUserGoal;
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
        User user = findByEmail(email);
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
        User user = findByEmail(email);
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
    public List<greencity.dto.user.HabitDictionaryDto> getAvailableHabitDictionary(Long userId, String language) {
        List<HabitDictionaryTranslation> availableHabitDictionary = habitDictionaryTranslationRepo
            .findAvailableHabitDictionaryByUser(userId, language);
        if (availableHabitDictionary.isEmpty()) {
            throw new UserHasNoAvailableHabitDictionaryException(USER_HAS_NO_AVAILABLE_HABIT_DICTIONARY);
        }
        return habitDictionaryDtos(availableHabitDictionary);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitCreateDto> createUserHabit(Long userId, List<HabitIdDto> habitIdDto, String language) {
        if (checkHabitId(userId, habitIdDto)) {
            User user = userRepo.findById(userId)
                .orElseThrow(() -> new WrongIdException(USER_NOT_FOUND_BY_ID + userId));
            List<Habit> habits = habitRepo.saveAll(convertToHabit(habitIdDto, user));
            return convertToHabitCreateDto(habits, language);
        } else {
            throw new WrongIdException(ErrorMessage.HABIT_IS_SAVED);
        }
    }

    /**
     * Method convert HabitIdDto to Habit.
     *
     * @param habitIdDtos {@link HabitIdDto}
     * @param user        current user.
     * @return list habits.
     */
    private List<Habit> convertToHabit(List<HabitIdDto> habitIdDtos, final User user) {
        List<HabitDictionary> habitDictionaries =
            habitIdDtos.stream()
                .map(HabitIdDto::getHabitDictionaryId)
                .map(habitDictionaryService::findById)
                .collect(Collectors.toList());

        List<Habit> habits = new ArrayList<>();

        for (HabitDictionary habitDictionary : habitDictionaries) {
            Habit habit = modelMapper.map(user, Habit.class);
            habit.setHabitDictionary(habitDictionary);
            habits.add(habit);
        }

        return habits;
    }

    /**
     * Method convert {@link Habit} in list {@link HabitCreateDto}.
     *
     * @param habits   - list {@link Habit}.
     * @param language - languageCode.
     * @return List {@link HabitCreateDto}.
     */
    private List<HabitCreateDto> convertToHabitCreateDto(List<Habit> habits, String language) {
        List<HabitCreateDto> habitCreateDtos = habits
            .stream()
            .map(habit -> modelMapper.map(habit, HabitCreateDto.class))
            .collect(Collectors.toList());

        List<HabitCreateDto> habitCreateDtoResultList = new ArrayList<>();

        for (int i = 0; i < habits.size(); i++) {
            HabitDictionaryTranslation htd = habitService.getHabitDictionaryTranslation(
                habits.get(i), language);
            HabitCreateDto habitCreateDto = habitCreateDtos.get(i);
            HabitDictionaryDto habitDictionaryDto = habitCreateDto.getHabitDictionary();
            habitDictionaryDto.setName(htd.getName());
            habitDictionaryDto.setDescription(htd.getDescription());
            habitCreateDto.setHabitDictionary(habitDictionaryDto);
            habitCreateDtoResultList.add(habitCreateDto);
        }

        return habitCreateDtoResultList;
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko
     */
    @Transactional
    @Override
    public List<CustomGoalResponseDto> getAvailableCustomGoals(Long userId) {
        return modelMapper.map(customGoalRepo.findAllAvailableCustomGoalsForUserId(userId),
            new TypeToken<List<CustomGoalResponseDto>>() {
            }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getActivatedUsersAmount() {
        return userRepo.countAllByUserStatus(UserStatus.ACTIVATED);
    }

    /**
     * Method check is in user habit.
     *
     * @param userId      Id current user.
     * @param habitIdDtos {@link HabitIdDto}
     * @return boolean
     */
    private boolean checkHabitId(Long userId, List<HabitIdDto> habitIdDtos) {
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
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_USER_ID_AND_HABIT_DICTIONARY_ID));
        int countHabit = habitRepo.countHabitByUserId(userId);
        if (!habitStatisticRepo.findAllByHabitId(habit.getId()).isEmpty() && countHabit > 1) {
            habitRepo.updateHabitStatusById(habit.getId(), false);
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
    public void addDefaultHabit(Long userId, String language) {
        if (habitRepo.findByUserIdAndStatusHabit(userId).isEmpty()) {
            HabitIdDto habitIdDto = new HabitIdDto();
            habitIdDto.setHabitDictionaryId(1L);
            createUserHabit(userId, Collections.singletonList(habitIdDto), language);
        }
    }

    private List<greencity.dto.user.HabitDictionaryDto> habitDictionaryDtos(
        List<HabitDictionaryTranslation> habitDictionaryTranslations) {
        List<greencity.dto.user.HabitDictionaryDto> habitDictionaryDtos = new ArrayList<>();
        for (HabitDictionaryTranslation hdt : habitDictionaryTranslations) {
            greencity.dto.user.HabitDictionaryDto hd = new greencity.dto.user.HabitDictionaryDto();
            hd.setId(hdt.getHabitDictionary().getId());
            hd.setName(hdt.getName());
            hd.setDescription(hdt.getDescription());
            hd.setHabitItem(hdt.getHabitItem());
            hd.setImage(hdt.getHabitDictionary().getImage());
            habitDictionaryDtos.add(hd);
        }
        return habitDictionaryDtos;
    }

    /**
     * Get profile picture path {@link String}.
     *
     * @return profile picture path {@link String}
     */
    @Override
    public String getProfilePicturePathByUserId(Long id) {
        String profilePicturePathByUserId = userRepo.getProfilePicturePathByUserId(id);
        if (profilePicturePathByUserId == null){
            throw new NotFoundException(id.toString());
        }
        return profilePicturePathByUserId;
    }
}