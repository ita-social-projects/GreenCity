package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.habit.*;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.HabitAssignStatus;
import greencity.exception.exceptions.*;
import greencity.repository.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitAssignServiceImplTest {
    @Mock
    HabitRepo habitRepo;
    @Mock
    HabitAssignRepo habitAssignRepo;
    @Mock
    HabitAssignService mock;
    @Mock
    ShoppingListItemRepo shoppingListItemRepo;
    @Mock
    UserShoppingListItemRepo userShoppingListItemRepo;
    @Mock
    private HabitStatusCalendarService habitStatusCalendarService;
    @Mock
    ModelMapper modelMapper;
    @Mock
    HabitStatisticService habitStatisticService;
    @Mock
    ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;
    @InjectMocks
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

    private HabitAssign habitAssignNew = HabitAssign.builder()
        .user(user).habit(habit).build();

    private HabitAssignStatDto habitAssignStatDto = HabitAssignStatDto.builder()
        .status(HabitAssignStatus.ACQUIRED).build();

    private List<HabitAssignDto> habitAssignDtos = Collections.singletonList(habitAssignDto);

    private List<HabitAssign> habitAssigns = Collections.singletonList(habitAssign);

    private List<HabitAssign> fullHabitAssigns = Collections.singletonList(fullHabitAssign);

    private HabitAssignPropertiesDto habitAssignPropertiesDto = HabitAssignPropertiesDto.builder().duration(14).defaultShoppingListItems(List.of(1L)).build();

    private String language = "en";

    @Test
    void getById() {
        when(habitAssignRepo.findById(1L)).thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        assertEquals(habitAssignDto, habitAssignService.getById(1L, language));
    }

    @Test
    void getByIdFailed() {
        assertThrows(NotFoundException.class, () -> habitAssignService.getById(1L, language));
    }

    @Test
    void assignDefaultHabitForUser() {
        when(habitAssignRepo.findAllByUserId(userVO.getId())).thenReturn(List.of(habitAssign));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(habitAssignRepo.save(any())).thenReturn(habitAssign);
        when(modelMapper.map(habitAssign, HabitAssignManagementDto.class)).thenReturn(habitAssignManagementDto);
        HabitAssignManagementDto actual = habitAssignService.assignDefaultHabitForUser(habit.getId(), userVO);
        assertEquals(habitAssignManagementDto, actual);
    }

    @Test
    void assignDefaultHabitForUserThatWasCancelled() {
        habitAssign.setStatus(HabitAssignStatus.CANCELLED);
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(habitAssignRepo.findByHabitIdAndUserIdAndStatusIsCancelled(habit.getId(), user.getId()))
            .thenReturn(habitAssign);
        when(modelMapper.map(habitAssign, HabitAssignManagementDto.class)).thenReturn(habitAssignManagementDto);
        HabitAssignManagementDto actual = habitAssignService.assignDefaultHabitForUser(habit.getId(), userVO);
        assertEquals(habitAssignManagementDto, actual);
    }

    @Test
    void assignDefaultHabitForUserAlreadyHasTheHabit() {
        when(habitAssignRepo.findAllByUserId(userVO.getId())).thenReturn(List.of(HABIT_ASSIGN_IN_PROGRESS));

        assertThrows(UserAlreadyHasHabitAssignedException.class,
            () -> habitAssignService.assignDefaultHabitForUser(1L, userVO));
    }

    @Test
    void assignDefaultHabitForUserAlreadyHasMaxQTYHabits() {
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(habitAssignRepo.countHabitAssignsByUserIdAndAcquiredFalseAndCancelledFalse(
            user.getId()))
                .thenReturn(10);
        assertThrows(UserAlreadyHasMaxNumberOfActiveHabitAssigns.class,
            () -> habitAssignService.assignDefaultHabitForUser(1L, userVO));
    }

    @Test
    void assignDefaultHabitForUserAlreadyHasAssignedForCurrentDay() {
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(habitAssignRepo.findByHabitIdAndUserIdAndCreateDate(any(), any(), any()))
            .thenReturn(Optional.of(habitAssign));
        assertThrows(UserAlreadyHasHabitAssignedException.class,
            () -> habitAssignService.assignDefaultHabitForUser(1L, userVO));
    }

    @Test
    void assignCustomHabitForUserThatWasCancelled() {
        habitAssign.setStatus(HabitAssignStatus.CANCELLED);
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(habitAssignRepo.findByHabitIdAndUserIdAndStatusIsCancelled(habit.getId(), user.getId()))
            .thenReturn(habitAssign);
        when(modelMapper.map(habitAssign, HabitAssignManagementDto.class)).thenReturn(habitAssignManagementDto);
        HabitAssignManagementDto actual =
            habitAssignService.assignCustomHabitForUser(habit.getId(), userVO, habitAssignPropertiesDto);
        assertEquals(habitAssignManagementDto, actual);
    }

    @Test
    void findHabitAssignsBetweenDates() {
        HabitAssign habit1 = ModelUtils.getHabitAssign();
        HabitAssign habit2 = ModelUtils.getHabitAssign();
        habit2.setId(2L);
        habit2.getHabit().setId(2L);
        habit1.setDuration(3);
        habit2.setDuration(3);
        ZonedDateTime creation = ZonedDateTime.of(2020, 12, 28,
            12, 12, 12, 12, ZoneId.of("Europe/Kiev"));
        habit1.setCreateDate(creation);
        habit2.setCreateDate(creation);
        habit1.setHabitStatusCalendars(Collections.singletonList(HabitStatusCalendar
            .builder().enrollDate(LocalDate.of(2020, 12, 28)).build()));
        habit2.setHabitStatusCalendars(Collections.emptyList());
        List<HabitAssign> habitAssignList = Arrays.asList(habit1, habit2);
        List<HabitsDateEnrollmentDto> dtos = Arrays.asList(
            HabitsDateEnrollmentDto.builder().enrollDate(LocalDate.of(2020, 12, 27))
                .habitAssigns(Collections.emptyList()).build(),
            HabitsDateEnrollmentDto.builder().enrollDate(LocalDate.of(2020, 12, 28))
                .habitAssigns(Arrays.asList(
                    new HabitEnrollDto(1L, "", "", true),
                    new HabitEnrollDto(2L, "", "", false)))
                .build(),
            HabitsDateEnrollmentDto.builder().enrollDate(LocalDate.of(2020, 12, 29))
                .habitAssigns(Arrays.asList(
                    new HabitEnrollDto(1L, "", "", false),
                    new HabitEnrollDto(2L, "", "", false)))
                .build());

        when(habitAssignRepo.findAllHabitAssignsBetweenDates(anyLong(),
            eq(LocalDate.of(2020, 12, 27)), eq(LocalDate.of(2020, 12, 29))))
                .thenReturn(habitAssignList);

        assertEquals(dtos, habitAssignService.findHabitAssignsBetweenDates(13L,
            LocalDate.of(2020, 12, 27), LocalDate.of(2020, 12, 29),
            "en"));
    }

    @Test
    void assignCustomHabitForUser() {
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(habitAssignRepo.save(any())).thenReturn(habitAssign);
        when(modelMapper.map(habitAssign, HabitAssignManagementDto.class)).thenReturn(habitAssignManagementDto);
        HabitAssignManagementDto actual = habitAssignService.assignCustomHabitForUser(habit.getId(), userVO,
            habitAssignPropertiesDto);
        assertEquals(habitAssignManagementDto, actual);
    }

    @Test
    void findHabitAssignByUserIdAndHabitId() {
        when(habitAssignRepo.findByHabitIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssign,
            HabitAssignDto.class)).thenReturn(habitAssignDto);

        assertEquals(habitAssignDto, habitAssignService.findHabitAssignByUserIdAndHabitId(1L, 1L, "en"));
    }

    @Test
    void getAllHabitAssignsByUserIdAndStatusNotCancelled() {
        when(habitAssignRepo.findAllByUserId(1L)).thenReturn(habitAssigns);
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        List<HabitAssignDto> actual = habitAssignService.getAllHabitAssignsByUserIdAndStatusNotCancelled(1L, "en");
        assertEquals(habitAssignDtos, actual);
    }

    @Test
    void getAllHabitAssignsByUserIdAndStatusAcquired() {
        List<ShoppingListItemTranslation> list = getShoppingListItemTranslationList();
        when(habitAssignRepo.findAllByUserIdAndStatusAcquired(1L)).thenReturn(fullHabitAssigns);
        when(modelMapper.map(fullHabitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        when(shoppingListItemTranslationRepo.findShoppingListByHabitIdAndByLanguageCode("en", 1L))
            .thenReturn(list);
        List<HabitAssignDto> actual = habitAssignService.getAllHabitAssignsByUserIdAndStatusAcquired(1L, "en");
        assertEquals(habitAssignDtos, actual);
    }

    @Test
    void getAllHabitAssignsByUserIdAndStatusAcquiredEmptyHabitAssign() {
        List<ShoppingListItemTranslation> list = getShoppingListItemTranslationList();
        when(habitAssignRepo.findAllByUserIdAndStatusAcquired(1L)).thenReturn(habitAssigns);
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        when(shoppingListItemTranslationRepo.findShoppingListByHabitIdAndByLanguageCode("en", 1L))
            .thenReturn(list);
        List<HabitAssignDto> actual = habitAssignService.getAllHabitAssignsByUserIdAndStatusAcquired(1L, "en");
        assertEquals(habitAssignDtos, actual);
    }

    @Test
    void getAllHabitAssignsByUserIdAndCancelledStatus() {
        when(habitAssignRepo.findAllByUserIdAndStatusIsCancelled(1L)).thenReturn(habitAssigns);
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        List<HabitAssignDto> actual = habitAssignService.getAllHabitAssignsByUserIdAndCancelledStatus(1L, "en");
        assertEquals(habitAssignDtos, actual);
    }

    @Test
    void updateStatusByHabitIdAndUserId() {
        when(habitAssignRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssignRepo.save(habitAssign), HabitAssignManagementDto.class))
            .thenReturn(habitAssignManagementDto);
        assertEquals(habitAssignManagementDto,
            habitAssignService.updateStatusByHabitIdAndUserId(1L, 1L, habitAssignStatDto));
    }

    @Test
    void enrollHabitThrowWrongIdException() {
        LocalDate localDate = LocalDate.now();
        when(habitAssignRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
            () -> habitAssignService.enrollHabit(1L, 1L, localDate, "en"));
    }

    @Test
    void unenrollHabit() {
        LocalDate enrollDate = LocalDate.now();
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();
        HabitStatusCalendarVO habitStatusCalendarVO = HabitStatusCalendarVO.builder()
            .enrollDate(enrollDate).build();
        when(habitAssignRepo.findByHabitIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssign, HabitAssignVO.class)).thenReturn(habitAssignVO);
        when(habitAssignRepo.save(habitAssign)).thenReturn(habitAssign);

        when(habitStatusCalendarService
            .findHabitStatusCalendarByEnrollDateAndHabitAssign(
                enrollDate, modelMapper.map(habitAssign, HabitAssignVO.class)))
                    .thenReturn(habitStatusCalendarVO);

        habitAssignService.unenrollHabit(1L, 1L, enrollDate);
        verify(habitStatusCalendarService).delete(habitStatusCalendarVO);
    }

    @Test
    void unenrollHabitThrowBadRequestException() {
        LocalDate enrollDate = LocalDate.now();
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();
        when(habitAssignRepo.findByHabitIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssign, HabitAssignVO.class)).thenReturn(habitAssignVO);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitAssign(enrollDate, habitAssignVO))
            .thenReturn(null);

        assertThrows(BadRequestException.class, () -> habitAssignService
            .unenrollHabit(1L, 1L, enrollDate));
    }

    @Test
    void unenrollHabitThrowWrongIdException() {
        LocalDate enrollDate = LocalDate.now();
        when(habitAssignRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> habitAssignService
            .unenrollHabit(1L, 1L, enrollDate));
    }

    @Test
    void cancelHabitAssign() {
        habitAssign.setStatus(HabitAssignStatus.INPROGRESS);
        habitAssignDto.setStatus(HabitAssignStatus.CANCELLED);

        when(habitAssignRepo.findByHabitIdAndUserIdAndStatusIsInprogress(1L, 1L)).thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);

        assertEquals(habitAssignDto, habitAssignService.cancelHabitAssign(1L, 1L));

        verify(habitAssignRepo).save(habitAssign);
    }

    @Test
    void deleteHabitAssign() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        when(habitAssignRepo.findByUserIdAndHabitId(1L, 1L)).thenReturn(Optional.ofNullable(habitAssign));
        assert habitAssign != null;
        habitAssignService.deleteHabitAssign(1L, 1L);
        verify(habitAssignRepo).delete(habitAssign);
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

        HabitDto habitDto = ModelUtils.getHabitDto();
        habitAssign.setHabit(habit);

        habit.setHabitTranslations(Collections.singletonList(translation));

        when(habitAssignRepo.findAllByHabitId(habitId)).thenReturn(Collections.singletonList(habitAssign));
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        when(modelMapper.map(translation, HabitDto.class)).thenReturn(habitDto);
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

        when(habitAssignRepo.findAllHabitAssignsByStatusAndHabitId(HabitAssignStatus.ACQUIRED, habitId))
            .thenReturn(habitAssignList);

        Long actual = habitAssignService.getNumberHabitAssignsByHabitIdAndStatus(habitId, HabitAssignStatus.ACQUIRED);

        assertEquals(habitAssignList.size(), actual);

    }

    @Test
    void deleteAllHabitAssignsByHabit() {
        HabitVO habit = ModelUtils.getHabitVO();
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();

        when(habitAssignRepo.findAllByHabitId(any())).thenReturn(Collections.singletonList(habitAssign));
        when(modelMapper.map(habitAssign, HabitAssignVO.class)).thenReturn(habitAssignVO);
        habitAssignService.deleteAllHabitAssignsByHabit(habit);

        verify(habitStatisticService).deleteAllStatsByHabitAssign(habitAssignVO);
        verify(habitAssignRepo).delete(habitAssign);
        verify(habitAssignRepo, times(1)).delete(any());
    }

    @Test
    void enrollHabit() {
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();
        HabitStatusCalendarVO calendarVO = null;
        HabitTranslation translation = ModelUtils.getHabitTranslation();

        habitAssign.setHabit(habit);
        habitAssign.getHabit().setHabitTranslations(Collections.singletonList(translation));

        when(habitAssignRepo.findByHabitIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssign, HabitAssignVO.class)).thenReturn(habitAssignVO);
        when(habitStatusCalendarService
            .findHabitStatusCalendarByEnrollDateAndHabitAssign(any(LocalDate.class), any(HabitAssignVO.class)))
                .thenReturn(null);
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        when(modelMapper.map(translation, HabitDto.class)).thenReturn(habitDto);
        HabitAssignDto actualDto = habitAssignService.enrollHabit(1L, 1L, LocalDate.now(), "en");

        verify(habitAssignRepo, times(1)).save(any(HabitAssign.class));

        assertEquals(habitAssignDto, actualDto);

    }

    @Test
    void enrollHabitThrowException() {
        when(habitAssignRepo.findByHabitIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        LocalDate d = LocalDate.now();
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

        when(habitAssignRepo.findAllInprogressHabitAssignsOnDate(anyLong(), any(LocalDate.class)))
            .thenReturn(Collections.singletonList(habitAssign));

        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);

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

        when(habitAssignRepo.findAllInprogressHabitAssignsOnDate(anyLong(), any(LocalDate.class)))
            .thenReturn(Collections.singletonList(habitAssign));

        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);

        List<HabitAssignDto> dtoList =
            habitAssignService.findInprogressHabitAssignsOnDateContent(id, date, language.getCode());
        assertEquals(dtoList.get(0), habitAssignDto);

    }

    @Test
    void addDefaultHabitIf() {

        UserVO userVo = ModelUtils.createUserVO2();
        UserVO userVo2 = new UserVO();
        User user = new User();
        Habit habit = new Habit();
        habit.setId(1L);
        HabitAssign habitAssign = getHabitAssign();

        when(habitAssignRepo.findAllByUserId(1L)).thenReturn(new ArrayList<>());
        when(modelMapper.map(userVo, UserVO.class)).thenReturn(userVo2);
        when(modelMapper.map(userVo2, User.class)).thenReturn(user);
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitAssignRepo.findByHabitIdAndUserIdAndStatusIsCancelled(1L, user.getId())).thenReturn(habitAssign);
        when(modelMapper.map(habitAssign, HabitAssignManagementDto.class)).thenReturn(new HabitAssignManagementDto());

        habitAssignService.addDefaultHabit(userVo, "eu");

        verify(habitRepo).findById(1L);
    }

    @Test
    void updateUserShoppingList() {
        HabitAssign habitAssign = getHabitAssign();
        habitAssign.setDuration(20);
        when(habitRepo.existsById(anyLong())).thenReturn(true);
        when(habitAssignRepo.findByHabitIdAndUserIdAndStatusIsInprogress(anyLong(), anyLong()))
            .thenReturn(Optional.of(habitAssign));
        when(shoppingListItemRepo.getShoppingListByListOfId(any())).thenReturn(List.of(getShoppingListItem()));
        when(habitAssignRepo.save(any())).thenReturn(habitAssign);
        when(modelMapper.map(any(), any())).thenReturn(getHabitAssignUserShoppingListItemDto());

        HabitAssignUserShoppingListItemDto result =
            habitAssignService.updateUserShoppingItemListAndDuration(1L, 21L, getHabitAssignPropertiesDto());
        assertEquals(20, result.getDuration());
        assertEquals(1, result.getUserShoppingListItemsDto().size());
    }

    @Test
    void updateUserShoppingListShouldThrowNotFoundException() {
        when(habitRepo.existsById(anyLong())).thenReturn(false);

        Exception thrown1 = assertThrows(NotFoundException.class,
            () -> habitAssignService.updateUserShoppingItemListAndDuration(1L, 21L,
                getHabitAssignPropertiesDto()));
        assertEquals(thrown1.getMessage(), ErrorMessage.HABIT_NOT_FOUND_BY_ID + 1L);
    }

    @Test
    void updateUserShoppingListShouldThrowInvalidStatusException() {
        when(habitRepo.existsById(anyLong())).thenReturn(true);
        when(habitAssignRepo.findByHabitIdAndUserIdAndStatusIsInprogress(anyLong(), anyLong()))
            .thenReturn(Optional.empty());
        Exception thrown1 = assertThrows(InvalidStatusException.class,
            () -> habitAssignService.updateUserShoppingItemListAndDuration(1L, 21L,
                getHabitAssignPropertiesDto()));
        assertEquals(ErrorMessage.HABIT_ASSIGN_STATUS_IS_NOT_INPROGRESS, thrown1.getMessage());
    }

    @Test
    void updateUserShoppingListItem() {
        UserShoppingListItem userShoppingListItem = getUserShoppingListItem();
        when(userShoppingListItemRepo.saveAll(any())).thenReturn(List.of(userShoppingListItem));
        when(habitAssignRepo.findById(1L)).thenReturn(Optional.of(getHabitAssign()));
        when(shoppingListItemRepo.findById(1L)).thenReturn(Optional.of(getShoppingListItem()));
        habitAssignService.updateUserShoppingListItem(getUpdateUserShoppingListDto());
        verify(userShoppingListItemRepo, times(1)).saveAll(any());
    }

    @Test
    void updateUserShoppingListItemThrowException() {
        when(habitAssignRepo.findById(1L)).thenReturn(Optional.of(getHabitAssign()));

        assertThrows(ShoppingListItemNotFoundException.class,
            () -> habitAssignService.updateUserShoppingListItem(getUpdateUserShoppingListDto()));
    }

    @Test
    void getReadinessPercent() {
        habitAssignDto.setWorkingDays(30);
        habitAssignDto.setDuration(2);

        assertEquals(1500, habitAssignService.getReadinessPercent(habitAssignDto));
    }
}