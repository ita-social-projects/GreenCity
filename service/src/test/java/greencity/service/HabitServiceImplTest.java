package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitTranslationRepo;
import greencity.repository.ShoppingListItemTranslationRepo;
import greencity.mapping.CustomHabitMapper;
import greencity.mapping.CustomShoppingListMapper;
import greencity.mapping.HabitTranslationMapper;
import greencity.repository.CustomShoppingListItemRepo;
import greencity.repository.LanguageRepo;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class HabitServiceImplTest {

    @InjectMocks
    private HabitServiceImpl habitService;

    @Mock
    private HabitRepo habitRepo;

    @Mock
    private HabitTranslationRepo habitTranslationRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CustomHabitMapper customHabitMapper;

    @Mock
    private HabitTranslationMapper habitTranslationMapper;

    @Mock
    private CustomShoppingListMapper customShoppingListMapper;

    @Mock
    private ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;
    @Mock
    private HabitAssignRepo habitAssignRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private TagsRepo tagsRepo;

    @Mock
    private LanguageRepo languageRepo;

    @Mock
    private CustomShoppingListItemRepo customShoppingListItemRepo;

    @Test()
    void getByIdAndLanguageCode() {
        Habit habit = ModelUtils.getHabit();
        HabitDto habitDto = ModelUtils.getHabitDto();
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitTranslationRepo.findByHabitAndLanguageCode(habit, "en"))
            .thenReturn(Optional.of(habitTranslation));
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        assertEquals(habitDto, habitService.getByIdAndLanguageCode(1L, "en"));
    }

    @Test
    void getByIdAndLanguageCodeNotFoundException() {
        assertThrows(NotFoundException.class, () -> habitService.getByIdAndLanguageCode(1L, "en"));
    }

    @Test
    void getByIdAndLanguageCodeHabitTranslationNotFoundException2() {
        Habit habit = ModelUtils.getHabit();
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        assertThrows(NotFoundException.class, () -> habitService.getByIdAndLanguageCode(1L, "en"));
    }

    @Test
    void getAllHabitsByLanguageCode() {
        Pageable pageable = PageRequest.of(0, 2);
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        Page<HabitTranslation> habitTranslationPage =
            new PageImpl<>(Collections.singletonList(habitTranslation), pageable, 10);
        HabitDto habitDto = ModelUtils.getHabitDto();
        when(habitTranslationRepo.findAllByLanguageCode(pageable, "en")).thenReturn(habitTranslationPage);
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        List<HabitDto> habitDtoList = Collections.singletonList(habitDto);
        PageableDto pageableDto = new PageableDto(habitDtoList, habitTranslationPage.getTotalElements(),
            habitTranslationPage.getPageable().getPageNumber(), habitTranslationPage.getTotalPages());
        assertEquals(pageableDto, habitService.getAllHabitsByLanguageCode(pageable, "en"));
    }

    @Test
    void getAllByTagsAndLanguageCode() {
        Pageable pageable = PageRequest.of(0, 2);
        String tag = "ECO_NEWS";
        List<String> tags = Collections.singletonList(tag);
        List<String> lowerCaseTags = Collections.singletonList(tag.toLowerCase());
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        HabitDto habitDto = ModelUtils.getHabitDto();
        Page<HabitTranslation> habitTranslationPage =
            new PageImpl<>(Collections.singletonList(habitTranslation), pageable, 10);
        List<HabitDto> habitDtoList = Collections.singletonList(habitDto);
        PageableDto pageableDto = new PageableDto(habitDtoList, habitTranslationPage.getTotalElements(),
            habitTranslationPage.getPageable().getPageNumber(), habitTranslationPage.getTotalPages());
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        when(habitTranslationRepo.findAllByTagsAndLanguageCode(pageable, lowerCaseTags, "en"))
            .thenReturn(habitTranslationPage);
        assertEquals(pageableDto, habitService.getAllByTagsAndLanguageCode(pageable, tags, "en"));
    }

    @Test
    void getShoppingListForHabit() {
        ShoppingListItemTranslation shoppingListItemTranslation = ModelUtils.getShoppingListItemTranslation();
        List<ShoppingListItemTranslation> shoppingListItemTranslations =
            Collections.singletonList(shoppingListItemTranslation);
        ShoppingListItemDto shoppingListItemDto = new ShoppingListItemDto(1L, "test", "ACTIVE");
        List<ShoppingListItemDto> shoppingListItemDtos = Collections.singletonList(shoppingListItemDto);
        when(modelMapper.map(shoppingListItemTranslation, ShoppingListItemDto.class)).thenReturn(shoppingListItemDto);
        when(shoppingListItemTranslationRepo.findShoppingListByHabitIdAndByLanguageCode("en", 1l))
            .thenReturn(shoppingListItemTranslations);
        assertEquals(shoppingListItemDtos, habitService.getShoppingListForHabit(1L, "en"));
    }

    @Test
    void addShoppingListItemToHabitTest() {
        doNothing().when(habitRepo).addShopingListItemToHabit(1L, 1L);
        habitService.addShoppingListItemToHabit(1L, 1L);
        verify(habitRepo).addShopingListItemToHabit(1L, 1L);
    }

    @Test
    void deleteShoppingListItemTest() {
        doNothing().when(habitRepo).upadateShopingListItemInHabit(1L, 1L);
        habitService.deleteShoppingListItem(1L, 1L);
        verify(habitRepo).upadateShopingListItemInHabit(1L, 1L);
    }

    @Test
    void addAllShoppingListItemToHabitTest() {
        List<Long> listID = Collections.singletonList(1L);
        doNothing().when(habitRepo).addShopingListItemToHabit(listID.get(0), 1L);
        habitService.addAllShoppingListItemsByListOfId(1L, listID);
        verify(habitRepo, times(1)).addShopingListItemToHabit(listID.get(0), 1L);
    }

    @Test
    void deleteAllShoppingListItemToHabitTest() {
        List<Long> listID = Collections.singletonList(1L);
        doNothing().when(habitRepo).addShopingListItemToHabit(listID.get(0), 1L);
        habitService.deleteAllShoppingListItemsByListOfId(1L, listID);
        verify(habitRepo, times(1)).upadateShopingListItemInHabit(listID.get(0), 1L);
    }

    @Test
    void addCustomHabitTest() {
        User user = ModelUtils.getUser();
        Tag tag = ModelUtils.getTagHabitForServiceTest();
        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        habit.setTags(Set.of(tag));
        habit.setUserId(1L);
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = ModelUtils.getAddCustomHabitDtoRequestForServiceTest();
        AddCustomHabitDtoResponse addCustomHabitDtoResponse = ModelUtils.getAddCustomHabitDtoResponse();

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest))).thenReturn(habit);
        when(tagsRepo.findTagsByNames(Set.of("Reusable"))).thenReturn(Set.of(tag));
        when(modelMapper.map(habit, AddCustomHabitDtoResponse.class)).thenReturn(addCustomHabitDtoResponse);

        var result = habitService.addCustomHabit(addCustomHabitDtoRequest, "taras@gmail.com");

        assertEquals(addCustomHabitDtoResponse, result);
        verify(habitRepo).save(customHabitMapper.convert(addCustomHabitDtoRequest));
        verify(customHabitMapper, times(3)).convert(addCustomHabitDtoRequest);
        verify(tagsRepo).findTagsByNames(Set.of("Reusable"));
        verify(modelMapper).map(habit, AddCustomHabitDtoResponse.class);
        verify(customShoppingListMapper).mapAllToList(anyList());
        verify(customShoppingListItemRepo).saveAll(anyList());
    }

    @Test
    void addCustomHabitNoSuchElementExceptionWithNotExistingLanguageCodeTest() {
        User user = ModelUtils.getUser();
        Tag tag = ModelUtils.getTagHabitForServiceTest();
        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        habit.setTags(Set.of(tag));
        habit.setUserId(1L);
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = ModelUtils.getAddCustomHabitDtoRequestForServiceTest();

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest))).thenReturn(habit);
        when(tagsRepo.findTagsByNames(Set.of("Reusable"))).thenReturn(Set.of(tag));
        when(habitTranslationMapper.mapAllToList(anyList())).thenReturn(List.of(ModelUtils.getHabitTranslation()));
        when(languageRepo.findByCode(anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
            () -> habitService.addCustomHabit(addCustomHabitDtoRequest, "taras@gmail.com"));

        verify(habitRepo).save(customHabitMapper.convert(addCustomHabitDtoRequest));
        verify(customHabitMapper, times(3)).convert(addCustomHabitDtoRequest);
        verify(tagsRepo).findTagsByNames(Set.of("Reusable"));
        verify(habitTranslationMapper).mapAllToList(addCustomHabitDtoRequest.getHabitTranslations());
        verify(languageRepo).findByCode(anyString());
    }

    @Test
    void addCustomHabitThrowUserNotFoundException() {
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = ModelUtils.getAddCustomHabitDtoRequestForServiceTest();
        when(userRepo.findByEmail("user@gmail.com")).thenReturn(Optional.empty());
        when(habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest))).thenReturn(nullable(Habit.class));

        assertThrows(WrongEmailException.class,
            () -> habitService.addCustomHabit(addCustomHabitDtoRequest, "user@gmail.com"));

        verify(userRepo).findByEmail("user@gmail.com");
        verify(customHabitMapper).convert(addCustomHabitDtoRequest);
    }
}
