package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.user.*;
import greencity.entity.*;
import greencity.enums.EmailNotification;
import greencity.enums.GoalStatus;
import greencity.enums.ROLE;
import greencity.enums.UserStatus;
import greencity.entity.localization.GoalTranslation;
import greencity.exception.exceptions.*;
import greencity.repository.*;
import greencity.repository.options.UserFilter;
import greencity.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static greencity.constant.ErrorMessage.*;

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
    private final HabitStatisticRepo habitStatisticRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final HabitAssignService habitAssignService;
    private final HabitTranslationRepo habitTranslationRepo;
    private final GoalTranslationRepo goalTranslationRepo;
    private final FileService fileService;
    private final TipsAndTricksRepo tipsAndTricksRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final SocialNetworkImageService socialNetworkImageService;
    @Value("${greencity.time.after.last.activity}")
    private long timeAfterLastActivity;
    @Value("${defaultProfilePicture}")
    private String defaultProfilePicture;

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
            users.getPageable().getPageNumber(),
            users.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<UserManagementDto> findUserForManagementByPage(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);
        List<UserManagementDto> userManagementDtos =
            users.getContent().stream()
                .map(user -> modelMapper.map(user, UserManagementDto.class))
                .collect(Collectors.toList());
        return new PageableAdvancedDto<>(
            userManagementDtos,
            users.getTotalElements(),
            users.getPageable().getPageNumber(),
            users.getTotalPages(),
            users.getNumber(),
            users.hasPrevious(),
            users.hasNext(),
            users.isFirst(),
            users.isLast());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUser(UserManagementDto dto) {
        User user = findById(dto.getId());
        updateUserFromDto(dto, user);
        userRepo.save(user);
    }

    /**
     * Method for setting data from {@link UserManagementDto} to {@link User}.
     *
     * @param dto  - dto {@link UserManagementDto} with updated fields.
     * @param user {@link User} to be updated.
     * @author Vasyl Zhovnir
     */
    private void updateUserFromDto(UserManagementDto dto, User user) {
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setUserCredo(dto.getUserCredo());
        user.setUserStatus(dto.getUserStatus());
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
     */
    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findNotDeactivatedByEmail(String email) {
        return userRepo.findNotDeactivatedByEmail(email);
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
            users.getPageable().getPageNumber(),
            users.getTotalPages());
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
    public UserUpdateDto update(UserUpdateDto dto, String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(USER_NOT_FOUND_BY_EMAIL + email));
        user.setName(dto.getName());
        user.setEmailNotification(dto.getEmailNotification());
        userRepo.save(user);
        return dto;
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
        userGoalResponseDtos.forEach(el -> setTextForAnyUserGoal(el, userId, language));
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
            ? goalTranslationRepo.findByUserIdLangAndUserGoalId(userId, language, dto.getId()).getContent()
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
     * {@inheritDoc}
     */
    @Override
    public void addDefaultHabit(User user, String language) {
        if (habitAssignRepo.findAllByUserId(user.getId()).isEmpty()) {
            UserVO userVO = modelMapper.map(user, UserVO.class);
            habitAssignService.assignHabitForUser(1L, userVO);
        }
    }

    /**
     * Get profile picture path {@link String}.
     *
     * @return profile picture path {@link String}
     */
    @Override
    public String getProfilePicturePathByUserId(Long id) {
        return userRepo
            .getProfilePicturePathByUserId(id)
            .orElseThrow(() -> new NotFoundException(PROFILE_PICTURE_NOT_FOUND_BY_ID + id.toString()));
    }

    /**
     * Update user profile picture {@link User}.
     *
     * @param image                 {@link MultipartFile}
     * @param email                 {@link String} - email of user that need to update.
     * @param userProfilePictureDto {@link UserProfilePictureDto}
     * @return {@link User}.
     * @author Marian Datsko
     */
    @Override
    public User updateUserProfilePicture(MultipartFile image, String email,
                                         UserProfilePictureDto userProfilePictureDto) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(USER_NOT_FOUND_BY_EMAIL + email));
        if (userProfilePictureDto.getProfilePicturePath() != null) {
            image = fileService.convertToMultipartImage(userProfilePictureDto.getProfilePicturePath());
        }
        if (image != null) {
            user.setProfilePicturePath(fileService.upload(image).toString());
        } else {
            throw new BadRequestException(IMAGE_EXISTS);
        }
        return userRepo.save(user);
    }

    /**
     * Delete user profile picture {@link User}.
     *
     * @param email {@link String} - email of user that need to update.
     */
    @Override
    public void deleteUserProfilePicture(String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(USER_NOT_FOUND_BY_EMAIL + email));
        user.setProfilePicturePath(defaultProfilePicture);
        userRepo.save(user);
    }

    /**
     * Get list user friends by user id {@link User}.
     *
     * @param userId {@link Long}
     * @return {@link User}.
     * @author Marian Datsko
     */
    @Override
    public List<User> getAllUserFriends(Long userId) {
        return userRepo.getAllUserFriends(userId);
    }

    /**
     * Delete user friend by id {@link User}.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    @Override
    @Transactional
    public void deleteUserFriendById(Long userId, Long friendId) {
        List<User> allUserFriends = getAllUserFriends(userId);
        findById(friendId);
        if (userId.equals(friendId)) {
            throw new CheckRepeatingValueException(OWN_USER_ID + friendId);
        }
        if (!allUserFriends.isEmpty()) {
            List<Long> listUserId = allUserFriends.stream().map(User::getId).collect(Collectors.toList());
            if (listUserId.contains(friendId)) {
                userRepo.deleteUserFriendById(userId, friendId);
            } else {
                throw new NotDeletedException(USER_FRIENDS_LIST + friendId);
            }
        } else {
            throw new NotDeletedException(USER_FRIENDS_LIST + friendId);
        }
    }

    /**
     * Add new user friend {@link User}.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    @Override
    @Transactional
    public void addNewFriend(Long userId, Long friendId) {
        List<User> allUserFriends = getAllUserFriends(userId);
        findById(friendId);
        if (userId.equals(friendId)) {
            throw new CheckRepeatingValueException(OWN_USER_ID + friendId);
        }
        if (!allUserFriends.isEmpty()) {
            allUserFriends.forEach(user -> {
                if (user.getId().equals(friendId)) {
                    throw new CheckRepeatingValueException(FRIEND_EXISTS + friendId);
                }
            });
        }
        userRepo.addNewFriend(userId, friendId);
    }

    /**
     * Get six friends with the highest rating {@link User}.
     *
     * @param userId {@link Long}
     * @author Marian Datsko
     */
    @Override
    public List<UserProfilePictureDto> getSixFriendsWithTheHighestRating(Long userId) {
        List<UserProfilePictureDto> userProfilePictureDtoList = userRepo.getSixFriendsWithTheHighestRating(userId)
            .stream()
            .map(user -> modelMapper.map(user, UserProfilePictureDto.class))
            .collect(Collectors.toList());
        if (userProfilePictureDtoList.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_ANY_FRIENDS + userId.toString());
        }
        return userProfilePictureDtoList;
    }

    /**
     * Save user profile information {@link User}.
     *
     * @author Marian Datsko
     */
    @Override
    public UserProfileDtoResponse saveUserProfile(UserProfileDtoRequest userProfileDtoRequest, String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(USER_NOT_FOUND_BY_EMAIL + email));
        user.setFirstName(userProfileDtoRequest.getFirstName());
        user.setCity(userProfileDtoRequest.getCity());
        user.setUserCredo(userProfileDtoRequest.getUserCredo());
        user.getSocialNetworks().clear();
        user.getSocialNetworks().addAll(userProfileDtoRequest.getSocialNetworks()
            .stream()
            .map(url ->
                SocialNetwork.builder()
                    .url(url)
                    .user(user)
                    .socialNetworkImage(modelMapper.map(socialNetworkImageService.getSocialNetworkImageByUrl(url),
                        SocialNetworkImage.class))
                    .build())
            .collect(Collectors.toList()));
        user.setShowLocation(userProfileDtoRequest.getShowLocation());
        user.setShowEcoPlace(userProfileDtoRequest.getShowEcoPlace());
        user.setShowShoppingList(userProfileDtoRequest.getShowShoppingList());
        userRepo.save(user);
        return modelMapper.map(user, UserProfileDtoResponse.class);
    }

    /**
     * Method return user profile information {@link User}.
     *
     * @author Marian Datsko
     */
    @Override
    public UserProfileDtoResponse getUserProfileInformation(Long userId) {
        User user = userRepo
            .findById(userId)
            .orElseThrow(() -> new WrongIdException(USER_NOT_FOUND_BY_ID + userId));
        if (user.getFirstName() == null) {
            user.setFirstName(user.getName());
        }
        return modelMapper.map(user, UserProfileDtoResponse.class);
    }

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link User}'s id
     * @param userLastActivityTime - new {@link User}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    @Override
    public void updateUserLastActivityTime(Long userId, Date userLastActivityTime) {
        userRepo.updateUserLastActivityTime(userId, userLastActivityTime);
    }

    /**
     * The method checks by id if a {@link User} is online.
     *
     * @param userId {@link Long}
     * @return {@link Boolean}.
     * @author Yurii Zhurakovskyi
     */
    @Override
    public boolean checkIfTheUserIsOnline(Long userId) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new WrongIdException(USER_NOT_FOUND_BY_ID + userId);
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
     * Method return user profile statistics {@link User}.
     *
     * @param userId - {@link User}'s id
     * @author Marian Datsko
     */
    @Override
    public UserProfileStatisticsDto getUserProfileStatistics(Long userId) {
        Long amountOfPublishedNewsByUserId = ecoNewsRepo.getAmountOfPublishedNewsByUserId(userId);
        Long amountOfWrittenTipsAndTrickByUserId = tipsAndTricksRepo.getAmountOfWrittenTipsAndTrickByUserId(userId);
        Long amountOfAcquiredHabitsByUserId = habitStatisticRepo.getAmountOfAcquiredHabitsByUserId(userId);
        Long amountOfHabitsInProgressByUserId = habitStatisticRepo.getAmountOfHabitsInProgressByUserId(userId);

        return UserProfileStatisticsDto.builder()
            .amountWrittenTipsAndTrick(amountOfWrittenTipsAndTrickByUserId)
            .amountPublishedNews(amountOfPublishedNewsByUserId)
            .amountHabitsAcquired(amountOfAcquiredHabitsByUserId)
            .amountHabitsInProgress(amountOfHabitsInProgressByUserId)
            .build();
    }

    /**
     * Get user and six friends with the online status {@link User}.
     *
     * @param userId {@link Long}
     * @author Yurii Zhurakovskyi
     */
    @Override
    public UserAndFriendsWithOnlineStatusDto getUserAndSixFriendsWithOnlineStatus(Long userId) {
        UserWithOnlineStatusDto userWithOnlineStatusDto = UserWithOnlineStatusDto.builder()
            .id(userId)
            .onlineStatus(checkIfTheUserIsOnline(userId))
            .build();
        List<User> sixFriendsWithTheHighestRating = userRepo.getSixFriendsWithTheHighestRating(userId);
        List<UserWithOnlineStatusDto> sixFriendsWithOnlineStatusDtos = new ArrayList<>();
        if (!sixFriendsWithTheHighestRating.isEmpty()) {
            sixFriendsWithOnlineStatusDtos = sixFriendsWithTheHighestRating
                .stream()
                .map(u -> new UserWithOnlineStatusDto(u.getId(), checkIfTheUserIsOnline(u.getId())))
                .collect(Collectors.toList());
        }
        return UserAndFriendsWithOnlineStatusDto.builder()
            .user(userWithOnlineStatusDto)
            .friends(sixFriendsWithOnlineStatusDtos)
            .build();
    }

    /**
     * Get user and all friends with the online status {@link User} by page.
     *
     * @param userId   {@link Long}
     * @param pageable {@link Pageable }
     * @author Yurii Zhurakovskyi
     */
    @Override
    public UserAndAllFriendsWithOnlineStatusDto getAllFriendsWithTheOnlineStatus(Long userId, Pageable pageable) {
        UserWithOnlineStatusDto userWithOnlineStatusDto = UserWithOnlineStatusDto.builder()
            .id(userId)
            .onlineStatus(checkIfTheUserIsOnline(userId))
            .build();
        Page<User> friends = userRepo.getAllUserFriends(userId, pageable);
        List<UserWithOnlineStatusDto> friendsWithOnlineStatusDtos = new ArrayList<>();
        if (!friends.isEmpty()) {
            friendsWithOnlineStatusDtos = friends
                .getContent()
                .stream()
                .map(u -> new UserWithOnlineStatusDto(u.getId(), checkIfTheUserIsOnline(u.getId())))
                .collect(Collectors.toList());
        }
        return UserAndAllFriendsWithOnlineStatusDto.builder()
            .user(userWithOnlineStatusDto)
            .friends(new PageableDto<>(friendsWithOnlineStatusDtos, friends.getTotalElements(),
                friends.getPageable().getPageNumber(), friends.getTotalPages()))
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deactivateUser(Long id) {
        User foundUser = findById(id);
        foundUser.setUserStatus(UserStatus.DEACTIVATED);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<Long> deactivateAllUsers(List<Long> listId) {
        userRepo.deactivateSelectedUsers(listId);
        return listId;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void setActivatedStatus(Long id) {
        User foundUser = findById(id);
        foundUser.setUserStatus(UserStatus.ACTIVATED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByIdAndToken(Long userId, String token) {
        User foundUser = findById(userId);
        VerifyEmail verifyEmail = foundUser.getVerifyEmail();
        if (verifyEmail != null && verifyEmail.getToken().equals(token)) {
            return Optional.of(foundUser);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<UserManagementDto> searchBy(Pageable paging, String query) {
        Page<User> page = userRepo.searchBy(paging, query);
        List<UserManagementDto> users = page.stream()
            .map(user -> modelMapper.map(user, UserManagementDto.class))
            .collect(Collectors.toList());
        return new PageableAdvancedDto<>(
            users,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages(),
            page.getNumber(),
            page.hasPrevious(),
            page.hasNext(),
            page.isFirst(),
            page.isLast());
    }
}
