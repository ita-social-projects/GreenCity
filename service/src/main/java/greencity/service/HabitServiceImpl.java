package greencity.service;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.entity.CustomShoppingListItem;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.mapping.CustomHabitMapper;
import greencity.mapping.CustomShoppingListMapper;
import greencity.mapping.CustomShoppingListResponseDtoMapper;
import greencity.mapping.HabitTranslationDtoMapper;
import greencity.mapping.HabitTranslationMapper;
import greencity.repository.HabitRepo;
import greencity.repository.HabitTranslationRepo;
import greencity.repository.ShoppingListItemTranslationRepo;
import greencity.repository.HabitAssignRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import greencity.repository.CustomShoppingListItemRepo;
import greencity.repository.LanguageRepo;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

/**
 * Implementation of {@link HabitService}.
 */
@Service
@AllArgsConstructor
public class HabitServiceImpl implements HabitService {
    private final HabitRepo habitRepo;
    private final HabitTranslationRepo habitTranslationRepo;
    private final ModelMapper modelMapper;
    private final CustomShoppingListResponseDtoMapper customShoppingListResponseDtoMapper;
    private final HabitTranslationDtoMapper habitTranslationDtoMapper;
    private final CustomShoppingListMapper customShoppingListMapper;
    private final HabitTranslationMapper habitTranslationMapper;
    private final CustomHabitMapper customHabitMapper;
    private final ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;
    private final CustomShoppingListItemRepo customShoppingListItemRepo;
    private final LanguageRepo languageRepo;
    private final UserRepo userRepo;
    private final TagsRepo tagsRepo;
    private final FileService fileService;
    private final HabitAssignRepo habitAssignRepo;
    private static final String DEFAULT_TITLE_IMAGE_PATH = AppConstant.DEFAULT_HABIT_IMAGE;

