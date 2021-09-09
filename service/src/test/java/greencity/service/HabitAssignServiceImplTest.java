package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.HabitAssignStatus;
import greencity.exception.exceptions.InvalidStatusException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitAssignServiceImplTest {
    @Mock
    HabitRepo habitRepo;
    @Mock
    HabitAssignRepo habitAssignRepo;
    @Mock
    ShoppingListItemRepo shoppingListItemRepo;
    @Mock
    UserShoppingListItemRepo userShoppingListItemRepo;
    @Mock
    ModelMapper modelMapper;
    @Mock
    HabitStatisticService habitStatisticService;
    @Mock
    ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;
    @Mock
    HabitAssignServiceImpl habitAssignService;

    private ZonedDateTime zonedDateTime = ZonedDateTime.now();

    private HabitDto habitDto = HabitDto.builder().id(1L).build();

    private HabitAssignDto habitAssignDto = HabitAssignDto.builder().id(1L)
        .createDateTime(zonedDateTime).habit(habitDto).build();

    private Habit habit = ModelUtils.getHabit();

    private HabitAssignManagementDto habitAssignManagementDto = HabitAssignManagementDto.builder()
        .id(1L)
        .createDateTime(zonedDateTime).habitId(habit.getId()).build();

    private HabitVO habitVO =
        HabitVO.builder().id(1L).image("src/main/resources/static/css/background-image-footer.svg").build();

    private UserVO userVO = UserVO.builder().id(1L).build();

    private User user = User.builder().id(1L).build();

    private HabitAssign habitAssign = getHabitAssign();

    private HabitAssign fullHabitAssign = getFullHabitAssign();

    private HabitAssignStatDto habitAssignStatDto = HabitAssignStatDto.builder()
        .status(HabitAssignStatus.ACQUIRED).build();

    private List<HabitAssignDto> habitAssignDtos = Collections.singletonList(habitAssignDto);

    private List<HabitAssign> habitAssigns = Collections.singletonList(habitAssign);

    private List<HabitAssign> fullHabitAssigns = Collections.singletonList(fullHabitAssign);

    private HabitAssignPropertiesDto habitAssignPropertiesDto = HabitAssignPropertiesDto.builder().duration(14).build();

    private String language = "en";

    @Test
    void getAllHabitAssignsByUserIdAndCancelledStatus() {
        when(habitAssignService.getAllHabitAssignsByUserIdAndCancelledStatus(1L, "en")).thenReturn(habitAssignDtos);
        List<HabitAssignDto> actual = habitAssignService.getAllHabitAssignsByUserIdAndCancelledStatus(1L, "en");
        assertEquals(habitAssignDtos, actual);
    }

    @Test
    void updateStatusByHabitIdAndUserId() {
        when(habitAssignService.updateStatusByHabitIdAndUserId(1L, 1L, habitAssignStatDto))
            .thenReturn(habitAssignManagementDto);

        assertEquals(habitAssignManagementDto,
            habitAssignService.updateStatusByHabitIdAndUserId(1L, 1L, habitAssignStatDto));
    }

    @Test
    void unenrollHabit() {
        LocalDate enrollDate = LocalDate.now();
        when(habitAssignService.unenrollHabit(1L, 1L, enrollDate)).thenReturn(ModelUtils.getHabitAssignDto());

        HabitAssignDto expected = ModelUtils.getHabitAssignDto();
        HabitAssignDto actual = habitAssignService.unenrollHabit(1L, 1L, enrollDate);
        expected.setCreateDateTime(actual.getCreateDateTime());

        assertEquals(expected, actual);
    }

    @Test
    void cancelHabitAssign() {
        habitAssign.setStatus(HabitAssignStatus.INPROGRESS);
        habitAssignDto.setStatus(HabitAssignStatus.CANCELLED);

        when(habitAssignService.cancelHabitAssign(1L, 1L)).thenReturn(habitAssignDto);

        assertEquals(habitAssignDto, habitAssignService.cancelHabitAssign(1L, 1L));
    }

    @Test
    void getAllHabitAssignsByHabitIdAndStatusNotCancelled() {
        Long habitId = 1L;

        Language language = ModelUtils.getLanguage();
        language.setCode("en");

        HabitTranslation translation = ModelUtils.getHabitTranslation();
        translation.setLanguage(language);

        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitAssignDto habitAssignDto = ModelUtils.getHabitAssignDto();

        habitAssign.setHabit(habit);

        habit.setHabitTranslations(Collections.singletonList(translation));

        when(habitAssignService.getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, language.getCode()))
            .thenReturn(Collections.singletonList(habitAssignDto));

        HabitAssignDto actual =
            habitAssignService.getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, language.getCode()).get(0);

        assertEquals(habitAssignDto, actual);

    }

    @Test
    void getNumberHabitAssignsByHabitIdAndStatusTest() {
        Long habitId = 1L;

        Language language = ModelUtils.getLanguage();
        language.setCode("en");

        HabitTranslation translation = ModelUtils.getHabitTranslation();
        translation.setLanguage(language);

        List<HabitAssign> habitAssignList = Collections.singletonList(ModelUtils.getHabitAssign());

        when(habitAssignService.getNumberHabitAssignsByHabitIdAndStatus(habitId, HabitAssignStatus.ACQUIRED))
            .thenReturn(1L);

        Long actual = habitAssignService.getNumberHabitAssignsByHabitIdAndStatus(habitId, HabitAssignStatus.ACQUIRED);

        assertEquals(habitAssignList.size(), actual);

    }

    @Test
    void deleteAllHabitAssignsByHabit() {
        HabitVO habit = ModelUtils.getHabitVO();
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();

        habitAssignService.deleteAllHabitAssignsByHabit(habit);
        habitStatisticService.deleteAllStatsByHabitAssign(habitAssignVO);

        verify(habitStatisticService).deleteAllStatsByHabitAssign(habitAssignVO);
    }

    @Test
    void enrollHabit() {
        HabitTranslation translation = ModelUtils.getHabitTranslation();

        habitAssign.setHabit(habit);
        habitAssign.getHabit().setHabitTranslations(Collections.singletonList(translation));
        when(habitAssignService.enrollHabit(1L, 1L, LocalDate.now(), "en")).thenReturn(habitAssignDto);

        HabitAssignDto actualDto = habitAssignService.enrollHabit(1L, 1L, LocalDate.now(), "en");

        assertEquals(habitAssignDto, actualDto);
    }

    @Test
    void enrollHabitThrowException() {
        LocalDate d = LocalDate.now();
        when(habitAssignService.enrollHabit(1L, 1L, d, "en")).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> habitAssignService.enrollHabit(1L, 1L, d, "en"));
    }

    @Test
    void findInprogressHabitAssignsOnDate() {

        Long id = 3L;
        LocalDate date = LocalDate.now();
        Language language = ModelUtils.getLanguage();
        language.setCode("en");

        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        habitAssign.getHabit().setHabitTranslations(Collections.singletonList(habitTranslation));

        when(habitAssignService.findInprogressHabitAssignsOnDate(id, date, language.getCode()))
            .thenReturn(Collections.singletonList(habitAssignDto));

        List<HabitAssignDto> dtoList =
            habitAssignService.findInprogressHabitAssignsOnDate(id, date, language.getCode());
        assertEquals(dtoList.get(0), habitAssignDto);
    }

    @Test
    void findInprogressHabitAssignsOnDateContent() {

        Long id = 3L;
        LocalDate date = LocalDate.now();
        Language language = ModelUtils.getLanguage();
        language.setCode("en");

        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        habitAssign.getHabit().setHabitTranslations(Collections.singletonList(habitTranslation));

        when(habitAssignService.findInprogressHabitAssignsOnDateContent(id, date, language.getCode()))
            .thenReturn(Collections.singletonList(habitAssignDto));

        List<HabitAssignDto> dtoList =
            habitAssignService.findInprogressHabitAssignsOnDateContent(id, date, language.getCode());
        assertEquals(dtoList.get(0), habitAssignDto);

    }

    @Test
    void addDefaultHabitIf() {

        UserVO userVo = ModelUtils.createUserVO2();
        Habit habit = new Habit();
        habit.setId(1L);

        habitAssignService.addDefaultHabit(userVo, "eu");
        habitRepo.findById(1L);
        verify(habitRepo).findById(1L);
    }

    @Test
    void updateUserShoppingList() {
        HabitAssign habitAssign = getHabitAssign();
        habitAssign.setDuration(20);

        when(habitAssignService.updateUserShoppingItemListAndDuration(1L, 21L, getHabitAssignPropertiesDto()))
            .thenReturn(getHabitAssignUserShoppingListItemDto());

        HabitAssignUserShoppingListItemDto result =
            habitAssignService.updateUserShoppingItemListAndDuration(1L, 21L, getHabitAssignPropertiesDto());
        assertEquals(20, result.getDuration());
        assertEquals(1, result.getUserShoppingListItemsDto().size());
    }

    @Test
    void updateUserShoppingListShouldThrowNotFoundException() {
        when(habitAssignService.updateUserShoppingItemListAndDuration(1L, 21L,
            getHabitAssignPropertiesDto())).thenThrow(new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + 1L));

        Exception thrown1 = assertThrows(NotFoundException.class,
            () -> habitAssignService.updateUserShoppingItemListAndDuration(1L, 21L,
                getHabitAssignPropertiesDto()));
        assertEquals(ErrorMessage.HABIT_NOT_FOUND_BY_ID + 1L, thrown1.getMessage());
    }

    @Test
    void updateUserShoppingListShouldThrowInvalidStatusException() {
        when(habitAssignService.updateUserShoppingItemListAndDuration(1L, 21L,
            getHabitAssignPropertiesDto()))
                .thenThrow(new InvalidStatusException(ErrorMessage.HABIT_ASSIGN_STATUS_IS_NOT_INPROGRESS));

        Exception thrown1 = assertThrows(InvalidStatusException.class,
            () -> habitAssignService.updateUserShoppingItemListAndDuration(1L, 21L,
                getHabitAssignPropertiesDto()));
        assertEquals(thrown1.getMessage(), ErrorMessage.HABIT_ASSIGN_STATUS_IS_NOT_INPROGRESS);
    }

    @Test
    void updateUserShoppingListItem() {
        UserShoppingListItem userShoppingListItem = getUserShoppingListItem();
        when(userShoppingListItemRepo.saveAll(any())).thenReturn(List.of(userShoppingListItem));

        habitAssignService.updateUserShoppingListItem(getUpdateUserShoppingListDto());

        userShoppingListItemRepo.saveAll(any());
        verify(userShoppingListItemRepo).saveAll(any());
    }

    @Test
    void getReadinessPercent() {
        habitAssignDto.setWorkingDays(30);
        habitAssignDto.setDuration(2);
        when(habitAssignService.getReadinessPercent(habitAssignDto)).thenReturn(1500);

        assertEquals(1500, habitAssignService.getReadinessPercent(habitAssignDto));
    }
}