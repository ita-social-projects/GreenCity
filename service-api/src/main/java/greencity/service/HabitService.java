package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.HabitVO;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HabitService {
    /**
     * Method finds {@code Habit} by id and language code.
     *
     * @param id           {@code Habit} id.
     * @param languageCode - language code.
     * @return {@link HabitDto}.
     */
    HabitDto getByIdAndLanguageCode(Long id, String languageCode);

    /**
     * Method returns all {@code Habit}'s by language code.
     *
     * @param pageable - instance of {@link Pageable}.
     * @param language - language code.
     * @return Pageable of {@link HabitDto}.
     */
    PageableDto<HabitDto> getAllHabitsByLanguageCode(Pageable pageable, String language);

    /**
     * Method returns shopping list in specific language by habit id.
     *
     * @return list {@link ShoppingListItemDto}.
     * @author Dmytro Khonko
     */
    List<ShoppingListItemDto> getShoppingListForHabit(Long habitId, String lang);

    /**
     * Method that find all habit's translations by language code and tags.
     *
     * @param pageable     {@link Pageable}
     * @param tags         {@link List} of {@link String} tags
     * @param languageCode language code {@link String}
     *
     * @return {@link PageableDto} of {@link HabitDto}.
     * @author Markiyan Derevetskyi
     */
    PageableDto<HabitDto> getAllByTagsAndLanguageCode(Pageable pageable, List<String> tags, String languageCode);

    /**
     * Method that add shopping list item To Habit by habit id and shopping list
     * item id.
     * 
     * @author Marian Diakiv
     */
    void addShoppingListItemToHabit(Long habitId, Long itemId);

    /**
     * Method for deleting the {@link ShoppingListItemDto} instance by its id.
     *
     * @param itemId  - {@link ShoppingListItemDto} instance id which will be
     *                deleted.
     * @param habitId - {@link HabitDto} the id of the instance from which it will
     *                be deleted.
     * @author Marian Diakiv
     */
    void deleteShoppingListItem(Long habitId, Long itemId);

    /**
     * Method deletes all {@link ShoppingListItemDto} by list of ids.
     *
     * @param listId  list of id {@link ShoppingListItemDto}
     * @param habitId - {@link HabitDto} the id of the instance from which it will
     *                be deleted. return list of id {@link ShoppingListItemDto}
     * @author Marian Diakiv
     */
    List<Long> deleteAllShoppingListItemsByListOfId(Long habitId, List<Long> listId);

    /**
     * Method add all {@link ShoppingListItemDto} by list of ids.
     *
     * @param listId  list of id {@link ShoppingListItemDto}
     * @param habitId - {@link HabitDto} the id of the instance to which it will be
     *                added return list of id {@link ShoppingListItemDto}
     * @author Marian Diakiv
     */
    List<Long> addAllShoppingListItemsByListOfId(Long habitId, List<Long> listId);

    /**
     * Method to save {@link AddCustomHabitDtoResponse}.
     *
     * @param addCustomHabitDtoRequest dto with {@link AddCustomHabitDtoRequest}
     *                                 entered info about field that need to edit.
     * @param userEmail                {@link String} - user email.
     * @return {@link AddCustomHabitDtoResponse} instance.
     * @author Lilia Mokhnatska
     */
    AddCustomHabitDtoResponse addCustomHabit(AddCustomHabitDtoRequest addCustomHabitDtoRequest, MultipartFile image,
        String userEmail);

    /**
     * Retrieves the list of profile pictures of the user's friends (which have
     * INPROGRESS assign to the habit).
     *
     * @param habitId {@link HabitVO} id.
     * @param userId  {@link UserVO} id.
     * @return List of friends' profile pictures.
     */
    List<UserProfilePictureDto> getFriendsAssignedToHabitProfilePictures(Long habitId, Long userId);
}
