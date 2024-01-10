package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemVO;
import greencity.dto.user.UserVO;
import greencity.entity.CustomShoppingListItem;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.CustomShoppingListItemNotSavedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.CustomShoppingListItemRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserShoppingListItemRepo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static greencity.constant.ErrorMessage.CUSTOM_SHOPPING_LIST_ITEM_NOT_FOUND_BY_ID;

/**
 * The class provides implementation of the
 * {@code CustomShoppingListItemService}.
 */
@Service
@AllArgsConstructor
public class CustomShoppingListItemServiceImpl implements CustomShoppingListItemService {
    /**
     * Autowired repository.
     */
    private CustomShoppingListItemRepo customShoppingListItemRepo;
    private UserShoppingListItemRepo userShoppingListItemRepo;
    private ModelMapper modelMapper;
    private RestClient restClient;
    private HabitAssignRepo habitAssignRepo;

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<CustomShoppingListItemResponseDto> save(BulkSaveCustomShoppingListItemDto bulkSave, Long userId,
        Long habitAssignId) {
        UserVO userVO = restClient.findById(userId);
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitAssignId));
        User user = modelMapper.map(userVO, User.class);
        List<CustomShoppingListItemSaveRequestDto> dto = bulkSave.getCustomShoppingListItemSaveRequestDtoList();
        List<String> errorMessages = findDuplicates(dto, user, habitAssign.getHabit());
        if (!errorMessages.isEmpty()) {
            throw new CustomShoppingListItemNotSavedException(
                ErrorMessage.CUSTOM_SHOPPING_LIST_ITEM_WHERE_NOT_SAVED + errorMessages);
        }
        List<CustomShoppingListItem> items = user.getCustomShoppingListItems();
        for (CustomShoppingListItem item : items) {
            item.setHabit(habitAssign.getHabit());
        }
        customShoppingListItemRepo.saveAll(items);
        return user.getCustomShoppingListItems().stream()
            .map(customShoppingListItem -> modelMapper.map(customShoppingListItem,
                CustomShoppingListItemResponseDto.class))
            .collect(Collectors.toList());
    }

    /**
     * Method for finding duplicates {@link CustomShoppingListItem} in user data
     * before saving.
     *
     * @param dto  {@link CustomShoppingListItemSaveRequestDto}`s for saving and
     *             finding duplicates.
     * @param user {@link User} for whom shopping list item are will saving.
     * @return list with the text of {@link CustomShoppingListItem} which is
     *         duplicated.
     * @author Bogdan Kuzenko.
     */
    private List<String> findDuplicates(List<CustomShoppingListItemSaveRequestDto> dto,
        User user, Habit habit) {
        List<String> errorMessages = new ArrayList<>();
        for (CustomShoppingListItemSaveRequestDto el : dto) {
            CustomShoppingListItem customShoppingListItem = modelMapper.map(el, CustomShoppingListItem.class);
            List<CustomShoppingListItem> duplicate = user.getCustomShoppingListItems().stream()
                .filter(o -> o.getText().equals(customShoppingListItem.getText())).toList();
            if (duplicate.isEmpty()) {
                customShoppingListItem.setUser(user);
                customShoppingListItem.setHabit(habit);
                user.getCustomShoppingListItems().add(customShoppingListItem);
                habit.setCustomShoppingListItems(user.getCustomShoppingListItems());
            } else {
                errorMessages.add(customShoppingListItem.getText());
            }
        }
        return errorMessages;
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<CustomShoppingListItemResponseDto> findAll() {
        return customShoppingListItemRepo.findAll().stream()
            .map(customcustomShoppingListItem -> modelMapper.map(customcustomShoppingListItem,
                CustomShoppingListItemResponseDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public CustomShoppingListItemResponseDto findById(Long id) {
        return modelMapper.map(findOne(id), CustomShoppingListItemResponseDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public CustomShoppingListItemResponseDto updateItemStatus(Long userId, Long itemId, String itemStatus) {
        CustomShoppingListItem customShoppingListItem =
            customShoppingListItemRepo.findByUserIdAndItemId(userId, itemId);
        if (customShoppingListItem == null) {
            throw new NotFoundException(CUSTOM_SHOPPING_LIST_ITEM_NOT_FOUND_BY_ID);
        }
        if (itemStatus.equalsIgnoreCase(ShoppingListItemStatus.DONE.name())) {
            customShoppingListItem.setStatus(ShoppingListItemStatus.DONE);
            return modelMapper.map(customShoppingListItemRepo.save(customShoppingListItem),
                CustomShoppingListItemResponseDto.class);
        }
        if (itemStatus.equalsIgnoreCase(ShoppingListItemStatus.ACTIVE.name())) {
            customShoppingListItem.setStatus(ShoppingListItemStatus.ACTIVE);
            return modelMapper.map(customShoppingListItemRepo.save(customShoppingListItem),
                CustomShoppingListItemResponseDto.class);
        }
        if (itemStatus.equalsIgnoreCase(ShoppingListItemStatus.DISABLED.name())) {
            customShoppingListItem.setStatus(ShoppingListItemStatus.DISABLED);
            return modelMapper.map(customShoppingListItemRepo.save(customShoppingListItem),
                CustomShoppingListItemResponseDto.class);
        }
        if (itemStatus.equalsIgnoreCase(ShoppingListItemStatus.INPROGRESS.name())) {
            customShoppingListItem.setStatus(ShoppingListItemStatus.INPROGRESS);
            return modelMapper.map(customShoppingListItemRepo.save(customShoppingListItem),
                CustomShoppingListItemResponseDto.class);
        }
        throw new BadRequestException(ErrorMessage.INCORRECT_INPUT_ITEM_STATUS);
    }

    /**
     * {@inheritDoc}
     *
     * @author Volodia Lesko.
     */
    @Override
    public void updateItemStatusToDone(Long userId, Long itemId) {
        Long userShoppingListItemId = userShoppingListItemRepo.getByUserAndItemId(userId, itemId)
            .orElseThrow(() -> new NotFoundException(CUSTOM_SHOPPING_LIST_ITEM_NOT_FOUND_BY_ID + " " + itemId));
        UserShoppingListItem userShoppingListItem = userShoppingListItemRepo.getReferenceById(userShoppingListItemId);
        userShoppingListItem.setStatus(ShoppingListItemStatus.DONE);
        userShoppingListItemRepo.save(userShoppingListItem);
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<CustomShoppingListItemResponseDto> findAllByUserAndHabit(Long userId, Long habitId) {
        List<CustomShoppingListItemResponseDto> customShoppingListItems =
            customShoppingListItemRepo.findAllByUserIdAndHabitId(userId, habitId).stream()
                .map(customShoppingListItem -> modelMapper.map(customShoppingListItem,
                    CustomShoppingListItemResponseDto.class))
                .collect(Collectors.toList());
        if (!customShoppingListItems.isEmpty()) {
            return customShoppingListItems;
        } else {
            throw new NotFoundException(ErrorMessage.CUSTOM_SHOPPING_LIST_ITEM_NOT_FOUND);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<Long> bulkDelete(String ids) {
        List<Long> arrayIds = Arrays
            .stream(ids.split(","))
            .map(Long::valueOf)
            .toList();

        List<Long> deleted = new ArrayList<>();
        for (Long id : arrayIds) {
            deleted.add(delete(id));
        }
        return deleted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomShoppingListItemResponseDto> findAllAvailableCustomShoppingListItems(Long userId, Long habitId) {
        return modelMapper.map(
            customShoppingListItemRepo.findAllAvailableCustomShoppingListItemsForUserId(userId, habitId),
            new TypeToken<List<CustomShoppingListItemResponseDto>>() {
            }.getType());
    }

    @Override
    public List<CustomShoppingListItemResponseDto> findAllCustomShoppingListItemsWithStatusInProgress(Long userId,
        Long habitId) {
        return customShoppingListItemRepo
            .findAllCustomShoppingListItemsForUserIdAndHabitIdInProgress(userId, habitId)
            .stream()
            .map(customShoppingListItem -> modelMapper.map(customShoppingListItem,
                CustomShoppingListItemResponseDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomShoppingListItemResponseDto> findAllAvailableCustomShoppingListItemsByHabitAssignId(Long userId,
        Long habitAssignId) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        if (!habitAssign.getUser().getId().equals(userId)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        Long habitId = habitAssign.getHabit().getId();

        List<CustomShoppingListItem> customShoppingListItemList =
            customShoppingListItemRepo.findAllAvailableCustomShoppingListItemsForUserId(userId, habitId);

        return customShoppingListItemList
            .stream().map(item -> modelMapper.map(item, CustomShoppingListItemResponseDto.class))
            .collect(Collectors.toList());
    }

    /**
     * Method for deleting custom shopping list item by id.
     *
     * @param id {@link CustomShoppingListItem} id.
     * @return id of deleted {@link CustomShoppingListItem}
     * @author Bogdan Kuzenko.
     */
    private Long delete(Long id) {
        try {
            customShoppingListItemRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(CUSTOM_SHOPPING_LIST_ITEM_NOT_FOUND_BY_ID + " " + id);
        }
        return id;
    }

    /**
     * Method for get one custom shopping list item by id.
     *
     * @param id a value of {@link Long}
     * @return {@link CustomShoppingListItem}
     * @author Bogdan Kuzenko.
     */
    private CustomShoppingListItem findOne(Long id) {
        return customShoppingListItemRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(CUSTOM_SHOPPING_LIST_ITEM_NOT_FOUND_BY_ID + " " + id));
    }

    /**
     * Method returns all user's custom shopping items by status(if is defined).
     *
     * @param userId user id.
     * @return list of {@link CustomShoppingListItemVO}
     * @author Max Bohonko.
     */
    @Override
    public List<CustomShoppingListItemResponseDto> findAllUsersCustomShoppingListItemsByStatus(Long userId,
        String status) {
        List<CustomShoppingListItem> customShoppingListItems;
        if (status != null
            && Arrays.stream(ShoppingListItemStatus.values())
                .anyMatch(itemStatus -> itemStatus.toString().equals(status))) {
            customShoppingListItems = customShoppingListItemRepo.findAllByUserIdAndStatus(userId, status);
        } else {
            customShoppingListItems = customShoppingListItemRepo.findAllByUserId(userId);
        }
        return customShoppingListItems.stream()
            .map(item -> modelMapper.map(item, CustomShoppingListItemResponseDto.class)).collect(Collectors.toList());
    }
}