    /**
     * Method returns Habit by its id.
     *
     * @param id           - id of the {@link Long} habit
     * @param languageCode - language code {@link String}
     * @return {@link HabitDto}
     */
    @Override
    public HabitDto getByIdAndLanguageCode(Long id, String languageCode) {
        Habit habit = habitRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id));
        HabitTranslation habitTranslation = habitTranslationRepo.findByHabitAndLanguageCode(habit, languageCode)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_TRANSLATION_NOT_FOUND + id));
        var habitDto = modelMapper.map(habitTranslation, HabitDto.class);
        List<ShoppingListItemDto> shoppingListItems = new ArrayList<>();
        shoppingListItemTranslationRepo
            .findShoppingListByHabitIdAndByLanguageCode(languageCode, id)
            .forEach(x -> shoppingListItems.add(modelMapper.map(x, ShoppingListItemDto.class)));
        habitDto.setShoppingListItems(shoppingListItems);
        habitDto.setAmountAcquiredUsers(habitAssignRepo.findAmountOfUsersAcquired(habitDto.getId()));
        boolean isCustomHabit = habit.getIsCustomHabit();
        habitDto.setIsCustomHabit(isCustomHabit);
        if (isCustomHabit) {
            habitDto.setCustomShoppingListItems(
                customShoppingListResponseDtoMapper.mapAllToList(habit.getCustomShoppingListItems()));
        }
        return habitDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<HabitDto> getAllHabitsByLanguageCode(Pageable pageable, String language) {
        Page<HabitTranslation> habitTranslationPage =
            habitTranslationRepo.findAllByLanguageCode(pageable, language);
        return buildPageableDto(habitTranslationPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<HabitDto> getAllByTagsAndLanguageCode(Pageable pageable, List<String> tags,
        String languageCode) {
        List<String> lowerCaseTags = tags.stream().map(String::toLowerCase).collect(Collectors.toList());
        Page<HabitTranslation> habitTranslationsPage =
            habitTranslationRepo.findAllByTagsAndLanguageCode(pageable, lowerCaseTags, languageCode);
        return buildPageableDto(habitTranslationsPage);
    }

    /**
     * Method that build {@link PageableDto} of {@link HabitDto} from {@link Page}
     * of {@link HabitTranslation}.
     *
     * @param habitTranslationsPage {@link Page} of {@link HabitTranslation}
     * @return {@link PageableDto} of {@link HabitDto}
     * @author Markiyan Derevetskyi
     */
    private PageableDto<HabitDto> buildPageableDto(Page<HabitTranslation> habitTranslationsPage) {
        List<HabitDto> habits =
            habitTranslationsPage.stream()
                .map(habitTranslation -> modelMapper.map(habitTranslation, HabitDto.class))
                .collect(Collectors.toList());
        habits.forEach(
            habitDto -> habitDto.setAmountAcquiredUsers(habitAssignRepo.findAmountOfUsersAcquired(habitDto.getId())));
        return new PageableDto<>(habits, habitTranslationsPage.getTotalElements(),
            habitTranslationsPage.getPageable().getPageNumber(),
            habitTranslationsPage.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<HabitDto> getAllByDifferentParameters(Pageable pageable, Optional<List<String>> tags,
        Optional<Boolean> isCustomHabit, Optional<List<Integer>> complexities, String languageCode) {
        List<String> lowerCaseTags = new ArrayList<>();
        List<Integer> complexitiesList = new ArrayList<>();
        if (tags.isPresent()) {
            lowerCaseTags = tags.get().stream().map(String::toLowerCase).collect(Collectors.toList());
        }
        if (complexities.isPresent()) {
            complexitiesList = complexities.get().stream().collect(Collectors.toList());
        }
        Page<HabitTranslation> habitTranslationsPage;

        if (isCustomHabit.isPresent() && !lowerCaseTags.isEmpty() && !complexitiesList.isEmpty()) {
            habitTranslationsPage = habitTranslationRepo.findAllByDifferentParameters(pageable, lowerCaseTags,
                isCustomHabit, complexities, languageCode);
        } else if (!complexitiesList.isEmpty() && isCustomHabit.isPresent()) {
            habitTranslationsPage = habitTranslationRepo.findAllByIsCustomHabitAndComplexityAndLanguageCode(pageable,
                isCustomHabit, complexities, languageCode);
        } else if (!complexitiesList.isEmpty() && !lowerCaseTags.isEmpty()) {
            habitTranslationsPage =
                habitTranslationRepo.findAllByTagsAndComplexityAndLanguageCode(pageable, lowerCaseTags,
                    complexities, languageCode);
        } else if (isCustomHabit.isPresent() && !lowerCaseTags.isEmpty()) {
            habitTranslationsPage = habitTranslationRepo.findAllByTagsAndIsCustomHabitAndLanguageCode(pageable,
                lowerCaseTags, isCustomHabit, languageCode);
        } else if (!lowerCaseTags.isEmpty()) {
            habitTranslationsPage =
                habitTranslationRepo.findAllByTagsAndLanguageCode(pageable, lowerCaseTags, languageCode);
        } else if (!complexitiesList.isEmpty()) {
            habitTranslationsPage =
                habitTranslationRepo.findAllByComplexityAndLanguageCode(pageable, complexities, languageCode);
        } else {
            habitTranslationsPage = habitTranslationRepo.findAllByIsCustomHabitAndLanguageCode(pageable,
                isCustomHabit, languageCode);
        }
        return buildPageableDtoForDifferentParameters(habitTranslationsPage);
    }

    /**
     * Method that build {@link PageableDto} of {@link HabitDto} from {@link Page}
     * of {@link HabitTranslation}.
     *
     * @param habitTranslationsPage {@link Page} of {@link HabitTranslation}
     * @return {@link PageableDto} of {@link HabitDto}
     * @author Lilia Mokhnatska
     */
    private PageableDto<HabitDto> buildPageableDtoForDifferentParameters(Page<HabitTranslation> habitTranslationsPage) {
        List<HabitDto> habits = habitTranslationsPage.stream()
            .map(habitTranslation -> modelMapper.map(habitTranslation, HabitDto.class))
            .collect(Collectors.toList());
        habits.forEach(
            habitDto -> habitDto.setAmountAcquiredUsers(habitAssignRepo.findAmountOfUsersAcquired(habitDto.getId())));

        for (HabitDto habitDto : habits) {
            Habit habit = habitRepo.findById(habitDto.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitDto.getId()));
            boolean isCustomHabit = habit.getIsCustomHabit();
            habitDto.setIsCustomHabit(isCustomHabit);
            habitDto.setCustomShoppingListItems(
                customShoppingListResponseDtoMapper.mapAllToList(habit.getCustomShoppingListItems()));
        }
        return new PageableDto<>(habits,
            habitTranslationsPage.getTotalElements(),
            habitTranslationsPage.getPageable().getPageNumber(),
            habitTranslationsPage.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ShoppingListItemDto> getShoppingListForHabit(Long habitId, String lang) {
        return shoppingListItemTranslationRepo.findShoppingListByHabitIdAndByLanguageCode(lang, habitId)
            .stream()
            .map(g -> modelMapper.map(g, ShoppingListItemDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public void addShoppingListItemToHabit(Long habitId, Long itemId) {
        habitRepo.addShopingListItemToHabit(habitId, itemId);
    }

    @Override
    public void deleteShoppingListItem(Long habitId, Long itemId) {
        habitRepo.upadateShopingListItemInHabit(habitId, itemId);
    }

    @Override
    public List<Long> deleteAllShoppingListItemsByListOfId(Long habitId, List<Long> listId) {
        listId.forEach(id -> deleteShoppingListItem(habitId, id));
        return listId;
    }

    @Override
    public List<Long> addAllShoppingListItemsByListOfId(Long habitId, List<Long> listId) {
        listId.forEach(id -> addShoppingListItemToHabit(habitId, id));
        return listId;
    }

    @Transactional
    @Override
    public AddCustomHabitDtoResponse addCustomHabit(
        AddCustomHabitDtoRequest addCustomHabitDtoRequest, MultipartFile image, String userEmail) {
        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + userEmail));
        if (StringUtils.isNotBlank(addCustomHabitDtoRequest.getImage())) {
            image = fileService.convertToMultipartImage(addCustomHabitDtoRequest.getImage());
        }
        if (image != null) {
            addCustomHabitDtoRequest.setImage(fileService.upload(image));
        } else {
            addCustomHabitDtoRequest.setImage(DEFAULT_TITLE_IMAGE_PATH);
        }

        Habit habit = habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest));
        habit.setUserId(user.getId());
        Set<Long> tagIds = addCustomHabitDtoRequest.getTagIds();

        habit.setTags(tagIds.stream().map(tagId -> tagsRepo.findById(tagId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TAG_NOT_FOUND + tagId))).collect(Collectors.toSet()));

        List<HabitTranslation> habitTranslationListForUa =
            habitTranslationMapper.mapAllToList((addCustomHabitDtoRequest.getHabitTranslations()));
        habitTranslationListForUa.forEach(habitTranslation -> habitTranslation.setHabit(habit));
        habitTranslationListForUa.forEach(habitTranslation -> habitTranslation.setLanguage(
            languageRepo.findByCode("ua")
                .orElseThrow(NoSuchElementException::new)));
        habitTranslationRepo.saveAll(habitTranslationListForUa);

        List<HabitTranslation> habitTranslationListForEn =
            habitTranslationMapper.mapAllToList((addCustomHabitDtoRequest.getHabitTranslations()));
        habitTranslationListForEn.forEach(habitTranslation -> habitTranslation.setHabit(habit));
        habitTranslationListForEn.forEach(habitTranslation -> habitTranslation.setLanguage(
            languageRepo.findByCode("en")
                .orElseThrow(NoSuchElementException::new)));
        habitTranslationRepo.saveAll(habitTranslationListForEn);

        List<CustomShoppingListItem> customShoppingListItems =
            customShoppingListMapper.mapAllToList(addCustomHabitDtoRequest.getCustomShoppingListItemDto());
        customShoppingListItems.forEach(customShoppingListItem -> customShoppingListItem.setHabit(habit));
        customShoppingListItems.forEach(customShoppingListItem -> customShoppingListItem.setUser(user));
        customShoppingListItemRepo.saveAll(customShoppingListItems);
        return buildAddCustomHabitDtoResponse(habit, user.getId());
    }

    /**
     * Method that build {@link AddCustomHabitDtoResponse} from {@link Habit}.
     *
     * @param habit  {@link Habit}
     * @param userId {@link Long}
     * @return {@link AddCustomHabitDtoResponse}
     * @author Lilia Mokhnatska
     */
    private AddCustomHabitDtoResponse buildAddCustomHabitDtoResponse(Habit habit, Long userId) {
        AddCustomHabitDtoResponse response = modelMapper.map(habit, AddCustomHabitDtoResponse.class);

        response.setCustomShoppingListItemDto(customShoppingListResponseDtoMapper
            .mapAllToList(customShoppingListItemRepo.findAllByUserIdAndHabitId(userId, habit.getId())));
        response.setTagIds(habit.getTags().stream().map(Tag::getId).collect(Collectors.toSet()));
        response
            .setHabitTranslations(habitTranslationDtoMapper.mapAllToList(habitTranslationRepo.findAllByHabit(habit)));
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserProfilePictureDto> getFriendsAssignedToHabitProfilePictures(Long habitId, Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        if (!habitRepo.existsById(habitId)) {
            throw new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId);
        }
        List<User> users = userRepo.getFriendsAssignedToHabit(userId, habitId);
        return users.stream().map(user -> modelMapper.map(user, UserProfilePictureDto.class))
            .collect(Collectors.toList());
    }
}
