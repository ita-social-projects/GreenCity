package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.friends.SixFriendsPageResponceDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.user.*;
import greencity.entity.SocialNetwork;
import greencity.entity.SocialNetworkImage;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.enums.AchievementCategory;
import greencity.enums.AchievementType;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.CheckRepeatingValueException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoRequestException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.CustomGoalRepo;
import greencity.repository.EcoNewsRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitStatisticRepo;
import greencity.repository.SocialNetworkRepo;
import greencity.repository.TipsAndTricksRepo;
import greencity.repository.UserGoalRepo;
import greencity.repository.UserRepo;
import greencity.repository.options.UserFilter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final HabitAssignRepo habitAssignRepo;
    private final HabitAssignService habitAssignService;
    private final GoalTranslationRepo goalTranslationRepo;
    private final FileService fileService;
    private final TipsAndTricksRepo tipsAndTricksRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final SocialNetworkImageService socialNetworkImageService;
    private final HabitStatisticRepo habitStatisticRepo;
    private final SocialNetworkRepo socialNetworkRepo;
    private final AchievementCalculation achievementCalculation;
    /**
     * Autowired mapper.
     */
    private final ModelMapper modelMapper;
    @Value("${greencity.time.after.last.activity}")
    private long timeAfterLastActivity;

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserVO save(UserVO userVO) {
        User user = modelMapper.map(userVO, User.class);
        return modelMapper.map(userRepo.save(user), UserVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO findById(Long id) {
        User user = userRepo.findById(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        return modelMapper.map(user, UserVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVOAchievement findUserForAchievement(Long id) {
        User user = userRepo.findUserForAchievement(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        return modelMapper.map(user, UserVOAchievement.class);
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
        UserVO userVO = findById(dto.getId());
        updateUserFromDto(dto, userVO);
        userRepo.save(modelMapper.map(userVO, User.class));
    }

    /**
     * Method for setting data from {@link UserManagementDto} to {@link UserVO}.
     *
     * @param dto    - dto {@link UserManagementDto} with updated fields.
     * @param userVO {@link UserVO} to be updated.
     * @author Vasyl Zhovnir
     */
    private void updateUserFromDto(UserManagementDto dto, UserVO userVO) {
        userVO.setName(dto.getName());
        userVO.setEmail(dto.getEmail());
        userVO.setRole(dto.getRole());
        userVO.setUserCredo(dto.getUserCredo());
        userVO.setUserStatus(dto.getUserStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        UserVO userVO = findById(id);
        userRepo.delete(modelMapper.map(userVO, User.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO findByEmail(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        return optionalUser.isEmpty() ? null : modelMapper.map(optionalUser.get(), UserVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> findAll() {
        return modelMapper.map(userRepo.findAll(), new TypeToken<List<UserVO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserManagementDto> findUserFriendsByUserId(Long id) {
        return modelMapper.map(userRepo.getAllUserFriends(id),
            new TypeToken<List<UserManagementDto>>() {
            }.getType());
    }

    @Override
    public PageableDto<RecommendedFriendDto> findUsersRecommendedFriends(Pageable pageable, Long userId) {
        Page<UsersFriendDto> friends = userRepo.findUsersRecommendedFriends(pageable, userId);
        List<RecommendedFriendDto> recommendedFriendDtos = friends.get()
            .map(user -> new RecommendedFriendDto(user.getId(), user.getName(), user.getProfilePicture()))
            .collect(Collectors.toList());
        return new PageableDto<>(
            recommendedFriendDtos,
            friends.getTotalElements(),
            friends.getPageable().getPageNumber(),
            friends.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<RecommendedFriendDto> findAllUsersFriends(Pageable pageable, Long userId) {
        Page<User> friends = userRepo.getAllUserFriends(userId, pageable);
        List<RecommendedFriendDto> friendDtos = modelMapper.map(friends.getContent(),
            new TypeToken<List<RecommendedFriendDto>>() {
            }.getType());
        return new PageableDto<>(
            friendDtos,
            friends.getTotalElements(),
            friends.getPageable().getPageNumber(),
            friends.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void acceptFriendRequest(Long userId, Long friendId) {
        checkFriendRequest(userId, friendId);
        userRepo.acceptFriendRequest(userId, friendId);
    }

    private void checkFriendRequest(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new CheckRepeatingValueException(ErrorMessage.OWN_USER_ID + friendId);
        }
        UserVO friend = findById(friendId);
        List<UserVO> users = getAllUserFriendRequests(userId);
        if (!users.contains(friend)) {
            throw new UserHasNoRequestException(ErrorMessage.NOT_FOUND_REQUEST + friendId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void declineFriendRequest(Long userId, Long friendId) {
        checkFriendRequest(userId, friendId);
        userRepo.declineFriendRequest(userId, friendId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public PageableDto<RecommendedFriendDto> getAllUserFriendRequests(Long userId, Pageable pageable) {
        Page<User> friendsRequests = userRepo.getAllUserFriendRequests(userId, pageable);
        List<RecommendedFriendDto> friendDtos = modelMapper.map(friendsRequests.getContent(),
            new TypeToken<List<RecommendedFriendDto>>() {
            }.getType());
        return new PageableDto<>(
            friendDtos,
            friendsRequests.getTotalElements(),
            friendsRequests.getPageable().getPageNumber(),
            friendsRequests.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> getAllUserFriendRequests(Long userId) {
        List<User> allUserFriends = userRepo.getAllUserFriendRequests(userId);
        return modelMapper.map(allUserFriends, new TypeToken<List<UserVO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserVO> findNotDeactivatedByEmail(String email) {
        return Optional.of(modelMapper.map(userRepo.findNotDeactivatedByEmail(email), UserVO.class));
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
    public UserRoleDto updateRole(Long id, Role role, String email) {
        checkUpdatableUser(id, email);
        UserVO userVO = findById(id);
        userVO.setRole(role);
        User map = modelMapper.map(userVO, User.class);
        return modelMapper.map(userRepo.save(map), UserRoleDto.class);
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
        User map = modelMapper.map(userVO, User.class);
        return modelMapper.map(userRepo.save(map), UserStatusDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleDto getRoles() {
        return new RoleDto(Role.class.getEnumConstants());
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
    public UserVO updateLastVisit(UserVO userVO) {
        UserVO user = findById(userVO.getId());
        log.info(user.getLastVisit() + "s");
        userVO.setLastVisit(LocalDateTime.now());
        User updatable = modelMapper.map(userVO, User.class);
        return modelMapper.map(userRepo.save(updatable), UserVO.class);
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
            userRepo.findByEmail(email)
                .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email)),
            UserUpdateDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserUpdateDto update(UserUpdateDto dto, String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        user.setName(dto.getName());
        user.setEmailNotification(dto.getEmailNotification());
        userRepo.save(user);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateUserRefreshToken(String refreshTokenKey, Long id) {
        return userRepo.updateUserRefreshToken(refreshTokenKey, id);
    }

    /**
     * Method which check that, if admin/moderator update role/status of himself,
     * then throw exception.
     *
     * @param id    id of updatable user.
     * @param email email of admin/moderator.
     * @author Rostyslav Khasanov
     */
    private void checkUpdatableUser(Long id, String email) {
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
     * @author Rostyslav Khasanov
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
    public void addDefaultHabit(UserVO user, String language) {
        if (habitAssignRepo.findAllByUserId(user.getId()).isEmpty()) {
            UserVO userVO = modelMapper.map(user, UserVO.class);
            habitAssignService.assignDefaultHabitForUser(1L, userVO);
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
            .orElseThrow(() -> new NotFoundException(ErrorMessage.PROFILE_PICTURE_NOT_FOUND_BY_ID + id.toString()));
    }

    /**
     * Update user profile picture {@link UserVO}.
     *
     * @param image                 {@link MultipartFile}
     * @param email                 {@link String} - email of user that need to
     *                              update.
     * @param userProfilePictureDto {@link UserProfilePictureDto}
     * @return {@link UserVO}.
     * @author Marian Datsko
     */
    @Override
    public UserVO updateUserProfilePicture(MultipartFile image, String email,
                                           UserProfilePictureDto userProfilePictureDto) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        if (userProfilePictureDto.getProfilePicturePath() != null) {
            image = fileService.convertToMultipartImage(userProfilePictureDto.getProfilePicturePath());
        }
        if (image != null) {
            user.setProfilePicturePath(fileService.upload(image).toString());
        } else {
            throw new BadRequestException(ErrorMessage.IMAGE_EXISTS);
        }
        return modelMapper.map(userRepo.save(user), UserVO.class);
    }

    /**
     * Delete user profile picture {@link UserVO}.
     *
     * @param email {@link String} - email of user that need to update.
     */
    @Override
    public void deleteUserProfilePicture(String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        user.setProfilePicturePath(null);
        userRepo.save(user);
    }

    /**
     * Get list user friends by user id {@link UserVO}.
     *
     * @param userId {@link Long}
     * @return {@link UserVO}.
     * @author Marian Datsko
     */
    @Override
    public List<UserVO> getAllUserFriends(Long userId) {
        List<User> allUserFriends = userRepo.getAllUserFriends(userId);
        return modelMapper.map(allUserFriends, new TypeToken<List<UserVO>>() {
        }.getType());
    }

    /**
     * Delete user friend by id {@link UserVO}.
     *
     * @param userId   {@link Long}g
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    @Override
    @Transactional
    public void deleteUserFriendById(Long userId, Long friendId) {
        List<UserVO> allUserFriends = getAllUserFriends(userId);
        findById(friendId);
        if (userId.equals(friendId)) {
            throw new CheckRepeatingValueException(ErrorMessage.OWN_USER_ID + friendId);
        }
        if (!allUserFriends.isEmpty()) {
            List<Long> listUserId = allUserFriends.stream().map(UserVO::getId).collect(Collectors.toList());
            if (listUserId.contains(friendId)) {
                userRepo.deleteUserFriendById(userId, friendId);
            } else {
                throw new NotDeletedException(ErrorMessage.USER_FRIENDS_LIST + friendId);
            }
        } else {
            throw new NotDeletedException(ErrorMessage.USER_FRIENDS_LIST + friendId);
        }
    }

    /**
     * Add new user friend {@link UserVO}.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    @Override
    @Transactional
    public void addNewFriend(Long userId, Long friendId) {
        List<UserVO> allUserFriends = getAllUserFriends(userId);

        findById(friendId);
        if (userId.equals(friendId)) {
            throw new CheckRepeatingValueException(ErrorMessage.OWN_USER_ID + friendId);
        }
        if (!allUserFriends.isEmpty()) {
            allUserFriends.forEach(user -> {
                if (user.getId().equals(friendId)) {
                    throw new CheckRepeatingValueException(ErrorMessage.FRIEND_EXISTS + friendId);
                }
            });
        }
        userRepo.addNewFriend(userId, friendId);
    }

    /**
     * Get six friends with the highest rating {@link UserVO}.
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
            throw new NotFoundException(ErrorMessage.NOT_FOUND_ANY_FRIENDS + userId.toString());
        }
        return userProfilePictureDtoList;
    }

    /**
     * Get six friends with the highest rating {@link UserVO}. by page.
     *
     * @param userId {@link Long}
     * @return {@link SixFriendsPageResponceDto}.
     * @author Oleh Bilonizhka
     */
    @Override
    public SixFriendsPageResponceDto getSixFriendsWithTheHighestRatingPaged(Long userId) {
        Pageable pageable = PageRequest.of(0, 6);
        List<User> users = userRepo.getSixFriendsWithTheHighestRating(userId);
        Page<User> pageUsers = new PageImpl<>(users, pageable, users.size());

        List<UserProfilePictureDto> userProfilePictureDtoList = users
            .stream()
            .map(user -> modelMapper.map(user, UserProfilePictureDto.class))
            .collect(Collectors.toList());

        return SixFriendsPageResponceDto.builder()
            .pagedFriends(getPageableDto(userProfilePictureDtoList,
                pageUsers))
            .amountOfFriends(userRepo.getAllUserFriendsCount(userId)).build();
    }

    private PageableDto<UserProfilePictureDto> getPageableDto(
        List<UserProfilePictureDto> userProfilePictureDtoList, Page<User> pageUsers) {
        return new PageableDto<>(
            userProfilePictureDtoList,
            pageUsers.getTotalElements(),
            pageUsers.getPageable().getPageNumber(),
            pageUsers.getTotalPages());
    }

    /**
     * Save user profile information {@link UserVO}.
     *
     * @author Marian Datsko
     */
    @Override
    public UserProfileDtoResponse saveUserProfile(UserProfileDtoRequest userProfileDtoRequest, String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        user.setFirstName(userProfileDtoRequest.getFirstName());
        user.setCity(userProfileDtoRequest.getCity());
        user.setUserCredo(userProfileDtoRequest.getUserCredo());
        user.getSocialNetworks().forEach(socialNetwork -> socialNetworkRepo.deleteById(socialNetwork.getId()));
        user.getSocialNetworks().clear();
        user.getSocialNetworks().addAll(userProfileDtoRequest.getSocialNetworks()
            .stream()
            .map(url -> SocialNetwork.builder()
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
        CompletableFuture.runAsync(() -> achievementCalculation
            .calculateAchievement(user.getId(), AchievementType.SETTER,
                AchievementCategory.SOCIAL_NETWORK, user.getSocialNetworks().size()));
        return modelMapper.map(user, UserProfileDtoResponse.class);
    }

    /**
     * Method return user profile information {@link UserVO}.
     *
     * @author Marian Datsko
     */
    @Override
    public UserProfileDtoResponse getUserProfileInformation(Long userId) {
        User user = userRepo
            .findById(userId)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
        if (user.getFirstName() == null) {
            user.setFirstName(user.getName());
        }
        return modelMapper.map(user, UserProfileDtoResponse.class);
    }

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link UserVO}'s id
     * @param userLastActivityTime - new {@link UserVO}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    @Override
    public void updateUserLastActivityTime(Long userId, Date userLastActivityTime) {
        userRepo.updateUserLastActivityTime(userId, userLastActivityTime);
    }

    /**
     * The method checks by id if a {@link UserVO} is online.
     *
     * @param userId {@link Long}
     * @return {@link Boolean}.
     * @author Yurii Zhurakovskyi
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
     * Method return user profile statistics {@link UserVO}.
     *
     * @param userId - {@link UserVO}'s id
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
     * Get user and six friends with the online status {@link UserVO}.
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
     * Get user and all friends with the online status {@link UserVO} by page.
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
        UserVO foundUser = findById(id);
        foundUser.setUserStatus(UserStatus.DEACTIVATED);
        userRepo.save(modelMapper.map(foundUser, User.class));
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
        UserVO foundUser = findById(id);
        foundUser.setUserStatus(UserStatus.ACTIVATED);
        userRepo.save(modelMapper.map(foundUser, User.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserVO> findByIdAndToken(Long userId, String token) {
        User foundUser = modelMapper.map(findById(userId), User.class);

        VerifyEmail verifyEmail = foundUser.getVerifyEmail();
        if (verifyEmail != null && verifyEmail.getToken().equals(token)) {
            return Optional.of(modelMapper.map(foundUser, UserVO.class));
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
