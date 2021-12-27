package greencity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.shoppinglistitem.*;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.entity.*;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.EmailNotification;
import greencity.enums.ShoppingListItemStatus;
import greencity.enums.Role;
import static greencity.enums.UserStatus.ACTIVATED;
import greencity.exception.exceptions.*;
import greencity.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ShoppingListItemServiceImplTest {
    @Mock
    private ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    UserShoppingListItemRepo userShoppingListItemRepo;
    @Mock
    HabitAssignRepo habitAssignRepo;
    @Mock
    private ShoppingListItemRepo shoppingListItemRepo;
    @Mock
    private HabitRepo habitRepo;
    @Mock
    private CustomShoppingListItemRepo customShoppingListItemRepo;
    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private ShoppingListItemServiceImpl shoppingListItemService;
    @Mock
    private final ShoppingListItem shoppingListItem =
        ShoppingListItem.builder().id(1L).translations(ModelUtils.getShoppingListItemTranslations()).build();

    private final List<LanguageTranslationDTO> languageTranslationDTOS =
        Collections.singletonList(ModelUtils.getLanguageTranslationDTO());
    private final ShoppingListItemPostDto shoppingListItemPostDto =
        new ShoppingListItemPostDto(languageTranslationDTOS, new ShoppingListItemRequestDto(1L));

    private final HabitAssign habitAssign = ModelUtils.getHabitAssign();
    private User user = User.builder()
        .id(1L)
        .name("Test Testing")
        .email("test@gmail.com")
        .role(Role.ROLE_USER)
        .userStatus(ACTIVATED)
        .emailNotification(EmailNotification.DISABLED)
        .lastActivityTime(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .socialNetworks(new ArrayList<>())
        .build();

    private String language = "uk";

    private List<ShoppingListItemTranslation> shoppingListItemTranslations = Arrays.asList(
        ShoppingListItemTranslation.builder()
            .id(1L)
            .language(new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
            .content("TEST")
            .shoppingListItem(
                new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
            .build(),
        ShoppingListItemTranslation.builder()
            .id(2L)
            .language(new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList()))
            .content("TEST")
            .shoppingListItem(
                new ShoppingListItem(2L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
            .build());

    List<ShoppingListItemRequestDto> shoppingListItemRequestDtos =
        Arrays.asList(new ShoppingListItemRequestDto(1L), new ShoppingListItemRequestDto(2L),
            new ShoppingListItemRequestDto(3L));

    private Long userId = user.getId();

    @Test
    void saveUserShoppingListItemTest() {
        ObjectMapper mapper = new ObjectMapper();
        UserShoppingListItem userShoppingListItem =
            mapper.convertValue(shoppingListItemRequestDtos.get(0), UserShoppingListItem.class);
        when(habitAssignRepo.findByHabitIdAndUserId(1L, userId))
            .thenReturn(Optional.of(habitAssign));
        when(userShoppingListItemRepo.getShoppingListItemsIdForHabit(habitAssign.getHabit().getId()))
            .thenReturn(Collections.singletonList(1L));
        when(userShoppingListItemRepo.getAllAssignedShoppingListItems(habitAssign.getId()))
            .thenReturn(Collections.singletonList(2L));
        when(modelMapper.map(shoppingListItemRequestDtos.get(0), UserShoppingListItem.class))
            .thenReturn(userShoppingListItem);
        getUserShoppingListItemTest();
        userShoppingListItem.setHabitAssign(habitAssign);
        shoppingListItemService
            .saveUserShoppingListItems(userId, 1L, Collections.singletonList(shoppingListItemRequestDtos.get(0)), "en");
        verify(userShoppingListItemRepo).saveAll(Collections.singletonList(userShoppingListItem));
    }

    @Test
    void saveUserShoppingListItemThorowsNotFoundException() {
        when(habitAssignRepo.findByHabitIdAndUserId(1L, userId))
            .thenReturn(Optional.of(habitAssign));
        when(userShoppingListItemRepo.getShoppingListItemsIdForHabit(habitAssign.getHabit().getId()))
            .thenReturn(Collections.singletonList(1L));
        List<ShoppingListItemRequestDto> shoppingListItemRequestDto =
            Collections.singletonList(shoppingListItemRequestDtos.get(2));
        assertThrows(NotFoundException.class, () -> shoppingListItemService
            .saveUserShoppingListItems(userId, 1L, shoppingListItemRequestDto, "en"));
    }

    @Test
    void saveUserShoppingListItemThorowsWrongIdException() {
        when(habitAssignRepo.findByHabitIdAndUserId(1L, userId))
            .thenReturn(Optional.of(habitAssign));
        when(userShoppingListItemRepo.getShoppingListItemsIdForHabit(habitAssign.getHabit().getId()))
            .thenReturn(Collections.singletonList(1L));
        when(userShoppingListItemRepo.getAllAssignedShoppingListItems(habitAssign.getId()))
            .thenReturn(Collections.singletonList(1L));
        List<ShoppingListItemRequestDto> shoppingListItemRequestDto =
            Collections.singletonList(shoppingListItemRequestDtos.get(0));
        assertThrows(WrongIdException.class, () -> shoppingListItemService
            .saveUserShoppingListItems(userId, 1L, shoppingListItemRequestDto, "en"));
    }

    @Test
    void findAllTest() {
        List<ShoppingListItemTranslation> shoppingListItemTranslations = ModelUtils.getShoppingListItemTranslations();
        List<ShoppingListItemDto> shoppingListItemDto = shoppingListItemTranslations
            .stream()
            .map(translation -> new ShoppingListItemDto(translation.getShoppingListItem().getId(),
                translation.getContent(), ShoppingListItemStatus.ACTIVE.toString()))
            .collect(Collectors.toList());

        when(modelMapper.map(shoppingListItemTranslations.get(0), ShoppingListItemDto.class))
            .thenReturn(shoppingListItemDto.get(0));
        when(modelMapper.map(shoppingListItemTranslations.get(1), ShoppingListItemDto.class))
            .thenReturn(shoppingListItemDto.get(1));
        when(shoppingListItemTranslationRepo.findAllByLanguageCode(AppConstant.DEFAULT_LANGUAGE_CODE))
            .thenReturn(shoppingListItemTranslations);

        assertEquals(shoppingListItemService.findAll(AppConstant.DEFAULT_LANGUAGE_CODE), shoppingListItemDto);
    }

    @Test
    void saveShoppingListItemTest() {
        when((modelMapper.map(shoppingListItemPostDto, ShoppingListItem.class))).thenReturn(shoppingListItem);
        when(modelMapper.map(shoppingListItem.getTranslations(),
            new TypeToken<List<LanguageTranslationDTO>>() {
            }.getType())).thenReturn(languageTranslationDTOS);
        List<LanguageTranslationDTO> res = shoppingListItemService.saveShoppingListItem(shoppingListItemPostDto);
        assertEquals(languageTranslationDTOS.get(0).getContent(), res.get(0).getContent());
    }

    @Test
    void updateTest() {
        when(shoppingListItemRepo.findById(shoppingListItemPostDto.getShoppingListItem().getId()))
            .thenReturn(Optional.of(shoppingListItem));
        when(modelMapper.map(shoppingListItem.getTranslations(),
            new TypeToken<List<LanguageTranslationDTO>>() {
            }.getType())).thenReturn(languageTranslationDTOS);
        List<LanguageTranslationDTO> res = shoppingListItemService.update(shoppingListItemPostDto);
        assertEquals(languageTranslationDTOS.get(0).getContent(), res.get(0).getContent());
    }

    @Test
    void updateThrowsTest() {
        assertThrows(ShoppingListItemNotFoundException.class,
            () -> shoppingListItemService.update(shoppingListItemPostDto));
    }

    @Test
    void updateUserShoppingListItemStatusWithNonExistentItemIdTest() {
        assertThrows(NullPointerException.class, () -> shoppingListItemService
            .updateUserShopingListItemStatus(userId, 2L, "en"));
    }

    @Test
    void updateUserShoppingListItemStatusWithActiveItemStateTest() {
        UserShoppingListItem userShoppingListItem = ModelUtils.getPredefinedUserShoppingListItem();
        when(userShoppingListItemRepo.getOne(userShoppingListItem.getId())).thenReturn(userShoppingListItem);
        when(modelMapper.map(any(), eq(UserShoppingListItemResponseDto.class)))
            .thenReturn(new UserShoppingListItemResponseDto(2L, null, ShoppingListItemStatus.DONE));
        when(
            shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(language, userShoppingListItem.getId()))
                .thenReturn(shoppingListItemTranslations.get(0));
        UserShoppingListItemResponseDto userShoppingListItemResponseDto =
            shoppingListItemService.updateUserShopingListItemStatus(userId, userShoppingListItem.getId(), "uk");

        assertEquals(ShoppingListItemStatus.DONE, userShoppingListItem.getStatus());
        assertEquals(userShoppingListItemResponseDto.getId(),
            new UserShoppingListItemResponseDto(2L, shoppingListItemTranslations.get(0).getContent(),
                ShoppingListItemStatus.DONE).getId());
        verify(userShoppingListItemRepo).save(userShoppingListItem);
    }

    @Test
    void updateUserShoppingListItemStatusWithDoneItemStateTest() {
        UserShoppingListItem userShoppingListItem =
            new UserShoppingListItem(1L, null, null, ShoppingListItemStatus.DONE, null);
        when(userShoppingListItemRepo.getOne(userShoppingListItem.getId())).thenReturn(userShoppingListItem);
        Long userId = user.getId();
        Long userShoppingListItemId = userShoppingListItem.getId();
        assertThrows(UserShoppingListItemStatusNotUpdatedException.class,
            () -> shoppingListItemService.updateUserShopingListItemStatus(userId, userShoppingListItemId, "en"));
        assertNotEquals(ShoppingListItemStatus.ACTIVE, userShoppingListItem.getStatus());
    }

    @Test
    void updateUserShoppingListItemStatus() {
        String status = "DONE";
        UserShoppingListItem userShoppingListItem = ModelUtils.getPredefinedUserShoppingListItem();
        when(userShoppingListItemRepo.getAllByShoppingListItemIdANdUserId(1L, 2L))
            .thenReturn(List.of(userShoppingListItem));
        when(modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class))
            .thenReturn(UserShoppingListItemResponseDto.builder()
                .id(1L)
                .status(ShoppingListItemStatus.DONE)
                .build());
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId("en", 1L))
            .thenReturn(ModelUtils.getShoppingListItemTranslation());

        List<UserShoppingListItemResponseDto> result = shoppingListItemService
            .updateUserShoppingListItemStatus(2L, 1L, "en", "DONE");

        assertEquals(ShoppingListItemStatus.DONE, result.get(0).getStatus());
    }

    @Test
    void updateUserShoppingListItemStatusShouldThrowNotFound() {
        when(userShoppingListItemRepo.getAllByShoppingListItemIdANdUserId(1L, 2L))
            .thenReturn(null);

        Exception thrown = assertThrows(NotFoundException.class, () -> shoppingListItemService
            .updateUserShoppingListItemStatus(2L, 1L, "en", "DONE"));

        assertEquals(ErrorMessage.USER_SHOPPING_LIST_ITEM_NOT_FOUND_BY_USER_ID, thrown.getMessage());
    }

    @Test
    void updateUserShoppingListItemStatusShouldThrowBadRequest() {
        when(userShoppingListItemRepo.getAllByShoppingListItemIdANdUserId(1L, 2L))
            .thenReturn(List.of(ModelUtils.getPredefinedUserShoppingListItem()));

        Exception thrown = assertThrows(BadRequestException.class, () -> shoppingListItemService
            .updateUserShoppingListItemStatus(2L, 1L, "en", "Wrong Status"));

        assertEquals(ErrorMessage.INCORRECT_INPUT_ITEM_STATUS, thrown.getMessage());
    }

    @Test
    void deleteTest() {
        shoppingListItemService.delete(1L);
        verify(shoppingListItemRepo).deleteById(1L);
    }

    @Test
    void deleteTestFailed() {
        doThrow(EmptyResultDataAccessException.class).when(shoppingListItemRepo).deleteById(300000L);

        assertThrows(NotDeletedException.class, () -> shoppingListItemService.delete(300000L));
    }

    @Test
    void findShoppingListItemByIdTest() {
        Optional<ShoppingListItem> object = Optional.of(shoppingListItem);
        when(shoppingListItemRepo.findById(anyLong())).thenReturn(object);
        when(modelMapper.map(object.get(), ShoppingListItemResponseDto.class))
            .thenReturn(new ShoppingListItemResponseDto());

        assertNotNull(shoppingListItemService.findShoppingListItemById(30L));
    }

    @Test
    void findShoppingListItemByIdTestFailed() {
        Optional<ShoppingListItem> object = null;

        assertThrows(ShoppingListItemNotFoundException.class,
            () -> shoppingListItemService.findShoppingListItemById(30L));
    }

    @Test
    void getAllFactsOfTheDay() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<ShoppingListItem> shoppingListItems = Collections.singletonList(shoppingListItem);
        Page<ShoppingListItem> page = new PageImpl<>(shoppingListItems, pageable, shoppingListItems.size());

        List<ShoppingListItemManagementDto> dtoList = Collections.singletonList(
            shoppingListItems.stream().map(g -> (ShoppingListItemManagementDto.builder().id(g.getId())).build())
                .findFirst().get());
        PageableAdvancedDto<ShoppingListItemManagementDto> expected = new PageableAdvancedDto<>(dtoList, dtoList.size(),
            0, 1, 0, false, false, true, true);

        when(shoppingListItemRepo.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(shoppingListItems.get(0), ShoppingListItemManagementDto.class)).thenReturn(dtoList.get(0));

        PageableAdvancedDto<ShoppingListItemManagementDto> actual =
            shoppingListItemService.findShoppingListItemsForManagementByPage(pageable);

        assertEquals(expected, actual);
    }

    @Test
    void searchBy() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<ShoppingListItem> shoppingListItems = Collections.singletonList(shoppingListItem);
        Page<ShoppingListItem> page = new PageImpl<>(shoppingListItems, pageable, shoppingListItems.size());

        List<ShoppingListItemManagementDto> dtoList = Collections.singletonList(
            shoppingListItems.stream().map(g -> (ShoppingListItemManagementDto.builder().id(g.getId())).build())
                .findFirst().get());
        PageableAdvancedDto<ShoppingListItemManagementDto> expected = new PageableAdvancedDto<>(dtoList, dtoList.size(),
            0, 1, 0, false, false, true, true);

        when(shoppingListItemRepo.searchBy(pageable, "uk")).thenReturn(page);
        when(modelMapper.map(shoppingListItems.get(0), ShoppingListItemManagementDto.class)).thenReturn(dtoList.get(0));

        PageableAdvancedDto<ShoppingListItemManagementDto> actual = shoppingListItemService.searchBy(pageable, "uk");

        assertEquals(expected, actual);
    }

    @Test
    void deleteAllShoppingListItemByListOfId() {
        List<Long> idsToBeDeleted = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);

        shoppingListItemService.deleteAllShoppingListItemsByListOfId(idsToBeDeleted);
        verify(shoppingListItemRepo, times(6)).deleteById(anyLong());
    }

    @Test
    void deleteUserShoppingListItems() {
        String ids = "1,2,3,4,5,6";
        List<Long> expected = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);
        UserShoppingListItem userShoppingListItem =
            new UserShoppingListItem(1L, null, shoppingListItem, ShoppingListItemStatus.ACTIVE, null);

        when(userShoppingListItemRepo.findById(anyLong())).thenReturn(Optional.of(userShoppingListItem));

        assertEquals(expected, shoppingListItemService.deleteUserShoppingListItems(ids));
        verify(userShoppingListItemRepo, times(6)).delete(userShoppingListItem);
    }

    @Test
    void deleteUserShoppingListItemsFailed() {
        String ids = "1,2,3,4,5,6";
        UserShoppingListItem userShoppingListItem =
            new UserShoppingListItem(1L, null, shoppingListItem, ShoppingListItemStatus.ACTIVE, null);

        when(userShoppingListItemRepo.findById(anyLong())).thenReturn(Optional.of(userShoppingListItem));
        when(userShoppingListItemRepo.findById(3L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> shoppingListItemService.deleteUserShoppingListItems(ids));
    }

    @Test
    void getUserShoppingListItemTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        UserShoppingListItem userShoppingListItem =
            UserShoppingListItem.builder().id(1L).status(ShoppingListItemStatus.ACTIVE).build();
        when(habitAssignRepo.findByHabitIdAndUserId(userId, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(userShoppingListItemRepo.findAllByHabitAssingId(habitAssign.getId())).thenReturn(Collections.singletonList(
            userShoppingListItem));
        when(modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class))
            .thenReturn(UserShoppingListItemResponseDto.builder().id(1L).build());
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId("en", 1L))
            .thenReturn(ShoppingListItemTranslation.builder().id(1L).build());
        assertEquals(1L, shoppingListItemService.getUserShoppingList(userId, 1L, "en").get(0).getId());
    }

    @Test
    void getUserShoppingListItemsTestTrows() {
        when(habitAssignRepo.findByHabitIdAndUserId(userId, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(userShoppingListItemRepo.findAllByHabitAssingId(habitAssign.getId())).thenReturn(Collections.emptyList());
        assertThrows(
            UserHasNoShoppingListItemsException.class,
            () -> shoppingListItemService.getUserShoppingList(userId, 1L, "en"));
    }

    @Test
    void deleteUserShoppingListItemByItemIdAndUserIdAndHabitIdTest() {
        when(habitAssignRepo.findByHabitIdAndUserId(1L, userId))
            .thenReturn(Optional.of(habitAssign));
        shoppingListItemService.deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(1L, userId, 1L);
        verify(userShoppingListItemRepo).deleteByShoppingListItemIdAndHabitAssignId(1L, 1L);
    }

    @Test
    void deleteUserShollingListItemByItemIdAndUserIdAndHabitIdTestThorows() {
        assertThrows(NotFoundException.class,
            () -> shoppingListItemService.deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(1L, userId, 1L));
    }

    @Test
    void getUserShoppingListtemsIfThereAreNoItems() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        when(habitAssignRepo.findByHabitIdAndUserId(userId, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(userShoppingListItemRepo.findAllByHabitAssingId(habitAssign.getId())).thenReturn(Collections.emptyList());

        assertThrows(
            UserHasNoShoppingListItemsException.class,
            () -> shoppingListItemService.getUserShoppingList(userId, 1L, "en"));
    }

    @Test
    void getShoppingListItemByHabitIdTest() {
        List<Long> listID = Collections.singletonList(1L);
        ShoppingListItem shoppingListItem = ModelUtils.getUserShoppingListItem().getShoppingListItem();
        List<ShoppingListItem> shoppingListItemList = Collections.singletonList(shoppingListItem);
        ShoppingListItemManagementDto shoppingListItemManagementDto = ShoppingListItemManagementDto.builder()
            .id(1L)
            .build();
        List<ShoppingListItemManagementDto> shoppingListItemManagementDtos =
            Collections.singletonList(shoppingListItemManagementDto);

        when(shoppingListItemRepo.getAllShoppingListItemIdByHabitIdISContained(1L)).thenReturn(listID);
        when(shoppingListItemRepo.getShoppingListByListOfId(listID)).thenReturn(shoppingListItemList);
        when(modelMapper.map(shoppingListItem, ShoppingListItemManagementDto.class)).thenReturn(
            shoppingListItemManagementDto);
        assertEquals(shoppingListItemManagementDtos, shoppingListItemService.getShoppingListByHabitId(1L));

    }

    @Test
    void findAllShoppingListItemForManagementPageNotContainedTest() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        ShoppingListItem shoppingListItem = ModelUtils.getUserShoppingListItem().getShoppingListItem();
        List<ShoppingListItem> shoppingListItemList = Collections.singletonList(shoppingListItem);
        List<Long> listID = Collections.singletonList(1L);
        Page<ShoppingListItem> page = new PageImpl<>(shoppingListItemList, pageable, shoppingListItemList.size());
        ShoppingListItemManagementDto shoppingListItemManagementDto = ShoppingListItemManagementDto.builder()
            .id(1L)
            .build();
        List<ShoppingListItemManagementDto> dtoList = Collections.singletonList(shoppingListItemManagementDto);
        PageableAdvancedDto<ShoppingListItemManagementDto> expected = new PageableAdvancedDto<>(dtoList, dtoList.size(),
            0, 1, 0, false, false, true, true);

        when(shoppingListItemRepo.getAllShoppingListItemsByHabitIdNotContained(1L)).thenReturn(listID);
        when(shoppingListItemRepo.getShoppingListByListOfIdPageable(listID, pageable)).thenReturn(page);
        when(modelMapper.map(shoppingListItem, ShoppingListItemManagementDto.class)).thenReturn(
            shoppingListItemManagementDto);
        PageableAdvancedDto<ShoppingListItemManagementDto> actual =
            shoppingListItemService.findAllShoppingListItemsForManagementPageNotContained(1L, pageable);
        assertEquals(expected, actual);
    }

    @Test
    void addNewCustomShoppingItemTest() {
        when(userRepo.getOne(any())).thenReturn(ModelUtils.getUser());
        CustomShoppingListItem customShoppingListItem = CustomShoppingListItem.builder()
            .text("Description")
            .habit(ModelUtils.getHabitAssign().getHabit())
            .status(ShoppingListItemStatus.INPROGRESS)
            .user(ModelUtils.getUser())
            .build();
        when(customShoppingListItemRepo.save(any())).thenReturn(customShoppingListItem);
        when(habitAssignRepo.getOne(any())).thenReturn(ModelUtils.getHabitAssign());

        NewShoppingListItemRequestDto newShoppingListItemRequestDto = new NewShoppingListItemRequestDto();
        newShoppingListItemRequestDto.setItemDescription("Description");
        newShoppingListItemRequestDto.setHabitAssignId(1L);

        shoppingListItemService.addNewCustomShoppingItem(1L, newShoppingListItemRequestDto);
    }

    @Test
    void getCustomShoppingItemsTest() {
        CustomShoppingListItem customShoppingListItem = CustomShoppingListItem.builder()
            .text("Description")
            .habit(ModelUtils.getHabit())
            .status(ShoppingListItemStatus.INPROGRESS)
            .user(ModelUtils.getUser())
            .build();
        List<CustomShoppingListItem> customShoppingListItems = List.of(customShoppingListItem);

        when(customShoppingListItemRepo.findAllByUserIdAndHabitId(1L, 1L)).thenReturn(customShoppingListItems);

        assertTrue(shoppingListItemService.getCustomShoppingItems(1L, 1L)
            .contains(modelMapper.map(customShoppingListItem, CustomShoppingListItemResponseDto.class)));
    }
}
