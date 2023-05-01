package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
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
import java.util.Set;
import java.util.stream.Collectors;

import greencity.repository.CustomShoppingListItemRepo;
import greencity.repository.LanguageRepo;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
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
        if (addCustomHabitDtoRequest.getImage() != null) {
            image = fileService.convertToMultipartImage(addCustomHabitDtoRequest.getImage());
        }
        if (image != null) {
            addCustomHabitDtoRequest.setImage(fileService.upload(image));
        }
        Habit habit = habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest));
        Set<Long> tagIds = addCustomHabitDtoRequest.getTagIds();

        habit.setTags(tagIds.stream().map(tagId -> tagsRepo.findById(tagId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TAG_NOT_FOUND + tagId))).collect(Collectors.toSet()));
        habit.setUserId(user.getId());

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
}
