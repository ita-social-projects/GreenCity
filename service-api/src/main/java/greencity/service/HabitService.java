package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.CustomHabitDtoRequest;
import greencity.dto.habit.CustomHabitDtoResponse;
import greencity.dto.habit.HabitVO;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

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
     * Method returns all default and custom which created by current user his
     * friends {@code Habit}'s.
     *
     * @param pageable - instance of {@link Pageable}.
     * @return Pageable of {@link HabitDto}.
     */
    PageableDto<HabitDto> getAllHabitsByLanguageCode(UserVO userVO, Pageable pageable, String languageCode);

    /**
     * Method returns all habits of the current user.
     *
     * @param pageable - instance of {@link Pageable}.
     * @return Pageable of {@link HabitDto}.
     */
    PageableDto<HabitDto> getMyHabits(Long userId, Pageable pageable, String languageCode);

    /**
     * Method returns all habits of a friend, both default and custom, for the
     * current user and the specified friend.
     *
     * @param userId   - ID of the current user.
     * @param friendId - ID of the friend whose habits are to be retrieved.
     * @param pageable - instance of {@link Pageable}.
     * @return Pageable of {@link HabitDto}.
     */
    PageableDto<HabitDto> getAllHabitsOfFriend(Long userId, Long friendId, Pageable pageable, String languageCode);

    /**
     * Method returns all mutual habits, both default and custom, that are shared
     * between the current user and a specified friend.
     *
     * @param userId   - ID of the current user.
     * @param friendId - ID of the friend whose mutual habits with the user are to
     *                 be retrieved.
     * @param pageable - instance of {@link Pageable}.
     * @return Pageable of {@link HabitDto}.
     */
    PageableDto<HabitDto> getAllMutualHabitsWithFriend(Long userId, Long friendId, Pageable pageable,
        String languageCode);

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
     * @param pageable        {@link Pageable}
     * @param tags            {@link List} of {@link String} tags
     * @param languageCode    language code {@link String}
     * @param excludeAssigned {@link boolean} flag to determine whether to exclude
     *                        habits already assigned to the specified user.
     * @param userId          {@link Long} representing the ID of the user for whom
     *                        assigned habits should be excluded (if applicable).
     *
     * @return {@link PageableDto} of {@link HabitDto}.
     * @author Markiyan Derevetskyi
     */
    PageableDto<HabitDto> getAllByTagsAndLanguageCode(Pageable pageable, List<String> tags, String languageCode,
        boolean excludeAssigned, Long userId);

    /**
     * Method that return all PageableDto of HabitDto by tags, isCustomHabit,
     * complexities, habitAssignStatus and language code.
     *
     * @param pageable      {@link Pageable}.
     * @param tags          {@link List} of {@link String}.
     * @param isCustomHabit {@link Boolean} value.
     * @param complexities  {@link List} of {@link Integer}.
     * @param languageCode  language code {@link String}.
     *
     * @return {@link PageableDto} of {@link HabitDto}.
     * @author Lilia Mokhnatska
     */
    PageableDto<HabitDto> getAllByDifferentParameters(UserVO userVO, Pageable pageable, Optional<List<String>> tags,
        Optional<Boolean> isCustomHabit, Optional<List<Integer>> complexities, String languageCode);

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
     * Method to save {@link CustomHabitDtoResponse}.
     *
     * @param addCustomHabitDtoRequest dto with {@link CustomHabitDtoRequest}
     *                                 entered info about field that need to edit.
     * @param userEmail                {@link String} - user email.
     * @return {@link CustomHabitDtoResponse} instance.
     * @author Lilia Mokhnatska
     */
    CustomHabitDtoResponse addCustomHabit(CustomHabitDtoRequest addCustomHabitDtoRequest, MultipartFile image,
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

    /**
     * Method to update {@link CustomHabitDtoResponse}.
     *
     * @param customHabitDtoRequest dto with {@link CustomHabitDtoRequest} entered
     *                              info about field that need to edit.
     * @param userEmail             {@link String} - user email.
     * @return {@link CustomHabitDtoResponse} instance.
     * @author Olena Sotnik.
     */
    CustomHabitDtoResponse updateCustomHabit(CustomHabitDtoRequest customHabitDtoRequest, Long habitId,
        String userEmail, MultipartFile image);

    /**
     * Method for deleting of custom habit by its id.
     *
     * @param customHabitId - id of custom habit to be deleted.
     * @param ownerEmail    - email of user who owns the habit.
     *
     * @author Olena Sotnik.
     */
    void deleteCustomHabit(Long customHabitId, String ownerEmail);

    /**
     * Method to like or dislike {@link HabitVO} specified by id.
     *
     * @param habitId id of {@link HabitVO} to like/dislike.
     * @param userVO  current {@link UserVO} that wants to like/dislike.
     */
    void like(Long habitId, UserVO userVO);
}
