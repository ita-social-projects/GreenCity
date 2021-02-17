package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.shoppinglistitem.BulkCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.CustomShoppingListItem;
import greencity.entity.User;
import greencity.exception.exceptions.CustomShoppingListItemNotSavedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomShoppingListItemRepo;
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
    private ModelMapper modelMapper;
    private RestClient restClient;

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<CustomShoppingListItemResponseDto> save(BulkSaveCustomShoppingListItemDto bulkSave, Long userId) {
        UserVO userVO = restClient.findById(userId);
        User user = modelMapper.map(userVO, User.class);
        List<CustomShoppingListItemSaveRequestDto> dto = bulkSave.getCustomShoppingListItemSaveRequestDtoList();
        List<String> errorMessages = findDuplicates(dto, user);
        if (!errorMessages.isEmpty()) {
            throw new CustomShoppingListItemNotSavedException(
                ErrorMessage.CUSTOM_SHOPPING_LIST_ITEM_WHERE_NOT_SAVED + errorMessages.toString());
        }
        customShoppingListItemRepo.saveAll(user.getCustomShoppingListItems());
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
    private List<String> findDuplicates(List<CustomShoppingListItemSaveRequestDto> dto, User user) {
        List<String> errorMessages = new ArrayList<>();
        for (CustomShoppingListItemSaveRequestDto el : dto) {
            CustomShoppingListItem customShoppingListItem = modelMapper.map(el, CustomShoppingListItem.class);
            List<CustomShoppingListItem> duplicate = user.getCustomShoppingListItems().stream()
                .filter(o -> o.getText().equals(customShoppingListItem.getText())).collect(Collectors.toList());
            if (duplicate.isEmpty()) {
                customShoppingListItem.setUser(user);
                user.getCustomShoppingListItems().add(customShoppingListItem);
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
    public List<CustomShoppingListItemResponseDto> updateBulk(
        BulkCustomShoppingListItemDto bulkCustomShoppingListItemDto) {
        List<CustomShoppingListItemResponseDto> updatable =
            bulkCustomShoppingListItemDto.getCustomShoppingListItemDto();
        List<CustomShoppingListItemResponseDto> returned = new ArrayList<>();
        for (CustomShoppingListItemResponseDto el : updatable) {
            returned.add(update(el));
        }
        return returned;
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<CustomShoppingListItemResponseDto> findAllByUser(Long userId) {
        List<CustomShoppingListItemResponseDto> customShoppingListItems =
            customShoppingListItemRepo.findAllByUserId(userId).stream()
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
            .collect(Collectors.toList());

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
    public List<CustomShoppingListItemResponseDto> findAllAvailableCustomShoppingListItems(Long userId) {
        return modelMapper.map(customShoppingListItemRepo.findAllAvailableCustomShoppingListItemsForUserId(userId),
            new TypeToken<List<CustomShoppingListItemResponseDto>>() {
            }.getType());
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
     * Method for update one object of user custom shopping list item.
     *
     * @param dto object for update.
     * @return {@link CustomShoppingListItemResponseDto}
     * @author Bogdan Kuzenko.
     */
    private CustomShoppingListItemResponseDto update(CustomShoppingListItemResponseDto dto) {
        CustomShoppingListItem updatable = findOne(dto.getId());
        List<CustomShoppingListItem> duplicate = updatable.getUser().getCustomShoppingListItems()
            .stream().filter(o -> o.getText().equals(dto.getText())).collect(Collectors.toList());
        if (duplicate.isEmpty()) {
            updatable.setText(dto.getText());
        } else {
            throw new CustomShoppingListItemNotSavedException(
                ErrorMessage.CUSTOM_SHOPPING_LIST_ITEM_WHERE_NOT_SAVED + dto.getText());
        }
        return modelMapper.map(updatable, CustomShoppingListItemResponseDto.class);
    }
}
