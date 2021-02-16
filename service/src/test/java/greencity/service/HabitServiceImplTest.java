package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.habit.HabitDto;
import greencity.entity.Goal;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.localization.GoalTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitTranslationRepo;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class HabitServiceImplTest {
    @Mock
    private HabitRepo habitRepo;
    @Mock
    private HabitTranslationRepo habitTranslationRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private GoalTranslationRepo goalTranslationRepo;
    @Mock
    private GoalRepo goalRepo;
    @InjectMocks
    private HabitServiceImpl habitService;

    @Test()
    void getByIdAndLanguageCode() {
        Habit habit = ModelUtils.getHabit();
        HabitDto habitDto = ModelUtils.getHabitDto();
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitTranslationRepo.findByHabitAndLanguageCode(habit, "en"))
            .thenReturn(Optional.of(habitTranslation));
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
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
        when(habitTranslationRepo.findAllByTagsAndLanguageCode(pageable, lowerCaseTags, "en"))
            .thenReturn(habitTranslationPage);
        assertEquals(pageableDto, habitService.getAllByTagsAndLanguageCode(pageable, tags, "en"));
    }

    @Test
    void getShoppingListForHabit() {
        GoalTranslation goalTranslation = ModelUtils.getGoalTranslation();
        List<GoalTranslation> goalTranslations = Collections.singletonList(goalTranslation);
        GoalDto goalDto = new GoalDto(1L, "test");
        List<GoalDto> goalDtos = Collections.singletonList(goalDto);
        when(modelMapper.map(goalTranslation, GoalDto.class)).thenReturn(goalDto);
        when(goalTranslationRepo.findAllGoalByHabitIdAndByLanguageCode("en", 1l))
            .thenReturn(goalTranslations);
        assertEquals(goalDtos, habitService.getShoppingListForHabit(1L, "en"));
    }

    @Test
    void addGoalToHabitTest() {
        doNothing().when(habitRepo).addShopingListItemToHabit(1L, 1L);
        habitService.addGoalToHabit(1L, 1L);
        verify(habitRepo).addShopingListItemToHabit(1L, 1L);
    }

    @Test
    void deleteGoalTest() {
        doNothing().when(habitRepo).upadateShopingListItemInHabit(1L, 1L);
        habitService.deleteGoal(1L, 1L);
        verify(habitRepo).upadateShopingListItemInHabit(1L, 1L);
    }

    @Test
    void addAllGoalToHabitTest() {
        List<Long> listID = Collections.singletonList(1L);
        doNothing().when(habitRepo).addShopingListItemToHabit(listID.get(0), 1L);
        habitService.addAllGoalByListOfId(1L, listID);
        verify(habitRepo, times(1)).addShopingListItemToHabit(listID.get(0), 1L);
    }

    @Test
    void deleteAllGoalToHabitTest() {
        List<Long> listID = Collections.singletonList(1L);
        doNothing().when(habitRepo).addShopingListItemToHabit(listID.get(0), 1L);
        habitService.deleteAllGoalByListOfId(1L, listID);
        verify(habitRepo, times(1)).upadateShopingListItemInHabit(listID.get(0), 1L);
    }
}
