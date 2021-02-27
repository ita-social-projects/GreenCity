package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.shoppinglistitem.BulkCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.CustomShoppingListItem;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.ShoppingListItemStatus;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.CustomShoppingListItemNotSavedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomShoppingListItemRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.anyLong;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;

@ExtendWith(MockitoExtension.class)
class CustomShoppingListItemServiceImplTest {
    @Mock
    private CustomShoppingListItemRepo customShoppingListItemRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RestClient restClient;

    @InjectMocks
    private CustomShoppingListItemServiceImpl customShoppingListItemService;

    private User user =
        User.builder()
            .id(1L)
            .name("Test Testing")
            .email("test@gmail.com")
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .customShoppingListItems(new ArrayList<>())
            .build();

    @Test
    void saveEmptyBulkSaveCustomShoppingListItemDtoTest() {
        UserVO userVO = ModelUtils.getUserVO();
        when(restClient.findById(1L)).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        List<CustomShoppingListItemResponseDto> saveResult = customShoppingListItemService.save(
            new BulkSaveCustomShoppingListItemDto(Collections.emptyList()),
            1L);
        assertTrue(saveResult.isEmpty());
        assertTrue(user.getCustomShoppingListItems().isEmpty());
    }

    @Test
    void saveNonExistentBulkSaveCustomShoppingListItemDtoTest() {
        CustomShoppingListItemSaveRequestDto dtoToSave = new CustomShoppingListItemSaveRequestDto("foo");
        CustomShoppingListItem customShoppingListItem =
            new CustomShoppingListItem(1L, dtoToSave.getText(), null, null, null);
        UserVO userVO = ModelUtils.getUserVO();
        when(restClient.findById(1L)).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(dtoToSave, CustomShoppingListItem.class)).thenReturn(customShoppingListItem);
        when(modelMapper.map(customShoppingListItem, CustomShoppingListItemResponseDto.class))
            .thenReturn(new CustomShoppingListItemResponseDto(1L, "bar", ShoppingListItemStatus.DONE));
        List<CustomShoppingListItemResponseDto> saveResult = customShoppingListItemService.save(
            new BulkSaveCustomShoppingListItemDto(Collections.singletonList(dtoToSave)),
            1L);
        assertEquals(user.getCustomShoppingListItems().get(0), customShoppingListItem);
        assertEquals("bar", saveResult.get(0).getText());
    }

    @Test
    void saveDuplicatedBulkSaveCustomShoppingListItemDtoTest() {
        CustomShoppingListItemSaveRequestDto dtoToSave = new CustomShoppingListItemSaveRequestDto("foo");
        CustomShoppingListItem customShoppingListItem =
            new CustomShoppingListItem(1L, dtoToSave.getText(), null, null, null);
        user.setCustomShoppingListItems(Collections.singletonList(customShoppingListItem));
        UserVO userVO = ModelUtils.getUserVO();
        when(restClient.findById(1L)).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(dtoToSave, CustomShoppingListItem.class)).thenReturn(customShoppingListItem);
        BulkSaveCustomShoppingListItemDto bulkSave =
            new BulkSaveCustomShoppingListItemDto(Collections.singletonList(dtoToSave));
        Assertions.assertThrows(CustomShoppingListItemNotSavedException.class,
            () -> customShoppingListItemService.save(bulkSave, 1L));
    }

    @Test
    void findAllTest() {
        CustomShoppingListItem customShoppingListItem =
            new CustomShoppingListItem(1L, "foo", null, ShoppingListItemStatus.DONE, null);
        when(customShoppingListItemRepo.findAll()).thenReturn(Collections.singletonList(customShoppingListItem));
        when(modelMapper.map(customShoppingListItem, CustomShoppingListItemResponseDto.class))
            .thenReturn(new CustomShoppingListItemResponseDto(customShoppingListItem.getId(),
                customShoppingListItem.getText(), customShoppingListItem.getStatus()));
        List<CustomShoppingListItemResponseDto> findAllResult = customShoppingListItemService.findAll();
        assertEquals("foo", findAllResult.get(0).getText());
        assertEquals(1L, (long) findAllResult.get(0).getId());
    }

    @Test
    void findByNullIdTest() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customShoppingListItemService.findById(null));
    }

    @Test
    void findByIdTest() {
        CustomShoppingListItem customShoppingListItem =
            new CustomShoppingListItem(1L, "foo", null, ShoppingListItemStatus.DONE, null);
        when(customShoppingListItemRepo.findById(anyLong())).thenReturn(java.util.Optional.of(customShoppingListItem));
        when(modelMapper.map(customShoppingListItem, CustomShoppingListItemResponseDto.class))
            .thenReturn(new CustomShoppingListItemResponseDto(customShoppingListItem.getId(),
                customShoppingListItem.getText(), customShoppingListItem.getStatus()));
        CustomShoppingListItemResponseDto findByIdResult = customShoppingListItemService.findById(1L);
        assertEquals("foo", findByIdResult.getText());
        assertEquals(1L, (long) findByIdResult.getId());
    }

    @Test
    void updateItemStatus() {
        CustomShoppingListItem customShoppingListItem =
            new CustomShoppingListItem(1L, "test", null, ShoppingListItemStatus.DONE, null);
        CustomShoppingListItemResponseDto test =
            new CustomShoppingListItemResponseDto(1L, "test", ShoppingListItemStatus.DONE);
        when(customShoppingListItemRepo.findByUserIdAndItemId(64L, 1L)).thenReturn(customShoppingListItem);
        when(customShoppingListItemRepo.save(customShoppingListItem)).thenReturn(customShoppingListItem);
        when(modelMapper.map(customShoppingListItem, CustomShoppingListItemResponseDto.class)).thenReturn(test);
        assertEquals(test, customShoppingListItemService.updateItemStatus(64L, 1L, "DONE"));
    }

    @Test
    void findAllByUserWithNullIdTest() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customShoppingListItemService.findAllByUser(null));
    }

    @Test
    void findAllByUserWithNonExistentIdTest() {
        when(customShoppingListItemRepo.findAllByUserId(1L)).thenReturn(Collections.emptyList());
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customShoppingListItemService.findAllByUser(1L));
    }

    @Test
    void findAllByUserWithExistentIdTest() {
        CustomShoppingListItem customShoppingListItem =
            new CustomShoppingListItem(1L, "foo", user, ShoppingListItemStatus.DONE, null);
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto =
            new CustomShoppingListItemResponseDto(customShoppingListItem.getId(), customShoppingListItem.getText(),
                customShoppingListItem.getStatus());
        when(customShoppingListItemRepo.findAllByUserId(user.getId()))
            .thenReturn(Collections.singletonList(customShoppingListItem));
        when(modelMapper.map(customShoppingListItem, CustomShoppingListItemResponseDto.class))
            .thenReturn(customShoppingListItemResponseDto);
        List<CustomShoppingListItemResponseDto> findAllByUserResult =
            customShoppingListItemService.findAllByUser(user.getId());
        assertEquals(findAllByUserResult.get(0).getId(), customShoppingListItemResponseDto.getId());
        assertEquals(findAllByUserResult.get(0).getText(), customShoppingListItemResponseDto.getText());
    }

    @Test
    void bulkDeleteWithNonExistentIdTest() {
        doThrow(new EmptyResultDataAccessException(1)).when(customShoppingListItemRepo).deleteById(1L);
        Assertions
            .assertThrows(NotFoundException.class,
                () -> customShoppingListItemService.bulkDelete("1"));
    }

    @Test
    void bulkDeleteWithExistentIdTest() {
        doNothing().when(customShoppingListItemRepo).deleteById(anyLong());
        ArrayList<Long> expectedResult = new ArrayList<>();
        expectedResult.add(1L);
        expectedResult.add(2L);
        expectedResult.add(3L);
        List<Long> bulkDeleteResult = customShoppingListItemService.bulkDelete("1,2,3");
        assertEquals(expectedResult, bulkDeleteResult);
    }
}
