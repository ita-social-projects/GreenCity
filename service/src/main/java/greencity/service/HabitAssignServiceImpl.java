package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
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
import greencity.dto.habit.UpdateUserShoppingListDto;
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
import greencity.entity.Language;
import greencity.entity.ShoppingListItem;
import greencity.entity.User;
import greencity.entity.UserShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.AchievementType;
import greencity.enums.HabitAssignStatus;
import greencity.enums.ShoppingListItemStatus;
import greencity.enums.RatingCalculationEnum;
import greencity.enums.AchievementCategoryType;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.InvalidStatusException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.ShoppingListItemNotFoundException;
import greencity.exception.exceptions.UserAlreadyHasEnrolledHabitAssign;
import greencity.exception.exceptions.UserAlreadyHasHabitAssignedException;
import greencity.exception.exceptions.UserAlreadyHasMaxNumberOfActiveHabitAssigns;
import greencity.exception.exceptions.UserHasNoFriendWithIdException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.exception.exceptions.UserHasReachedOutOfEnrollRange;
import greencity.rating.RatingCalculation;
import greencity.repository.CustomShoppingListItemRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitStatusCalendarRepo;
import greencity.repository.ShoppingListItemRepo;
import greencity.repository.ShoppingListItemTranslationRepo;
import greencity.repository.UserRepo;
import greencity.repository.UserShoppingListItemRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;

import static greencity.constant.AppConstant.AUTHORIZATION;

/**
 * Implementation of {@link HabitAssignService}.
 */
@Service
@AllArgsConstructor
public class HabitAssignServiceImpl implements HabitAssignService {
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
    private final HttpServletRequest httpServletRequest;
    private final UserService userService;
    private final RatingCalculation ratingCalculation;

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
            List<ShoppingListItem> shoppingList =
                shoppingListItemRepo.getShoppingListByListOfId(
                    shoppingListItemRepo.getAllShoppingListItemIdByHabitIdISContained(habitId));
            habitAssign = buildHabitAssign(habit, user, HabitAssignStatus.INPROGRESS);
            saveUserShoppingListItems(shoppingList, habitAssign);
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

        habitAssignRepo.save(habitAssign);

        List<HabitAssignManagementDto> habitAssignManagementDtoList = new ArrayList<>();
        habitAssignManagementDtoList.add(modelMapper.map(habitAssign, HabitAssignManagementDto.class));

        if (!CollectionUtils.isEmpty(habitAssignCustomPropertiesDto.getFriendsIdsList())) {
            assignFriendsForCustomHabit(habit, userVO.getId(), habitAssignCustomPropertiesDto,
                habitAssignManagementDtoList);
        }

        return habitAssignManagementDtoList;
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
        setShoppingListItems(habitAssignDto, habitAssign, language);
        return habitAssignDto;
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

    @Override
    @Transactional
    public void updateUserShoppingListItem(UpdateUserShoppingListDto updateUserShoppingListDto) {
        userShoppingListItemRepo.saveAll(buildUserShoppingListItem(updateUserShoppingListDto));
    }

