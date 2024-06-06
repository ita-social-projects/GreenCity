package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.habit.HabitAssignCustomPropertiesDto;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.dto.habit.HabitAssignPropertiesDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitEnrollDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habit.HabitsDateEnrollmentDto;
import greencity.dto.habit.HabitAssignPreviewDto;
import greencity.dto.habit.UserShoppingAndCustomShoppingListsDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemWithStatusSaveRequestDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.shoppinglistitem.ShoppingListItemWithStatusRequestDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.CustomShoppingListItem;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import greencity.entity.HabitTranslation;
import greencity.entity.ShoppingListItem;
import greencity.entity.User;
import greencity.entity.UserShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.AchievementAction;
import greencity.enums.AchievementCategoryType;
import greencity.enums.HabitAssignStatus;
import greencity.enums.RatingCalculationEnum;
import greencity.enums.ShoppingListItemStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.CustomShoppingListItemNotSavedException;
import greencity.exception.exceptions.InvalidStatusException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyHasEnrolledHabitAssign;
import greencity.exception.exceptions.UserAlreadyHasHabitAssignedException;
import greencity.exception.exceptions.UserAlreadyHasMaxNumberOfActiveHabitAssigns;
import greencity.exception.exceptions.UserHasNoFriendWithIdException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.exception.exceptions.UserHasReachedOutOfEnrollRange;
import greencity.message.HabitAssignNotificationMessage;
import greencity.rating.RatingCalculation;
import greencity.repository.CustomShoppingListItemRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitStatusCalendarRepo;
import greencity.repository.ShoppingListItemRepo;
import greencity.repository.ShoppingListItemTranslationRepo;
import greencity.repository.UserRepo;
import greencity.repository.UserShoppingListItemRepo;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of {@link HabitAssignService}.
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class HabitAssignServiceImpl implements HabitAssignService {
    private static final Logger log = LoggerFactory.getLogger(HabitAssignServiceImpl.class);
    private final HabitAssignRepo habitAssignRepo;
    private final HabitRepo habitRepo;
    private final UserRepo userRepo;
    private final ShoppingListItemRepo shoppingListItemRepo;
    private final UserShoppingListItemRepo userShoppingListItemRepo;
    private final CustomShoppingListItemRepo customShoppingListItemRepo;
    private final ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;
    private final HabitStatusCalendarRepo habitStatusCalendarRepo;
    private final ShoppingListItemService shoppingListItemService;
    private final CustomShoppingListItemService customShoppingListItemService;
    private final HabitStatisticService habitStatisticService;
    private final HabitStatusCalendarService habitStatusCalendarService;
    private final AchievementCalculation achievementCalculation;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final RatingCalculation ratingCalculation;
    private final NotificationService notificationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitAssignDto getByHabitAssignIdAndUserId(Long habitAssignId, Long userId, String language) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        if (!habitAssign.getUser().getId().equals(userId)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        HabitAssignDto habitAssignDto = buildHabitAssignDto(habitAssign, language);
        HabitDto habitDto = habitAssignDto.getHabit();
        Long amountAcquiredUsers = habitAssignRepo.findAmountOfUsersAcquired(habitDto.getId());
        habitDto.setAmountAcquiredUsers(amountAcquiredUsers);
        habitDto.setUsersIdWhoCreatedCustomHabit(habitAssign.getHabit().getUserId());
        habitAssignDto.setHabit(habitDto);
        habitAssignDto.setProgressNotificationHasDisplayed(habitAssign.getProgressNotificationHasDisplayed());

        return habitAssignDto;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignManagementDto assignDefaultHabitForUser(Long habitId, UserVO userVO) {
        checkStatusInProgressExists(habitId, userVO);

        User user = modelMapper.map(userVO, User.class);
        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId));
        validateHabitForAssign(habitId, user);
        HabitAssign habitAssign =
            habitAssignRepo.findByHabitIdAndUserIdAndStatusIsCancelled(habitId, user.getId());

        if (habitAssign != null) {
            habitAssign.setStatus(HabitAssignStatus.INPROGRESS);
            habitAssign.setCreateDate(ZonedDateTime.now());
        } else {
            List<Long> allShoppingListItemId =
                shoppingListItemRepo.getAllShoppingListItemIdByHabitIdISContained(habitId);
            habitAssign = buildHabitAssign(habit, user, HabitAssignStatus.INPROGRESS);
            if (!allShoppingListItemId.isEmpty()) {
                List<ShoppingListItem> shoppingList =
                    shoppingListItemRepo.getShoppingListByListOfId(allShoppingListItemId);
                saveUserShoppingListItems(shoppingList, habitAssign);
            }
        }

        enhanceAssignWithDefaultProperties(habitAssign);
        habitAssign.setProgressNotificationHasDisplayed(false);

        HabitAssignManagementDto habitAssignManagementDto =
            modelMapper.map(habitAssign, HabitAssignManagementDto.class);
        habitAssignManagementDto.setProgressNotificationHasDisplayed(habitAssign.getProgressNotificationHasDisplayed());
        return habitAssignManagementDto;
    }

    /**
     * Method updates {@link HabitAssign} with default properties.
     *
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void enhanceAssignWithDefaultProperties(HabitAssign habitAssign) {
        habitAssign.setDuration(habitAssign.getHabit().getDefaultDuration());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<HabitAssignManagementDto> assignCustomHabitForUser(Long habitId, UserVO userVO,
        HabitAssignCustomPropertiesDto habitAssignCustomPropertiesDto) {
        User user = modelMapper.map(userVO, User.class);

        checkStatusInProgressExists(habitId, userVO);

        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId));
        validateHabitForAssign(habitId, user);
        HabitAssign habitAssign =
            habitAssignRepo.findByHabitIdAndUserIdAndStatusIsCancelled(habitId, user.getId());
        if (habitAssign != null) {
            habitAssign.setStatus(HabitAssignStatus.INPROGRESS);
            habitAssign.setCreateDate(ZonedDateTime.now());
        } else {
            habitAssign = buildHabitAssign(habit, user, HabitAssignStatus.INPROGRESS);
        }
        enhanceAssignWithCustomProperties(habitAssign, habitAssignCustomPropertiesDto.getHabitAssignPropertiesDto());

        if (!habitAssignCustomPropertiesDto.getHabitAssignPropertiesDto().getDefaultShoppingListItems().isEmpty()) {
            List<ShoppingListItem> shoppingList =
                shoppingListItemRepo
                    .getShoppingListByListOfId(habitAssignCustomPropertiesDto.getHabitAssignPropertiesDto()
                        .getDefaultShoppingListItems());
            saveUserShoppingListItems(shoppingList, habitAssign);
        }
        setDefaultShoppingListItemsIntoCustomHabit(habitAssign,
            habitAssignCustomPropertiesDto.getHabitAssignPropertiesDto().getDefaultShoppingListItems());
        saveCustomShoppingListItemList(habitAssignCustomPropertiesDto.getCustomShoppingListItemList(), user, habit);

        habitAssignRepo.save(habitAssign);

        List<HabitAssignManagementDto> habitAssignManagementDtoList = new ArrayList<>();
        habitAssignManagementDtoList.add(modelMapper.map(habitAssign, HabitAssignManagementDto.class));

        if (!CollectionUtils.isEmpty(habitAssignCustomPropertiesDto.getFriendsIdsList())) {
            assignFriendsForCustomHabit(habit, userVO.getId(), habitAssignCustomPropertiesDto,
                habitAssignManagementDtoList);
        }

        return habitAssignManagementDtoList;
    }

    private void saveCustomShoppingListItemList(List<CustomShoppingListItemSaveRequestDto> saveList,
        User user, Habit habit) {
        if (!CollectionUtils.isEmpty(saveList)) {
            saveList.forEach(item -> {
                CustomShoppingListItem customShoppingListItem = modelMapper.map(item, CustomShoppingListItem.class);
                List<CustomShoppingListItem> duplicates = user.getCustomShoppingListItems().stream()
                    .filter(userItem -> userItem.getText().equals(customShoppingListItem.getText()))
                    .collect(Collectors.toList());
                if (duplicates.isEmpty()) {
                    customShoppingListItem.setUser(user);
                    customShoppingListItem.setHabit(habit);
                    user.getCustomShoppingListItems().add(customShoppingListItem);
                    customShoppingListItemRepo.save(customShoppingListItem);
                } else {
                    throw new CustomShoppingListItemNotSavedException(String.format(
                        ErrorMessage.CUSTOM_SHOPPING_LIST_ITEM_EXISTS, customShoppingListItem.getText()));
                }
            });
        }
    }

    private void assignFriendsForCustomHabit(Habit habit,
        Long userId,
        HabitAssignCustomPropertiesDto habitAssignCustomPropertiesDto,
        List<HabitAssignManagementDto> habitAssignManagementDtoList) {
        List<User> usersWhoShouldBeFriendList = habitAssignCustomPropertiesDto.getFriendsIdsList().stream()
            .map(id -> userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " doesn't exist")))
            .collect(Collectors.toList());

        for (User friendOfUser : usersWhoShouldBeFriendList) {
            if (!userRepo.isFriend(userId, friendOfUser.getId())) {
                throw new UserHasNoFriendWithIdException(
                    ErrorMessage.USER_HAS_NO_FRIEND_WITH_ID + friendOfUser.getId());
            }
            checkStatusInProgressExists(habit.getId(), UserVO.builder().id(friendOfUser.getId()).build());
            validateHabitForAssign(habit.getId(), friendOfUser);
            HabitAssign habitAssign =
                habitAssignRepo.findByHabitIdAndUserIdAndStatusIsCancelled(habit.getId(), friendOfUser.getId());
            if (habitAssign != null) {
                habitAssign.setStatus(HabitAssignStatus.REQUESTED);
                habitAssign.setCreateDate(ZonedDateTime.now());
            } else {
                habitAssign = buildHabitAssign(habit, friendOfUser, HabitAssignStatus.REQUESTED);
            }
            enhanceAssignWithCustomProperties(habitAssign,
                habitAssignCustomPropertiesDto.getHabitAssignPropertiesDto());
            setDefaultShoppingListItemsIntoCustomHabit(habitAssign,
                habitAssignCustomPropertiesDto.getHabitAssignPropertiesDto().getDefaultShoppingListItems());
            habitAssignRepo.save(habitAssign);
            habitAssignManagementDtoList.add(modelMapper.map(habitAssign, HabitAssignManagementDto.class));
        }
    }

    private void setDefaultShoppingListItemsIntoCustomHabit(HabitAssign habitAssign,
        List<Long> defaultShoppingListItems) {
        if (!defaultShoppingListItems.isEmpty()) {
            List<ShoppingListItem> shoppingList =
                shoppingListItemRepo.getShoppingListByListOfId(defaultShoppingListItems);
            saveUserShoppingListItems(shoppingList, habitAssign);
        }
    }

    private void checkStatusInProgressExists(Long habitId, UserVO userVO) {
        List<HabitAssign> habits = habitAssignRepo.findAllByUserId(userVO.getId());
        boolean habitInProgress = habits.stream()
            .filter(h -> h.getHabit().getId().equals(habitId))
            .anyMatch(h -> h.getStatus().equals(HabitAssignStatus.INPROGRESS));

        if (habitInProgress) {
            throw new UserAlreadyHasHabitAssignedException(
                ErrorMessage.USER_ALREADY_HAS_ASSIGNED_HABIT + habitId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllCustomHabitAssignsByUserId(Long userId, String language) {
        return habitAssignRepo.findAllByUserId(userId)
            .stream()
            .filter(this::isHabitCustom)
            .map(habitAssign -> buildHabitAssignDtoContent(habitAssign, language)).collect(Collectors.toList());
    }

    /**
     * Method checks if {@link HabitAssign} is custom.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @return boolean.
     */
    private boolean isHabitCustom(HabitAssign habitAssign) {
        Integer duration = habitAssign.getDuration();
        Integer defaultDuration = habitAssign.getHabit().getDefaultDuration();
        List<UserShoppingListItem> shoppingListItems = habitAssign.getUserShoppingListItems();
        return !duration.equals(defaultDuration) && !shoppingListItems.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZonedDateTime getEndDate(HabitAssignDto habitAssign) {
        return habitAssign.getCreateDateTime().plusDays(habitAssign.getDuration());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getReadinessPercent(HabitAssignDto habitAssign) {
        return habitAssign.getWorkingDays() * 100 / habitAssign.getDuration();
    }

    /**
     * Method which updates duration of habit assigned for user.
     *
     * @param habitAssignId {@code AssignHabit} id.
     * @param userId        {@link Long} id.
     * @param duration      {@link Integer} with needed duration.
     * @return {@link HabitAssignUserDurationDto}.
     */
    @Transactional
    @Override
    public HabitAssignUserDurationDto updateUserHabitInfoDuration(Long habitAssignId, Long userId, Integer duration) {
        if (!habitAssignRepo.existsById(habitAssignId)) {
            throw new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitAssignId);
        }
        HabitAssign habitAssign = habitAssignRepo.findByHabitAssignIdUserIdAndStatusIsInProgress(habitAssignId, userId)
            .orElseThrow(() -> new InvalidStatusException(
                ErrorMessage.HABIT_ASSIGN_STATUS_IS_NOT_INPROGRESS_OR_USER_HAS_NOT_ANY_ASSIGNED_HABITS));
        if (duration < habitAssign.getWorkingDays()) {
            throw new BadRequestException(ErrorMessage.INVALID_DURATION);
        }

        habitAssign.setDuration(duration);
        return modelMapper.map(habitAssignRepo.save(habitAssign), HabitAssignUserDurationDto.class);
    }

    private void saveUserShoppingListItems(List<ShoppingListItem> shoppingList, HabitAssign habitAssign) {
        List<UserShoppingListItem> userShoppingList = new ArrayList<>();
        for (ShoppingListItem shoppingItem : shoppingList) {
            userShoppingList.add(UserShoppingListItem.builder()
                .habitAssign(habitAssign)
                .shoppingListItem(shoppingItem)
                .status(ShoppingListItemStatus.ACTIVE)
                .build());
        }
        userShoppingListItemRepo.saveAll(userShoppingList);
    }

    /**
     * Method updates {@link HabitAssign} with custom properties from
     * {@link HabitAssignPropertiesDto} instance.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @param props       {@link HabitAssignPropertiesDto} instance.
     */
    private void enhanceAssignWithCustomProperties(HabitAssign habitAssign,
        HabitAssignPropertiesDto props) {
        habitAssign.setDuration(props.getDuration());
    }

    /**
     * Method builds {@link HabitAssign} with main props.
     *
     * @param habit        {@link Habit} instance.
     * @param user         {@link User} instance.
     * @param assignStatus {@link HabitAssignStatus} instance.
     * @return {@link HabitAssign} instance.
     */
    private HabitAssign buildHabitAssign(Habit habit, User user, HabitAssignStatus assignStatus) {
        return habitAssignRepo.save(
            HabitAssign.builder()
                .habit(habit)
                .status(assignStatus)
                .createDate(ZonedDateTime.now())
                .user(user)
                .duration(habit.getDefaultDuration())
                .habitStreak(0)
                .workingDays(0)
                .lastEnrollmentDate(ZonedDateTime.now())
                .build());
    }

    /**
     * Method builds {@link HabitAssignDto} with one habit translation.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @param language    code of {@link Language}.
     * @return {@link HabitAssign} instance.
     */
    private HabitAssignDto buildHabitAssignDto(HabitAssign habitAssign, String language) {
        HabitTranslation habitTranslation = getHabitTranslation(habitAssign, language);
        HabitAssignDto habitAssignDto = modelMapper.map(habitAssign, HabitAssignDto.class);
        habitAssignDto.setHabit(modelMapper.map(habitTranslation, HabitDto.class));
        habitAssignDto.setFriendsIdsTrackingHabit(getFriendsIdsTrackingHabitList(habitAssign));
        setShoppingListItems(habitAssignDto, habitAssign, language);
        return habitAssignDto;
    }

    private List<Long> getFriendsIdsTrackingHabitList(HabitAssign habitAssign) {
        return habitAssignRepo
            .findFriendsIdsTrackingHabit(habitAssign.getHabit().getId(), habitAssign.getUser().getId());
    }

    private void setShoppingListItems(HabitAssignDto habitAssignDto, HabitAssign habitAssign, String language) {
        habitAssignDto.getHabit().setShoppingListItems(userShoppingListItemRepo
            .getAllAssignedShoppingListItemsFull(habitAssign.getId()).stream()
            .map(shoppingItem -> ShoppingListItemDto.builder()
                .id(shoppingItem.getId())
                .status(shoppingItem.getStatus().toString())
                .text(shoppingItem.getShoppingListItem().getTranslations().stream()
                    .filter(shopItem -> shopItem.getLanguage().getCode().equals(language)).findFirst()
                    .orElseThrow(
                        () -> new NotFoundException(
                            ErrorMessage.SHOPPING_LIST_ITEM_TRANSLATION_NOT_FOUND + habitAssignDto.getHabit().getId()))
                    .getContent())
                .build())
            .collect(Collectors.toList()));
    }

    private HabitAssignDto buildHabitAssignDtoContent(HabitAssign habitAssign, String language) {
        HabitAssignDto habitAssignDto = buildHabitAssignDto(habitAssign, language);
        habitAssignDto.setUserShoppingListItems(buildUserShoppingListItemAdvanceDto(habitAssign, language));
        return habitAssignDto;
    }

    private List<UserShoppingListItemAdvanceDto> buildUserShoppingListItemAdvanceDto(HabitAssign habitAssign,
        String language) {
        List<UserShoppingListItemAdvanceDto> userItemsDTO = new ArrayList<>();
        boolean isContains;
        List<ShoppingListItemTranslation> listItemTranslations = shoppingListItemTranslationRepo
            .findShoppingListByHabitIdAndByLanguageCode(language, habitAssign.getHabit().getId());
        for (ShoppingListItemTranslation translationItem : listItemTranslations) {
            isContains = false;
            for (UserShoppingListItem userItem : habitAssign.getUserShoppingListItems()) {
                if (translationItem.getShoppingListItem().getId().equals(userItem.getShoppingListItem().getId())) {
                    userItemsDTO.add(UserShoppingListItemAdvanceDto.builder()
                        .id(userItem.getId())
                        .shoppingListItemId(translationItem.getId())
                        .status(userItem.getStatus())
                        .dateCompleted(userItem.getDateCompleted())
                        .content(translationItem.getContent())
                        .build());
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                userItemsDTO.add(UserShoppingListItemAdvanceDto.builder()
                    .shoppingListItemId(translationItem.getId())
                    .status(ShoppingListItemStatus.ACTIVE)
                    .content(translationItem.getContent())
                    .build());
            }
        }
        return userItemsDTO;
    }

    /**
     * Method to get {@link HabitTranslation} for current habit assign and language.
     *
     * @param habitAssign {@link HabitAssign} habit assign.
     * @param language    {@link String} language code.
     */
    private HabitTranslation getHabitTranslation(HabitAssign habitAssign, String language) {
        return habitAssign.getHabit().getHabitTranslations().stream()
            .filter(ht -> ht.getLanguage().getCode().equals(language)).findFirst()
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_TRANSLATION_NOT_FOUND + habitAssign.getHabit().getId()));
    }

    /**
     * Method validates new {@link HabitAssign} to be created for current user.
     *
     * @param habitId {@link Habit} id.
     * @param user    {@link User} instance.
     */
    private void validateHabitForAssign(Long habitId, User user) {
        if (habitAssignRepo.countHabitAssignsByUserIdAndAcquiredFalseAndCancelledFalse(
            user.getId()) >= AppConstant.MAX_NUMBER_OF_HABIT_ASSIGNS_FOR_USER) {
            throw new UserAlreadyHasMaxNumberOfActiveHabitAssigns(
                ErrorMessage.USER_ALREADY_HAS_MAX_NUMBER_OF_HABIT_ASSIGNS
                    + AppConstant.MAX_NUMBER_OF_HABIT_ASSIGNS_FOR_USER);
        }
        if (habitAssignRepo.findByHabitIdAndUserIdAndCreateDate(
            habitId, user.getId(), ZonedDateTime.now()).isPresent()) {
            throw new UserAlreadyHasHabitAssignedException(
                ErrorMessage.USER_SUSPENDED_ASSIGNED_HABIT_FOR_CURRENT_DAY_ALREADY + habitId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitAssignDto findHabitAssignByUserIdAndHabitId(Long userId, Long habitId, String language) {
        HabitAssign habitAssign =
            habitAssignRepo.findByHabitIdAndUserId(habitId, userId)
                .orElseThrow(
                    () -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID
                        + habitId));
        return buildHabitAssignDto(habitAssign, language);
    }

    @Override
    public HabitDto findHabitByUserIdAndHabitAssignId(Long userId, Long habitAssignId, String language) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        if (!habitAssign.getUser().getId().equals(userId)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        var habitAssignDto = buildHabitAssignDto(habitAssign, language);
        HabitDto habit = habitAssignDto.getHabit();
        habit.setDefaultDuration(habitAssignDto.getDuration());
        List<ShoppingListItemDto> shoppingListItems = new ArrayList<>();
        shoppingListItemTranslationRepo
            .findShoppingListByHabitIdAndByLanguageCode(language, habit.getId())
            .forEach(x -> shoppingListItems.add(modelMapper.map(x, ShoppingListItemDto.class)));
        changeStatuses(ShoppingListItemStatus.INPROGRESS.toString(),
            habitAssign.getId(), shoppingListItems);
        changeStatuses(ShoppingListItemStatus.DONE.toString(),
            habitAssign.getId(), shoppingListItems);
        habit.setShoppingListItems(shoppingListItems);
        habit.setAmountAcquiredUsers(habitAssignRepo.findAmountOfUsersAcquired(habit.getId()));
        habit.setHabitAssignStatus(habitAssign.getStatus());
        return habit;
    }

    /**
     * Method changes statuses in shoppingListItems.
     *
     * @param status            String status to set.
     * @param habitAssignId     Long id.
     * @param shoppingListItems list with habit's items.
     */
    private void changeStatuses(String status, Long habitAssignId,
        List<ShoppingListItemDto> shoppingListItems) {
        List<Long> otherStatusItems = userShoppingListItemRepo
            .getShoppingListItemsByHabitAssignIdAndStatus(habitAssignId, status);
        if (!otherStatusItems.isEmpty()) {
            for (Long otherStatusItemId : otherStatusItems) {
                for (ShoppingListItemDto slid : shoppingListItems) {
                    if (slid.getId().equals(otherStatusItemId)) {
                        slid.setStatus(status);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByUserIdAndStatusNotCancelled(Long userId, String language) {
        return habitAssignRepo.findAllByUserId(userId)
            .stream().map(habitAssign -> buildHabitAssignDto(habitAssign, language)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<HabitAssignPreviewDto> getAllMutualHabitAssignsWithUserAndStatusNotCancelled(
        Long userId, Long currentUserId, Pageable pageable) {
        Page<HabitAssign> returnedPage = habitAssignRepo.findAllMutual(userId, currentUserId, pageable);
        return mapHabitAssignPageToPageableAdvancedDtoOfMutualHabitAssignDto(returnedPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<HabitAssignPreviewDto> getMyHabitsOfCurrentUserAndStatusNotCancelled(
        Long userId, Long currentUserId, Pageable pageable) {
        Page<HabitAssign> returnedPage = habitAssignRepo.findAllOfCurrentUser(userId, currentUserId, pageable);
        return mapHabitAssignPageToPageableAdvancedDtoOfMutualHabitAssignDto(returnedPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByHabitIdAndStatusNotCancelled(Long habitId,
        String language) {
        return habitAssignRepo.findAllByHabitId(habitId)
            .stream().map(habitAssign -> buildHabitAssignDto(habitAssign, language)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<HabitAssignPreviewDto> getAllByUserIdAndStatusNotCancelled(Long userId,
        Pageable pageable) {
        Page<HabitAssign> returnedPage = habitAssignRepo.findAllByUserId(userId, pageable);
        return mapHabitAssignPageToPageableAdvancedDtoOfMutualHabitAssignDto(returnedPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getNumberHabitAssignsByHabitIdAndStatus(Long habitId, HabitAssignStatus status) {
        List<HabitAssign> habitAssigns =
            habitAssignRepo.findAllHabitAssignsByStatusAndHabitId(status, habitId);
        return (long) habitAssigns.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByUserIdAndStatusAcquired(Long userId, String language) {
        return habitAssignRepo.findAllByUserIdAndStatusAcquired(userId)
            .stream().map(habitAssign -> buildHabitAssignDtoContent(habitAssign, language))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserShoppingAndCustomShoppingListsDto getUserShoppingAndCustomShoppingLists(
        Long userId, Long habitAssignId, String language) {
        return UserShoppingAndCustomShoppingListsDto
            .builder()
            .userShoppingListItemDto(
                shoppingListItemService.getUserShoppingListByHabitAssignId(userId, habitAssignId, language))
            .customShoppingListItemDto(customShoppingListItemService
                .findAllAvailableCustomShoppingListItemsByHabitAssignId(userId, habitAssignId))
            .build();
    }

    @Transactional
    @Override
    public List<UserShoppingAndCustomShoppingListsDto> getListOfUserAndCustomShoppingListsWithStatusInprogress(
        Long userId, String language) {
        List<HabitAssign> habitAssignList = habitAssignRepo.findAllByUserIdAndStatusIsInProgress(userId);
        if (habitAssignList.isEmpty()) {
            throw new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_INPROGRESS_STATUS + userId);
        }
        return habitAssignList.stream()
            .map(habitAssign -> UserShoppingAndCustomShoppingListsDto
                .builder()
                .userShoppingListItemDto(shoppingListItemService
                    .getUserShoppingListItemsByHabitAssignIdAndStatusInProgress(habitAssign.getId(), language))
                .customShoppingListItemDto(customShoppingListItemService
                    .findAllCustomShoppingListItemsWithStatusInProgress(userId, habitAssign.getHabit().getId()))
                .build())
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByUserIdAndCancelledStatus(Long userId,
        String language) {
        return habitAssignRepo.findAllByUserIdAndStatusIsCancelled(userId)
            .stream().map(habitAssign -> buildHabitAssignDtoContent(habitAssign, language))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignManagementDto updateStatusByHabitAssignId(Long habitAssignId,
        HabitAssignStatDto dto) {
        HabitAssign updatable = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ASSIGN_ID + habitAssignId));

        updatable.setStatus(dto.getStatus());

        return modelMapper.map(habitAssignRepo.save(updatable), HabitAssignManagementDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteAllHabitAssignsByHabit(HabitVO habit) {
        habitAssignRepo.findAllByHabitId(habit.getId())
            .forEach(habitAssign -> {
                HabitAssignVO habitAssignVO = modelMapper.map(habitAssign, HabitAssignVO.class);
                habitStatisticService.deleteAllStatsByHabitAssign(habitAssignVO);
                habitAssignRepo.delete(habitAssign);
            });
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignDto enrollHabit(Long habitAssignId, Long userId, LocalDate date, String language) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        if (!habitAssign.getUser().getId().equals(userId)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        validateForEnroll(date, habitAssign);

        HabitStatusCalendar habitCalendar = HabitStatusCalendar.builder()
            .enrollDate(date).habitAssign(habitAssign).build();

        updateHabitAssignAfterEnroll(habitAssign, habitCalendar);
        UserVO userVO = userService.findById(userId);
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.HABIT, AchievementAction.ASSIGN, habitAssign.getHabit().getId());
        ratingCalculation.ratingCalculation(RatingCalculationEnum.DAYS_OF_HABIT_IN_PROGRESS, userVO);

        return buildHabitAssignDto(habitAssign, language);
    }

    /**
     * Method validates existed enrolls of {@link HabitAssign} for creating new one.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @param date        {@link LocalDate} date.
     */
    private void validateForEnroll(LocalDate date, HabitAssign habitAssign) {
        HabitAssignVO habitAssignVO = modelMapper.map(habitAssign, HabitAssignVO.class);
        HabitStatusCalendarVO habitCalendarVO =
            habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitAssign(
                date, habitAssignVO);
        if (habitCalendarVO != null) {
            throw new UserAlreadyHasEnrolledHabitAssign(ErrorMessage.HABIT_HAS_BEEN_ALREADY_ENROLLED);
        }

        LocalDate today = LocalDate.now();
        LocalDate lastDayToEnroll = today.minusDays(AppConstant.MAX_PASSED_DAYS_OF_ABILITY_TO_ENROLL);
        if (!(date.isBefore(today.plusDays(1)) && date.isAfter(lastDayToEnroll))) {
            throw new UserHasReachedOutOfEnrollRange(
                ErrorMessage.HABIT_STATUS_CALENDAR_OUT_OF_ENROLL_RANGE);
        }
        if (habitAssign.getWorkingDays() >= habitAssign.getDuration()) {
            throw new UserHasReachedOutOfEnrollRange(ErrorMessage.HABIT_ASSIGN_ENROLL_RANGE_REACHED);
        }
    }

    /**
     * Method updates {@link HabitAssign} after enroll.
     *
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void updateHabitAssignAfterEnroll(HabitAssign habitAssign,
        HabitStatusCalendar habitCalendar) {
        habitAssign.setWorkingDays(habitAssign.getWorkingDays() + 1);
        habitAssign.setLastEnrollmentDate(ZonedDateTime.now());

        List<HabitStatusCalendar> habitStatusCalendars =
            new ArrayList<>(habitAssign.getHabitStatusCalendars());
        habitStatusCalendars.add(habitCalendar);
        habitAssign.setHabitStatusCalendars(habitStatusCalendars);

        int habitStreak = countNewHabitStreak(habitAssign.getHabitStatusCalendars());
        habitAssign.setHabitStreak(habitStreak);
        if (isHabitAcquired(habitAssign)) {
            habitAssign.setStatus(HabitAssignStatus.ACQUIRED);
        }
        habitAssignRepo.save(habitAssign);
    }

    /**
     * Method checks if {@link HabitAssign} is completed.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @return boolean.
     */
    private boolean isHabitAcquired(HabitAssign habitAssign) {
        int workingDays = habitAssign.getWorkingDays();
        int habitDuration = habitAssign.getDuration();
        if (workingDays == habitDuration) {
            if (HabitAssignStatus.ACQUIRED.equals(habitAssign.getStatus())) {
                throw new BadRequestException(
                    ErrorMessage.HABIT_ALREADY_ACQUIRED + habitAssign.getHabit().getId());
            }
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignDto unenrollHabit(Long habitAssignId, Long userId, LocalDate date) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        if (!habitAssign.getUser().getId().equals(userId)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        deleteHabitStatusCalendar(date, habitAssign);
        updateHabitAssignAfterUnenroll(habitAssign);
        UserVO userVO = userService.findById(userId);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_DAYS_OF_HABIT_IN_PROGRESS, userVO);
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.HABIT, AchievementAction.DELETE, habitAssign.getHabit().getId());
        return modelMapper.map(habitAssign, HabitAssignDto.class);
    }

    /**
     * Method delete {@link HabitStatusCalendar}.
     *
     * @param date        {@link LocalDate} date.
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void deleteHabitStatusCalendar(LocalDate date, HabitAssign habitAssign) {
        HabitStatusCalendar habitStatusCalendar = habitStatusCalendarRepo
            .findHabitStatusCalendarByEnrollDateAndHabitAssign(date, habitAssign);

        if (habitStatusCalendar == null) {
            throw new NotFoundException(ErrorMessage.HABIT_IS_NOT_ENROLLED_ON_CURRENT_DATE + date);
        }

        habitStatusCalendarRepo.delete(habitStatusCalendar);
        habitAssign.getHabitStatusCalendars().remove(habitStatusCalendar);
    }

    /**
     * Method updates {@link HabitAssign} after unenroll.
     *
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void updateHabitAssignAfterUnenroll(HabitAssign habitAssign) {
        habitAssign.setWorkingDays(habitAssign.getWorkingDays() - 1);
        habitAssign.setHabitStreak(countNewHabitStreak(habitAssign.getHabitStatusCalendars()));

        habitAssignRepo.save(habitAssign);
    }

    /**
     * Method counts new habit streak for {@link HabitAssign}.
     *
     * @param habitCalendars {@link List} of {@link HabitStatusCalendar}'s.
     * @return int of habit days streak.
     */
    private int countNewHabitStreak(List<HabitStatusCalendar> habitCalendars) {
        habitCalendars.sort(Comparator.comparing(HabitStatusCalendar::getEnrollDate).reversed());

        LocalDate today = LocalDate.now();
        int daysStreak = 0;
        int daysPast = 0;
        for (HabitStatusCalendar hc : habitCalendars) {
            if (today.minusDays(daysPast++).equals(hc.getEnrollDate())) {
                daysStreak++;
            } else {
                return daysStreak;
            }
        }
        return daysStreak;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> findInprogressHabitAssignsOnDate(Long userId, LocalDate date, String language) {
        List<HabitAssign> list = habitAssignRepo.findAllInprogressHabitAssignsOnDate(userId, date);
        return list.stream().map(
            habitAssign -> buildHabitAssignDto(habitAssign, language)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> findInprogressHabitAssignsOnDateContent(Long userId, LocalDate date, String language) {
        List<HabitAssign> list = habitAssignRepo.findAllInprogressHabitAssignsOnDate(userId, date);
        return list.stream().map(
            habitAssign -> buildHabitAssignDtoContent(habitAssign, language)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitsDateEnrollmentDto> findHabitAssignsBetweenDates(Long userId, LocalDate from, LocalDate to,
        String language) {
        if (from.isAfter(to)) {
            throw new BadRequestException(ErrorMessage.INVALID_DATE_RANGE);
        }

        List<HabitAssign> allHabitAssigns = habitAssignRepo
            .findAllInProgressHabitAssignsRelatedToUser(userId);

        List<HabitAssign> habitAssignsBetweenDates = allHabitAssigns.stream()
            .filter(ha -> isWithinDateRange(ha, from, to)).toList();

        List<LocalDate> dates = Stream.iterate(from, date -> date.plusDays(1))
            .limit(ChronoUnit.DAYS.between(from, to.plusDays(1)))
            .toList();

        List<HabitsDateEnrollmentDto> dtos = dates.stream()
            .map(date -> HabitsDateEnrollmentDto.builder().enrollDate(date)
                .habitAssigns(new ArrayList<>())
                .build())
            .collect(Collectors.toList());

        habitAssignsBetweenDates.forEach(habitAssign -> buildHabitsDateEnrollmentDto(habitAssign, language, dtos));
        return dtos;
    }

    private boolean isWithinDateRange(HabitAssign habitAssign, LocalDate from, LocalDate to) {
        LocalDate createDate = habitAssign.getCreateDate().toLocalDate();
        LocalDate endDate = createDate.plusDays(habitAssign.getDuration());

        boolean createDateWithinRange = !createDate.isBefore(from) && !createDate.isAfter(to);
        boolean endDateWithinRange = !endDate.isBefore(from) && !endDate.isAfter(to);
        boolean rangeEncompassesDates = createDate.isBefore(from) && endDate.isAfter(to);

        return createDateWithinRange || endDateWithinRange || rangeEncompassesDates;
    }

    /**
     * Method to fill in all user enrollment activity in the list of
     * {@code HabitsDateEnrollmentDto}'s by {@code HabitAssign}'s list of habit
     * status calendar.
     *
     * @param habitAssign {@code HabitAssign} habit assign.
     * @param language    {@link String} of language code value.
     * @param list        of {@link HabitsDateEnrollmentDto} instances.
     */
    private void buildHabitsDateEnrollmentDto(HabitAssign habitAssign, String language,
        List<HabitsDateEnrollmentDto> list) {
        HabitTranslation habitTranslation = getHabitTranslation(habitAssign, language);

        list.stream().filter(dto -> checkIfHabitIsActiveOnDay(dto, habitAssign))
            .forEach(dto -> markHabitOnHabitsEnrollmentDto(dto, checkIfHabitIsEnrolledOnDay(dto, habitAssign),
                habitTranslation, habitAssign));
    }

    /**
     * Method to mark if habit was enrolled on concrete date.
     *
     * @param dto              {@link HabitsDateEnrollmentDto}.
     * @param isEnrolled       {@link boolean} shows if habit was enrolled.
     * @param habitTranslation {@link HabitTranslation} contains content.
     * @param habitAssign      {@link HabitAssign} contains habit id.
     */
    private void markHabitOnHabitsEnrollmentDto(HabitsDateEnrollmentDto dto, boolean isEnrolled,
        HabitTranslation habitTranslation, HabitAssign habitAssign) {
        dto.getHabitAssigns().add(HabitEnrollDto.builder()
            .habitDescription(habitTranslation.getDescription()).habitName(habitTranslation.getName())
            .isEnrolled(isEnrolled).habitAssignId(habitAssign.getId()).build());
    }

    /**
     * Method to check if {@code HabitAssign} was enrolled on concrete date.
     *
     * @param dto         {@link HabitsDateEnrollmentDto} which contains date.
     * @param habitAssign {@link HabitAssign} contains enroll dates.
     * @return boolean.
     */
    private boolean checkIfHabitIsEnrolledOnDay(HabitsDateEnrollmentDto dto, HabitAssign habitAssign) {
        return habitAssign.getHabitStatusCalendars().stream()
            .anyMatch(habitStatusCalendar -> habitStatusCalendar.getEnrollDate().equals(dto.getEnrollDate()));
    }

    /**
     * Method to check if {@code HabitAssign} is active on concrete date.
     *
     * @param dto         {@link HabitsDateEnrollmentDto} which contains date.
     * @param habitAssign {@link HabitAssign} contains habit date borders.
     * @return boolean.
     */
    private boolean checkIfHabitIsActiveOnDay(HabitsDateEnrollmentDto dto, HabitAssign habitAssign) {
        return dto.getEnrollDate()
            .isBefore(habitAssign.getCreateDate().toLocalDate().plusDays(habitAssign.getDuration() + 1L))
            && dto.getEnrollDate()
                .isAfter(habitAssign.getCreateDate().toLocalDate().minusDays(1L));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void addDefaultHabit(UserVO user, String language) {
        if (habitAssignRepo.findAllByUserId(user.getId()).isEmpty()) {
            UserVO userVO = modelMapper.map(user, UserVO.class);
            assignDefaultHabitForUser(1L, userVO);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteHabitAssign(Long habitAssignId, Long userId) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        if (!habitAssign.getUser().getId().equals(userId)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        UserVO userVO = userService.findById(userId);

        for (int i = 0; i < habitAssign.getWorkingDays(); i++) {
            ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_DAYS_OF_HABIT_IN_PROGRESS,
                userVO);
            achievementCalculation.calculateAchievement(userVO,
                AchievementCategoryType.HABIT, AchievementAction.DELETE, habitAssign.getHabit().getId());
        }
        userShoppingListItemRepo.deleteShoppingListItemsByHabitAssignId(habitAssign.getId());
        customShoppingListItemRepo.deleteCustomShoppingListItemsByHabitId(habitAssign.getHabit().getId());
        habitAssignRepo.delete(habitAssign);
    }

    /**
     * Method update shopping item by habitAssign id and shoppingListItem id.
     *
     * @param habitAssignId      {@link Long} habit id.
     * @param shoppingListItemId {@link Long} item id.
     */
    @Transactional
    public void updateShoppingItem(Long habitAssignId, Long shoppingListItemId) {
        Optional<UserShoppingListItem> optionalUserShoppingListItem =
            userShoppingListItemRepo.getAllAssignedShoppingListItemsFull(habitAssignId).stream()
                .filter(f -> f.getId().equals(shoppingListItemId)).findAny();
        if (optionalUserShoppingListItem.isPresent()) {
            UserShoppingListItem usli = optionalUserShoppingListItem.get();
            if (usli.getStatus().equals(ShoppingListItemStatus.INPROGRESS)) {
                usli.setStatus(ShoppingListItemStatus.ACTIVE);
            } else if (usli.getStatus().equals(ShoppingListItemStatus.ACTIVE)) {
                usli.setStatus(ShoppingListItemStatus.INPROGRESS);
            }
            userShoppingListItemRepo.save(usli);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void fullUpdateUserAndCustomShoppingLists(
        Long userId,
        Long habitAssignId,
        UserShoppingAndCustomShoppingListsDto listsDto,
        String language) {
        fullUpdateUserShoppingList(userId, habitAssignId, listsDto.getUserShoppingListItemDto(), language);
        fullUpdateCustomShoppingList(userId, habitAssignId, listsDto.getCustomShoppingListItemDto());
    }

    /**
     * Method that update UserShoppingList.
     *
     * <ul>
     * <li>If items are present in the db, method update them;</li>
     * <li>If items don't present in the db and id is null, method try to add it to
     * user;</li>
     * <li>If some items from db don't present in the lists, method delete
     * them(Except items with DISABLED status).</li>
     * </ul>
     *
     * @param userId        {@code User} id.
     * @param habitAssignId {@code HabitAssign} id.
     * @param list          {@link UserShoppingListItemResponseDto} User Shopping
     *                      lists.
     * @param language      {@link String} of language code value.
     */
    private void fullUpdateUserShoppingList(
        Long userId,
        Long habitAssignId,
        List<UserShoppingListItemResponseDto> list,
        String language) {
        updateAndDeleteUserShoppingListWithStatuses(userId, habitAssignId, list);
        saveUserShoppingListWithStatuses(userId, habitAssignId, list, language);
    }

    /**
     * Method that save {@link UserShoppingListItemResponseDto} for item with id =
     * null.
     *
     * @param userId           {@code User} id.
     * @param habitAssignId    {@code HabitAssign} id.
     * @param userShoppingList {@link UserShoppingListItemResponseDto} User shopping
     *                         lists.
     * @param language         {@link String} of language code value.
     */
    private void saveUserShoppingListWithStatuses(
        Long userId,
        Long habitAssignId,
        List<UserShoppingListItemResponseDto> userShoppingList,
        String language) {
        List<UserShoppingListItemResponseDto> listToSave = userShoppingList.stream()
            .filter(shoppingItem -> shoppingItem.getId() == null)
            .collect(Collectors.toList());
        checkDuplicationForUserShoppingListByName(listToSave);

        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        if (!habitAssign.getUser().getId().equals(userId)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        Long habitId = habitAssign.getHabit().getId();

        List<ShoppingListItem> shoppingListItems = findRelatedShoppingListItem(habitId, language, listToSave);

        Map<Long, ShoppingListItemStatus> shoppingItemIdToStatusMap =
            getShoppingItemIdToStatusMap(shoppingListItems, listToSave, language);

        List<ShoppingListItemRequestDto> listToSaveParam = shoppingListItems.stream()
            .map(shoppingItem -> ShoppingListItemWithStatusRequestDto.builder()
                .id(shoppingItem.getId())
                .status(shoppingItemIdToStatusMap.get(shoppingItem.getId()))
                .build())
            .collect(Collectors.toList());

        shoppingListItemService.saveUserShoppingListItems(userId, habitId, listToSaveParam, language);
    }

    private void checkDuplicationForUserShoppingListByName(List<UserShoppingListItemResponseDto> listToSave) {
        long countOfUnique = listToSave.stream()
            .map(UserShoppingListItemResponseDto::getText)
            .distinct()
            .count();
        if (listToSave.size() != countOfUnique) {
            throw new BadRequestException(ErrorMessage.DUPLICATED_USER_SHOPPING_LIST_ITEM);
        }
    }

    private List<ShoppingListItem> findRelatedShoppingListItem(
        Long habitId,
        String language,
        List<UserShoppingListItemResponseDto> listToSave) {
        if (listToSave.isEmpty()) {
            return List.of();
        }

        List<String> listToSaveNames = listToSave.stream()
            .map(UserShoppingListItemResponseDto::getText)
            .collect(Collectors.toList());

        List<ShoppingListItem> relatedShoppingListItems =
            shoppingListItemRepo.findByNames(habitId, listToSaveNames, language);

        if (listToSaveNames.size() != relatedShoppingListItems.size()) {
            List<String> relatedShoppingListItemNames = relatedShoppingListItems.stream()
                .map(x -> getShoppingItemNameByLanguageCode(x, language))
                .collect(Collectors.toList());

            listToSaveNames.removeAll(relatedShoppingListItemNames);

            String notFoundItems = String.join(", ", listToSaveNames);

            throw new NotFoundException(ErrorMessage.SHOPPING_LIST_ITEM_NOT_FOUND_BY_NAMES + notFoundItems);
        }
        return relatedShoppingListItems;
    }

    private Map<Long, ShoppingListItemStatus> getShoppingItemIdToStatusMap(
        List<ShoppingListItem> shoppingListItems,
        List<UserShoppingListItemResponseDto> listToSave,
        String language) {
        Map<String, ShoppingListItemStatus> shoppingItemNameToStatusMap =
            listToSave.stream()
                .collect(Collectors.toMap(
                    UserShoppingListItemResponseDto::getText,
                    UserShoppingListItemResponseDto::getStatus));

        return shoppingListItems.stream()
            .collect(Collectors.toMap(
                ShoppingListItem::getId,
                shoppingListItem -> shoppingItemNameToStatusMap
                    .get(getShoppingItemNameByLanguageCode(shoppingListItem, language))));
    }

    private String getShoppingItemNameByLanguageCode(ShoppingListItem shoppingItem, String language) {
        return shoppingItem.getTranslations()
            .stream()
            .filter(x -> x.getLanguage().getCode().equals(language))
            .findFirst()
            .orElseThrow()
            .getContent();
    }

    /**
     * Method that update or delete {@link UserShoppingListItem}. Not founded items,
     * except DISABLED, will be deleted.
     *
     * @param userId           {@code User} id.
     * @param habitAssignId    {@code HabitAssign} id.
     * @param userShoppingList {@link UserShoppingListItemResponseDto} User shopping
     *                         lists.
     */
    private void updateAndDeleteUserShoppingListWithStatuses(
        Long userId,
        Long habitAssignId,
        List<UserShoppingListItemResponseDto> userShoppingList) {
        List<UserShoppingListItemResponseDto> listToUpdate = userShoppingList.stream()
            .filter(item -> item.getId() != null)
            .collect(Collectors.toList());

        checkDuplicationForUserShoppingListById(listToUpdate);

        HabitAssign habitAssign = habitAssignRepo
            .findByHabitAssignIdUserIdNotCancelledAndNotExpiredStatus(habitAssignId, userId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ASSIGN_ID + habitAssignId));

        List<UserShoppingListItem> currentList = habitAssign.getUserShoppingListItems();

        checkIfUserShoppingItemsExist(listToUpdate, currentList);

        Map<Long, ShoppingListItemStatus> mapIdToStatus =
            listToUpdate.stream()
                .collect(Collectors.toMap(
                    UserShoppingListItemResponseDto::getId,
                    UserShoppingListItemResponseDto::getStatus));

        List<UserShoppingListItem> listToSave = new ArrayList<>();
        List<UserShoppingListItem> listToDelete = new ArrayList<>();
        for (var currentItem : currentList) {
            ShoppingListItemStatus newStatus = mapIdToStatus.get(currentItem.getId());
            if (newStatus != null) {
                currentItem.setStatus(newStatus);
                listToSave.add(currentItem);
            } else {
                if (!currentItem.getStatus().equals(ShoppingListItemStatus.DISABLED)) {
                    listToDelete.add(currentItem);
                }
            }
        }
        userShoppingListItemRepo.saveAll(listToSave);
        userShoppingListItemRepo.deleteAll(listToDelete);
    }

    private void checkDuplicationForUserShoppingListById(List<UserShoppingListItemResponseDto> listToUpdate) {
        long countOfUnique = listToUpdate.stream()
            .map(UserShoppingListItemResponseDto::getId)
            .distinct()
            .count();
        if (listToUpdate.size() != countOfUnique) {
            throw new BadRequestException(ErrorMessage.DUPLICATED_USER_SHOPPING_LIST_ITEM);
        }
    }

    private void checkIfUserShoppingItemsExist(
        List<UserShoppingListItemResponseDto> listToUpdate,
        List<UserShoppingListItem> currentList) {
        List<Long> updateIds =
            listToUpdate.stream().map(UserShoppingListItemResponseDto::getId).collect(Collectors.toList());
        List<Long> currentIds = currentList.stream().map(UserShoppingListItem::getId).collect(Collectors.toList());

        updateIds.removeAll(currentIds);

        if (!updateIds.isEmpty()) {
            String notFoundedIds = updateIds.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
            throw new NotFoundException(ErrorMessage.USER_SHOPPING_LIST_ITEM_NOT_FOUND + notFoundedIds);
        }
    }

    /**
     * Method that update CustomShopping List.
     *
     * <ul>
     * <li>If items are present in the db, method update them;</li>
     * <li>If items don't present in the db and id is null, method try to add it to
     * user;</li>
     * <li>If some items from db don't present in the lists, method delete
     * them(Except items with DISABLED status).</li>
     * </ul>
     *
     * @param userId        {@code User} id.
     * @param habitAssignId {@code HabitAssign} id.
     * @param list          {@link CustomShoppingListItemResponseDto} Custom
     *                      Shopping lists.
     */
    private void fullUpdateCustomShoppingList(
        Long userId,
        Long habitAssignId,
        List<CustomShoppingListItemResponseDto> list) {
        updateAndDeleteCustomShoppingListWithStatuses(userId, habitAssignId, list);
        saveCustomShoppingListWithStatuses(userId, habitAssignId, list);
    }

    /**
     * Method that save {@link CustomShoppingListItemResponseDto} for item with id =
     * null.
     *
     * @param userId             {@code User} id.
     * @param habitAssignId      {@code HabitAssign} id.
     * @param customShoppingList {@link CustomShoppingListItemResponseDto} Custom
     *                           shopping lists.
     */
    private void saveCustomShoppingListWithStatuses(
        Long userId,
        Long habitAssignId,
        List<CustomShoppingListItemResponseDto> customShoppingList) {
        List<CustomShoppingListItemResponseDto> listToSave = customShoppingList.stream()
            .filter(shoppingItem -> shoppingItem.getId() == null)
            .collect(Collectors.toList());

        checkDuplicationForCustomShoppingListByName(listToSave);

        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        if (!habitAssign.getUser().getId().equals(userId)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        List<CustomShoppingListItemSaveRequestDto> listToSaveParam = listToSave.stream()
            .map(item -> CustomShoppingListItemWithStatusSaveRequestDto.builder()
                .text(item.getText())
                .status(item.getStatus())
                .build())
            .collect(Collectors.toList());

        customShoppingListItemService.save(new BulkSaveCustomShoppingListItemDto(listToSaveParam), userId,
            habitAssignId);
    }

    private void checkDuplicationForCustomShoppingListByName(List<CustomShoppingListItemResponseDto> listToSave) {
        long countOfUnique = listToSave.stream()
            .map(CustomShoppingListItemResponseDto::getText)
            .distinct()
            .count();
        if (listToSave.size() != countOfUnique) {
            throw new BadRequestException(ErrorMessage.DUPLICATED_CUSTOM_SHOPPING_LIST_ITEM);
        }
    }

    /**
     * Method that update or delete {@link CustomShoppingListItem}. Not founded
     * items, except DISABLED, will be deleted.
     *
     * @param userId             {@code User} id.
     * @param habitAssignId      {@code HabitAssign} id.
     * @param customShoppingList {@link CustomShoppingListItemResponseDto} Custom
     *                           shopping lists.
     */
    private void updateAndDeleteCustomShoppingListWithStatuses(
        Long userId,
        Long habitAssignId,
        List<CustomShoppingListItemResponseDto> customShoppingList) {
        List<CustomShoppingListItemResponseDto> listToUpdate = customShoppingList.stream()
            .filter(shoppingItem -> shoppingItem.getId() != null)
            .collect(Collectors.toList());

        checkDuplicationForCustomShoppingListById(listToUpdate);

        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        if (!habitAssign.getUser().getId().equals(userId)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        Long habitId = habitAssign.getHabit().getId();

        List<CustomShoppingListItem> currentList =
            customShoppingListItemRepo.findAllByUserIdAndHabitId(userId, habitId);

        checkIfCustomShoppingItemsExist(listToUpdate, currentList);

        Map<Long, ShoppingListItemStatus> mapIdToStatus =
            listToUpdate.stream()
                .collect(Collectors.toMap(
                    CustomShoppingListItemResponseDto::getId,
                    CustomShoppingListItemResponseDto::getStatus));

        List<CustomShoppingListItem> listToSave = new ArrayList<>();
        List<CustomShoppingListItem> listToDelete = new ArrayList<>();
        for (var currentItem : currentList) {
            ShoppingListItemStatus newStatus = mapIdToStatus.get(currentItem.getId());
            if (newStatus != null) {
                currentItem.setStatus(newStatus);
                listToSave.add(currentItem);
            } else {
                if (!currentItem.getStatus().equals(ShoppingListItemStatus.DISABLED)) {
                    listToDelete.add(currentItem);
                }
            }
        }
        customShoppingListItemRepo.saveAll(listToSave);
        customShoppingListItemRepo.deleteAll(listToDelete);
    }

    private void checkDuplicationForCustomShoppingListById(List<CustomShoppingListItemResponseDto> listToUpdate) {
        long countOfUnique = listToUpdate.stream()
            .map(CustomShoppingListItemResponseDto::getId)
            .distinct()
            .count();
        if (listToUpdate.size() != countOfUnique) {
            throw new BadRequestException(ErrorMessage.DUPLICATED_CUSTOM_SHOPPING_LIST_ITEM);
        }
    }

    private void checkIfCustomShoppingItemsExist(
        List<CustomShoppingListItemResponseDto> listToUpdate,
        List<CustomShoppingListItem> currentList) {
        List<Long> updateIds =
            listToUpdate.stream().map(CustomShoppingListItemResponseDto::getId).collect(Collectors.toList());
        List<Long> currentIds = currentList.stream().map(CustomShoppingListItem::getId).collect(Collectors.toList());

        updateIds.removeAll(currentIds);

        if (!updateIds.isEmpty()) {
            String notFoundedIds = updateIds.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
            throw new NotFoundException(ErrorMessage.CUSTOM_SHOPPING_LIST_ITEM_WITH_THIS_ID_NOT_FOUND + notFoundedIds);
        }
    }

    @Transactional
    @Override
    public void updateProgressNotificationHasDisplayed(Long habitAssignId, Long userId) {
        if (habitAssignRepo.findById(habitAssignId).isEmpty()) {
            throw new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId);
        }
        habitAssignRepo.updateProgressNotificationHasDisplayed(habitAssignId, userId);
    }

    @Transactional
    @Override
    public HabitAssignUserDurationDto updateStatusAndDurationOfHabitAssign(Long habitAssignId, Long userId,
        Integer duration) {
        Optional<HabitAssign> habitAssignOptional = habitAssignRepo.findById(habitAssignId);
        HabitAssign habitAssign;

        if (habitAssignOptional.isPresent()) {
            habitAssign = habitAssignRepo.findByHabitAssignIdUserIdAndStatusIsRequested(habitAssignId, userId)
                .orElseThrow(() -> new InvalidStatusException(
                    ErrorMessage.HABIT_ASSIGN_STATUS_IS_NOT_REQUESTED_OR_USER_HAS_NOT_ANY_ASSIGNED_HABITS));
        } else {
            throw new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId);
        }
        habitAssign.setDuration(duration);
        habitAssign.setStatus(HabitAssignStatus.INPROGRESS);
        return modelMapper.map(habitAssignRepo.save(habitAssign), HabitAssignUserDurationDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void inviteFriendForYourHabitWithEmailNotification(UserVO userVO, Long friendId, Long habitId,
        Locale locale) {
        if (!userRepo.isFriend(userVO.getId(), friendId)) {
            throw new UserHasNoFriendWithIdException(
                ErrorMessage.USER_HAS_NO_FRIEND_WITH_ID + friendId);
        }
        User friend = userRepo.findById(friendId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId));
        UserVO friendVO = modelMapper.map(friend, UserVO.class);
        checkStatusInProgressExists(habitId, friendVO);
        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId));

        validateHabitForAssign(habitId, friend);
        HabitAssign habitAssign =
            habitAssignRepo.findByHabitIdAndUserIdAndStatusIsCancelled(habitId, friend.getId());

        if (habitAssign != null) {
            habitAssign.setStatus(HabitAssignStatus.REQUESTED);
            habitAssign.setCreateDate(ZonedDateTime.now());
            habitAssignRepo.save(habitAssign);
            notificationService.sendHabitAssignEmailNotification(HabitAssignNotificationMessage.builder()
                .senderName(userVO.getName())
                .receiverName(friendVO.getName())
                .receiverEmail(friendVO.getEmail())
                .habitAssignId(habitAssign.getId())
                .habitName(getHabitTranslation(habitAssign, locale.getLanguage()).getName())
                .language(locale.getLanguage())
                .build());
        } else {
            List<Long> allShoppingListItemId =
                shoppingListItemRepo.getAllShoppingListItemIdByHabitIdISContained(habitId);
            habitAssign = buildHabitAssign(habit, friend, HabitAssignStatus.REQUESTED);
            notificationService.sendHabitAssignEmailNotification(HabitAssignNotificationMessage.builder()
                .senderName(userVO.getName())
                .receiverName(friendVO.getName())
                .receiverEmail(friendVO.getEmail())
                .habitAssignId(habitAssign.getId())
                .habitName(getHabitTranslation(habitAssign, locale.getLanguage()).getName())
                .language(locale.getLanguage())
                .build());
            if (!allShoppingListItemId.isEmpty()) {
                List<ShoppingListItem> shoppingList =
                    shoppingListItemRepo.getShoppingListByListOfId(allShoppingListItemId);
                saveUserShoppingListItems(shoppingList, habitAssign);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void confirmHabitInvitation(Long habitAssignId) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));
        if (!habitAssign.getStatus().equals(HabitAssignStatus.REQUESTED)) {
            throw new BadRequestException(
                ErrorMessage.HABIT_ASSIGN_STATUS_IS_NOT_REQUESTED_OR_USER_HAS_NOT_ANY_ASSIGNED_HABITS);
        }
        habitAssign.setStatus(HabitAssignStatus.INPROGRESS);
        habitAssign.setCreateDate(ZonedDateTime.now());
        habitAssignRepo.save(habitAssign);
    }

    @NotNull
    private PageableAdvancedDto<HabitAssignPreviewDto> mapHabitAssignPageToPageableAdvancedDtoOfMutualHabitAssignDto(
        Page<HabitAssign> returnedPage) {
        List<HabitAssignPreviewDto> habitAssignPreviewDtos = returnedPage.getContent().stream()
            .map(habitAssign -> modelMapper.map(habitAssign, HabitAssignPreviewDto.class)).toList();
        return new PageableAdvancedDto<>(habitAssignPreviewDtos, returnedPage.getTotalElements(),
            returnedPage.getPageable().getPageNumber(), returnedPage.getTotalPages(), returnedPage.getNumber(),
            returnedPage.hasPrevious(), returnedPage.hasNext(), returnedPage.isFirst(), returnedPage.isLast());
    }
}
