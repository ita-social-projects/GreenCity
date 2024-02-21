package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.shoppinglistitem.ShoppingListItemManagementDto;
import greencity.dto.shoppinglistitem.ShoppingListItemPostDto;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.shoppinglistitem.ShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.ShoppingListItemViewDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.HabitAssign;
import greencity.entity.UserShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.ShoppingListItemNotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.exception.exceptions.UserHasNoShoppingListItemsException;
import greencity.exception.exceptions.UserShoppingListItemStatusNotUpdatedException;
import greencity.exception.exceptions.WrongIdException;
import greencity.filters.ShoppingListItemSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.ShoppingListItemRepo;
import greencity.repository.ShoppingListItemTranslationRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserShoppingListItemRepo;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class ShoppingListItemServiceImpl implements ShoppingListItemService {
    private final ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;
    private final ShoppingListItemRepo shoppingListItemRepo;
    private final ModelMapper modelMapper;
    private final UserShoppingListItemRepo userShoppingListItemRepo;
    private final HabitAssignRepo habitAssignRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ShoppingListItemDto> findAll(String language) {
        return shoppingListItemTranslationRepo
            .findAllByLanguageCode(language)
            .stream()
            .map(g -> modelMapper.map(g, ShoppingListItemDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageTranslationDTO> saveShoppingListItem(ShoppingListItemPostDto item) {
        ShoppingListItem savedShoppingListItem = modelMapper.map(item, ShoppingListItem.class);
        savedShoppingListItem.getTranslations().forEach(a -> a.setShoppingListItem(savedShoppingListItem));
        shoppingListItemRepo.save(savedShoppingListItem);
        return modelMapper.map(savedShoppingListItem.getTranslations(),
            new TypeToken<List<LanguageTranslationDTO>>() {
            }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageTranslationDTO> update(ShoppingListItemPostDto shoppingListItemPostDto) {
        Optional<ShoppingListItem> optionalItem =
            shoppingListItemRepo.findById(shoppingListItemPostDto.getShoppingListItem().getId());
        if (optionalItem.isPresent()) {
            ShoppingListItem updatedShoppingListItem = optionalItem.get();
            updateTranslations(updatedShoppingListItem.getTranslations(), shoppingListItemPostDto.getTranslations());
            shoppingListItemRepo.save(updatedShoppingListItem);
            return modelMapper.map(updatedShoppingListItem.getTranslations(),
                new TypeToken<List<LanguageTranslationDTO>>() {
                }.getType());
        } else {
            throw new ShoppingListItemNotFoundException(ErrorMessage.SHOPPING_LIST_ITEM_NOT_FOUND_BY_ID);
        }
    }

    private void updateTranslations(List<ShoppingListItemTranslation> oldTranslations,
        List<LanguageTranslationDTO> newTranslations) {
        oldTranslations.forEach(itemTranslation -> itemTranslation.setContent(newTranslations.stream()
            .filter(newTranslation -> newTranslation.getLanguage().getCode()
                .equals(itemTranslation.getLanguage().getCode()))
            .findFirst().get()
            .getContent()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShoppingListItemResponseDto findShoppingListItemById(Long id) {
        Optional<ShoppingListItem> item = shoppingListItemRepo.findById(id);
        if (item.isPresent()) {
            return modelMapper.map(item.get(), ShoppingListItemResponseDto.class);
        } else {
            throw new ShoppingListItemNotFoundException(ErrorMessage.SHOPPING_LIST_ITEM_NOT_FOUND_BY_ID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long delete(Long itemId) {
        try {
            shoppingListItemRepo.deleteById(itemId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotDeletedException(ErrorMessage.SHOPPING_LIST_ITEM_NOT_DELETED);
        }
        return itemId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<ShoppingListItemManagementDto> findShoppingListItemsForManagementByPage(
        Pageable pageable) {
        Page<ShoppingListItem> shoppingListItems = shoppingListItemRepo.findAll(pageable);
        List<ShoppingListItemManagementDto> shoppingListItemManagementDtos =
            shoppingListItems.getContent().stream()
                .map(item -> modelMapper.map(item, ShoppingListItemManagementDto.class))
                .collect(Collectors.toList());
        return getPagebleAdvancedDto(shoppingListItemManagementDtos, shoppingListItems);
    }

    private PageableAdvancedDto<ShoppingListItemManagementDto> getPagebleAdvancedDto(
        List<ShoppingListItemManagementDto> shoppingListItemManagementDtos, Page<ShoppingListItem> shoppingListItems) {
        return new PageableAdvancedDto<>(
            shoppingListItemManagementDtos,
            shoppingListItems.getTotalElements(),
            shoppingListItems.getPageable().getPageNumber(),
            shoppingListItems.getTotalPages(),
            shoppingListItems.getNumber(),
            shoppingListItems.hasPrevious(),
            shoppingListItems.hasNext(),
            shoppingListItems.isFirst(),
            shoppingListItems.isLast());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> deleteAllShoppingListItemsByListOfId(List<Long> listId) {
        listId.forEach(this::delete);
        return listId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<ShoppingListItemManagementDto> searchBy(Pageable paging, String query) {
        Page<ShoppingListItem> shoppingListItems = shoppingListItemRepo.searchBy(paging, query);
        List<ShoppingListItemManagementDto> shoppingListItemManagementDtos = shoppingListItems.stream()
            .map(item -> modelMapper.map(item, ShoppingListItemManagementDto.class))
            .collect(Collectors.toList());
        return getPagebleAdvancedDto(shoppingListItemManagementDtos, shoppingListItems);
    }

    /**
     * * This method used for build {@link SearchCriteria} depends on
     * {@link ShoppingListItemDto}.
     *
     * @param dto used for receive parameters for filters from UI.
     * @return {@link SearchCriteria}.
     */
    private List<SearchCriteria> buildSearchCriteria(ShoppingListItemViewDto dto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        setValueIfNotEmpty(criteriaList, "id", dto.getId());
        setValueIfNotEmpty(criteriaList, "content", dto.getContent());
        return criteriaList;
    }

    /**
     * Returns {@link ShoppingListItemSpecification} for entered filter parameters.
     *
     * @param shoppingListItemViewDto contains data from filters
     */
    private ShoppingListItemSpecification getSpecification(ShoppingListItemViewDto shoppingListItemViewDto) {
        List<SearchCriteria> searchCriteria = buildSearchCriteria(shoppingListItemViewDto);
        return new ShoppingListItemSpecification(searchCriteria);
    }

    /**
     * Method that adds new {@link SearchCriteria}.
     *
     * @param searchCriteria - list of existing {@link SearchCriteria}
     * @param key            - key of field
     * @param value          - value of field
     */
    private void setValueIfNotEmpty(List<SearchCriteria> searchCriteria, String key, String value) {
        if (StringUtils.hasLength(value)) {
            searchCriteria.add(SearchCriteria.builder()
                .key(key)
                .type(key)
                .value(value)
                .build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<ShoppingListItemManagementDto> getFilteredDataForManagementByPage(Pageable pageable,
        ShoppingListItemViewDto dto) {
        Page<ShoppingListItem> pages = shoppingListItemRepo.findAll(getSpecification(dto), pageable);
        return getPagesFilteredPages(pages);
    }

    private PageableAdvancedDto<ShoppingListItemManagementDto> getPagesFilteredPages(Page<ShoppingListItem> pages) {
        List<ShoppingListItemManagementDto> shoppingListItemManagementDtos = pages.getContent()
            .stream()
            .map(item -> modelMapper.map(item, ShoppingListItemManagementDto.class))
            .collect(Collectors.toList());
        return getPagebleAdvancedDto(shoppingListItemManagementDtos, pages);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<UserShoppingListItemResponseDto> saveUserShoppingListItems(Long userId, Long habitId,
        List<ShoppingListItemRequestDto> dtoList,
        String language) {
        if (dtoList != null) {
            Optional<HabitAssign> habitAssign = habitAssignRepo.findByHabitIdAndUserId(habitId, userId);
            if (habitAssign.isPresent()) {
                saveShoppingListItemsForHabitAssign(habitAssign.get(), dtoList);
            } else {
                throw new UserHasNoShoppingListItemsException(ErrorMessage.USER_HAS_NO_SHOPPING_LIST_ITEMS);
            }
        }
        return getUserShoppingList(userId, habitId, language);
    }

    /**
     * Method save user shopping list item with item dictionary.
     *
     * @param dtoList list {@link ShoppingListItemRequestDto} for saving
     * @author Dmytro Khonko
     */
    private void saveShoppingListItemsForHabitAssign(HabitAssign habitAssign,
        List<ShoppingListItemRequestDto> dtoList) {
        for (ShoppingListItemRequestDto el : dtoList) {
            saveUserShoppingListItemForShoppingList(el, habitAssign);
        }
    }

    private void saveUserShoppingListItemForShoppingList(ShoppingListItemRequestDto dto, HabitAssign habitAssign) {
        if (isAssignedToHabit(dto, habitAssign)) {
            if (isAssignedToUser(dto, habitAssign)) {
                saveUserShoppingListItem(dto, habitAssign);
            } else {
                throw new WrongIdException(ErrorMessage.SHOPPING_LIST_ITEM_ALREADY_SELECTED + dto.getId());
            }
        } else {
            throw new NotFoundException(ErrorMessage.SHOPPING_LIST_ITEM_NOT_ASSIGNED_FOR_THIS_HABIT + dto.getId());
        }
    }

    private boolean isAssignedToHabit(ShoppingListItemRequestDto dto, HabitAssign habitAssign) {
        List<Long> ids = userShoppingListItemRepo.getShoppingListItemsIdForHabit(habitAssign.getHabit().getId());
        return ids.contains(dto.getId());
    }

    private boolean isAssignedToUser(ShoppingListItemRequestDto dto, HabitAssign habitAssign) {
        List<Long> assignedIds = userShoppingListItemRepo.getAllAssignedShoppingListItems(habitAssign.getId());
        return !assignedIds.contains(dto.getId());
    }

    private void saveUserShoppingListItem(ShoppingListItemRequestDto dto, HabitAssign habitAssign) {
        UserShoppingListItem userShoppingListItem = modelMapper.map(dto, UserShoppingListItem.class);
        userShoppingListItem.setHabitAssign(habitAssign);
        habitAssign.getUserShoppingListItems().add(userShoppingListItem);
        userShoppingListItemRepo.saveAll(habitAssign.getUserShoppingListItems());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<UserShoppingListItemResponseDto> getUserShoppingList(Long userId, Long habitId, String language) {
        Optional<HabitAssign> habitAssign = habitAssignRepo.findByHabitIdAndUserId(habitId, userId);
        if (habitAssign.isPresent()) {
            List<UserShoppingListItemResponseDto> itemsDtos = getAllUserShoppingListItems(habitAssign.get());
            itemsDtos.forEach(el -> setTextForUserShoppingListItem(el, language));
            return itemsDtos;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserShoppingListItemResponseDto> getUserShoppingListByHabitAssignId(Long userId, Long habitAssignId,
        String language) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        if (!habitAssign.getUser().getId().equals(userId)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        List<UserShoppingListItemResponseDto> itemsDtos = getAllUserShoppingListItems(habitAssign);
        itemsDtos.forEach(el -> setTextForUserShoppingListItem(el, language));
        return itemsDtos;
    }

    private List<UserShoppingListItemResponseDto> getAllUserShoppingListItems(HabitAssign habitAssign) {
        return userShoppingListItemRepo
            .findAllByHabitAssingId(habitAssign.getId())
            .stream()
            .map(userShoppingListItem -> modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class))
            .collect(Collectors.toList());
    }

    /**
     * Method for setting text either for UserShoppingListItem with localization.
     *
     * @param dto {@link ShoppingListItemDto}
     */
    private void setTextForUserShoppingListItem(UserShoppingListItemResponseDto dto, String language) {
        String text =
            shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(language, dto.getId()).getContent();
        dto.setText(text);
    }

    @Transactional
    @Override
    public List<UserShoppingListItemResponseDto> getUserShoppingListItemsByHabitAssignIdAndStatusInProgress(
        Long habitAssignId, String language) {
        return userShoppingListItemRepo
            .findUserShoppingListItemsByHabitAssignIdAndStatusInProgress(habitAssignId)
            .stream()
            .map(userShoppingListItem -> converterUserShoppingListItemResponseDto(userShoppingListItem, language))
            .collect(Collectors.toList());
    }

    /**
     * Method converts UserShoppingListItem to UserShoppingListItemResponseDto and
     * sets text for UserShoppingListItemResponseDto with localization.
     *
     * @param userShoppingListItem {@link UserShoppingListItem}
     * @param language             {@link String}
     */
    private UserShoppingListItemResponseDto converterUserShoppingListItemResponseDto(
        UserShoppingListItem userShoppingListItem, String language) {
        UserShoppingListItemResponseDto dto =
            modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class);
        setTextForUserShoppingListItem(dto, language);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(Long itemId, Long userId, Long habitId) {
        userShoppingListItemRepo.deleteByShoppingListItemIdAndHabitAssignId(itemId,
            getHabitAssignByHabitIdAndUserIdAndSuspendedFalse(userId, habitId).getId());
    }

    private HabitAssign getHabitAssignByHabitIdAndUserIdAndSuspendedFalse(Long userId, Long habitId) {
        Optional<HabitAssign> habitAssign = habitAssignRepo.findByHabitIdAndUserId(habitId, userId);
        if (habitAssign.isPresent()) {
            return habitAssign.get();
        }
        throw new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserShoppingListItemResponseDto updateUserShopingListItemStatus(Long userId, Long itemId, String language) {
        UserShoppingListItem userShoppingListItem;
        userShoppingListItem = userShoppingListItemRepo.getReferenceById(itemId);
        if (isActive(userShoppingListItem)) {
            changeStatusToDone(userShoppingListItem);
        } else {
            throw new UserShoppingListItemStatusNotUpdatedException(
                ErrorMessage.USER_SHOPPING_LIST_ITEMS_STATUS_IS_ALREADY_DONE + userShoppingListItem.getId());
        }
        UserShoppingListItemResponseDto updatedUserShoppingListItem =
            modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class);
        setTextForUserShoppingListItem(updatedUserShoppingListItem, language);
        return updatedUserShoppingListItem;
    }

    private boolean isActive(UserShoppingListItem userShoppingListItem) {
        try {
            if (ShoppingListItemStatus.ACTIVE.equals(userShoppingListItem.getStatus())) {
                return true;
            }
        } catch (Exception e) {
            throw new UserShoppingListItemStatusNotUpdatedException(
                ErrorMessage.USER_SHOPPING_LIST_ITEM_NOT_FOUND + userShoppingListItem.getId());
        }
        return false;
    }

    private void changeStatusToDone(UserShoppingListItem userShoppingListItem) {
        userShoppingListItem.setStatus(ShoppingListItemStatus.DONE);
        userShoppingListItem.setDateCompleted(LocalDateTime.now());
        userShoppingListItemRepo.save(userShoppingListItem);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<UserShoppingListItemResponseDto> updateUserShoppingListItemStatus(Long userId,
        Long userShoppingListItemId,
        String language,
        String status) {
        String statusUpperCase = status.toUpperCase();
        List<UserShoppingListItem> userShoppingListItems =
            userShoppingListItemRepo.getAllByUserShoppingListIdAndUserId(userShoppingListItemId, userId);
        if (userShoppingListItems == null || userShoppingListItems.isEmpty()) {
            throw new NotFoundException(ErrorMessage.USER_SHOPPING_LIST_ITEM_NOT_FOUND_BY_USER_ID);
        }
        if (Arrays.stream(ShoppingListItemStatus.values()).noneMatch(s -> s.name().equalsIgnoreCase(statusUpperCase))) {
            throw new BadRequestException(ErrorMessage.INCORRECT_INPUT_ITEM_STATUS);
        }
        userShoppingListItems.forEach(u -> u.setStatus(ShoppingListItemStatus.valueOf(statusUpperCase)));
        userShoppingListItemRepo.saveAll(userShoppingListItems);
        List<UserShoppingListItemResponseDto> userShoppingListItemResponseDtoList =
            userShoppingListItems.stream()
                .map(u -> modelMapper.map(u, UserShoppingListItemResponseDto.class))
                .collect(Collectors.toList());
        userShoppingListItemResponseDtoList.forEach(u -> setTextForUserShoppingListItem(u, language));
        return userShoppingListItemResponseDtoList;
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko
     */
    @Transactional
    @Override
    public List<Long> deleteUserShoppingListItems(String ids) {
        List<Long> arrayId = Arrays.stream(ids.split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList());

        List<Long> deleted = new ArrayList<>();
        for (Long id : arrayId) {
            deleted.add(deleteUserShoppingListItem(id));
        }
        return deleted;
    }

    /**
     * Method for deleting user shopping list item by id.
     *
     * @param id {@link UserShoppingListItem} id.
     * @return id of deleted {@link UserShoppingListItem}
     * @author Bogdan Kuzenko
     */
    private Long deleteUserShoppingListItem(Long id) {
        UserShoppingListItem userShoppingListItem = userShoppingListItemRepo
            .findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_SHOPPING_LIST_ITEM_NOT_FOUND + id));
        userShoppingListItemRepo.delete(userShoppingListItem);
        return id;
    }

    @Override
    public List<ShoppingListItemManagementDto> getShoppingListByHabitId(Long habitId) {
        List<Long> idList =
            shoppingListItemRepo.getAllShoppingListItemIdByHabitIdISContained(habitId);
        List<ShoppingListItem> shoppingListItems;
        if (!idList.isEmpty()) {
            shoppingListItems = shoppingListItemRepo.getShoppingListByListOfId(idList);
        } else {
            shoppingListItems = new ArrayList<>();
        }

        return shoppingListItems.stream()
            .map(listItem -> modelMapper.map(listItem, ShoppingListItemManagementDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public PageableAdvancedDto<ShoppingListItemManagementDto> findAllShoppingListItemsForManagementPageNotContained(
        Long habitId,
        Pageable pageable) {
        List<Long> items =
            shoppingListItemRepo.getAllShoppingListItemsByHabitIdNotContained(habitId);
        Page<ShoppingListItem> shoppingListItems =
            shoppingListItemRepo
                .getShoppingListByListOfIdPageable(items, pageable);
        List<ShoppingListItemManagementDto> shoppingListItemManagementDtos =
            shoppingListItems.getContent().stream()
                .map(item -> modelMapper.map(item, ShoppingListItemManagementDto.class))
                .collect(Collectors.toList());
        return getPagebleAdvancedDto(shoppingListItemManagementDtos, shoppingListItems);
    }

    @Override
    public List<ShoppingListItemDto> findInProgressByUserIdAndLanguageCode(Long userId, String code) {
        List<ShoppingListItemDto> shoppingListItemDtos = shoppingListItemRepo
            .findInProgressByUserIdAndLanguageCode(userId, code)
            .stream()
            .map(shoppingListItemTranslation -> modelMapper.map(shoppingListItemTranslation, ShoppingListItemDto.class))
            .collect(Collectors.toList());
        shoppingListItemDtos.forEach(x -> x.setStatus(ShoppingListItemStatus.INPROGRESS.toString()));
        return shoppingListItemDtos;
    }
}