    private List<UserShoppingListItem> buildUserShoppingListItem(UpdateUserShoppingListDto updateUserShoppingListDto) {
        HabitAssign habitAssign = habitAssignRepo.findById(updateUserShoppingListDto.getHabitAssignId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID));
        List<UserShoppingListItem> userShoppingListItemList = new ArrayList<>();
        for (UserShoppingListItemAdvanceDto item : updateUserShoppingListDto.getUserShoppingListAdvanceDto()) {
            ShoppingListItem shoppingListItem = shoppingListItemRepo.findById(item.getShoppingListItemId())
                .orElseThrow(
                    () -> new ShoppingListItemNotFoundException(ErrorMessage.SHOPPING_LIST_ITEM_NOT_FOUND_BY_ID));
            userShoppingListItemList.add(UserShoppingListItem.builder()
                .habitAssign(habitAssign)
                .shoppingListItem(shoppingListItem)
                .status(item.getStatus())
                .id(updateUserShoppingListDto.getUserShoppingListItemId())
                .build());
        }
        return userShoppingListItemList;
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
    public List<HabitAssignDto> getAllHabitAssignsByHabitIdAndStatusNotCancelled(Long habitId,
        String language) {
        return habitAssignRepo.findAllByHabitId(habitId)
            .stream().map(habitAssign -> buildHabitAssignDto(habitAssign, language)).collect(Collectors.toList());
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

        updateHabitAssignAfterEnroll(habitAssign, habitCalendar, userId);
        UserVO userVO = userService.findById(userId);
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
            () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.DAYS_OF_HABIT_IN_PROGRESS, userVO));
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
    }

    /**
     * Method updates {@link HabitAssign} after enroll.
     *
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void updateHabitAssignAfterEnroll(HabitAssign habitAssign,
        HabitStatusCalendar habitCalendar, Long userId) {
        habitAssign.setWorkingDays(habitAssign.getWorkingDays() + 1);
        habitAssign.setLastEnrollmentDate(ZonedDateTime.now());

        List<HabitStatusCalendar> habitStatusCalendars =
            new ArrayList<>(habitAssign.getHabitStatusCalendars());
        habitStatusCalendars.add(habitCalendar);
        habitAssign.setHabitStatusCalendars(habitStatusCalendars);

        int habitStreak = countNewHabitStreak(habitAssign.getHabitStatusCalendars());
        habitAssign.setHabitStreak(habitStreak);
        CompletableFuture.runAsync(() -> achievementCalculation
            .calculateAchievement(userId, AchievementType.COMPARISON,
                AchievementCategoryType.HABIT_STREAK, habitStreak));

        if (isHabitAcquired(habitAssign)) {
            habitAssign.setStatus(HabitAssignStatus.ACQUIRED);
            CompletableFuture.runAsync(() -> achievementCalculation
                .calculateAchievement(userId, AchievementType.INCREMENT, AchievementCategoryType.HABIT_STREAK, 0));
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
        CompletableFuture.runAsync(
            () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_DAYS_OF_HABIT_IN_PROGRESS, userVO));
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
        List<HabitAssign> habitAssignsBetweenDates = habitAssignRepo
            .findAllHabitAssignsBetweenDates(userId, from, to);
        List<LocalDate> dates = Stream.iterate(from, date -> date.plusDays(1))
            .limit(ChronoUnit.DAYS.between(from, to.plusDays(1)))
            .collect(Collectors.toList());

        List<HabitsDateEnrollmentDto> dtos = dates.stream()
            .map(date -> HabitsDateEnrollmentDto.builder().enrollDate(date)
                .habitAssigns(new ArrayList<>())
                .build())
            .collect(Collectors.toList());

        habitAssignsBetweenDates.forEach(habitAssign -> buildHabitsDateEnrollmentDto(habitAssign, language, dtos));
        return dtos;
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
     * Method to set {@link HabitAssign} status from inprogress to cancelled.
     *
     * @param habitId - id of {@link HabitVO}.
     * @param userId  - id of {@link UserVO}.
     * @return {@link HabitAssignDto}.
     */
    @Transactional
    @Override
    public HabitAssignDto cancelHabitAssign(Long habitId, Long userId) {
        HabitAssign habitAssignToCancel = habitAssignRepo.findByHabitIdAndUserIdAndStatusIsInprogress(habitId, userId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID_AND_INPROGRESS_STATUS + habitId));
        habitAssignToCancel.setStatus(HabitAssignStatus.CANCELLED);
        UserVO userVO = userService.findById(userId);

        for (int i = 0; i < habitAssignToCancel.getWorkingDays(); i++) {
            CompletableFuture.runAsync(
                () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_DAYS_OF_HABIT_IN_PROGRESS,
                    userVO));
        }
        habitAssignRepo.save(habitAssignToCancel);
        return buildHabitAssignDto(habitAssignToCancel, "en");
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
            CompletableFuture.runAsync(
                () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_DAYS_OF_HABIT_IN_PROGRESS,
                    userVO));
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
}
