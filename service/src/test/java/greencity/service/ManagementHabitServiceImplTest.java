package greencity.service;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habittranslation.HabitTranslationManagementDto;
import greencity.dto.language.LanguageDTO;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.repository.HabitRepo;
import greencity.repository.HabitTranslationRepo;
import greencity.repository.options.HabitFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ManagementHabitServiceImplTest {

    @Mock
    private HabitRepo habitRepo;
    @Mock
    private HabitTranslationRepo habitTranslationRepo;
    @Mock
    private LanguageService languageService;
    @Mock
    private FileService fileService;
    @Mock
    private HabitAssignService habitAssignService;
    @Mock
    private HabitFactService habitFactService;
    @Mock
    private AdviceService adviceService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ManagementHabitServiceImpl managementHabitService;

    @Test
    void getByIdTest() {
        Habit habit = ModelUtils.getHabit();
        HabitManagementDto habitManagementDto = ModelUtils.gethabitManagementDto();
        when(habitRepo.findById(1L))
            .thenReturn(Optional.of(habit));
        when(modelMapper.map(habit, HabitManagementDto.class))
            .thenReturn(habitManagementDto);
        HabitManagementDto result = managementHabitService.getById(1L);
        assertEquals(1, (long) result.getId());
    }

    @Test
    void getAllHabitsDtoTest() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<HabitManagementDto> habitManagementDtos = Collections.singletonList(new HabitManagementDto());
        List<Habit> habits = Collections.singletonList(ModelUtils.getHabit());
        Page<Habit> listHabits = new PageImpl<>(habits, pageRequest, habits.size());

        PageableDto<HabitManagementDto> result =
            new PageableDto<>(habitManagementDtos, habitManagementDtos.size(), 0, 1);
        when(modelMapper.map(habits.getFirst(), HabitManagementDto.class)).thenReturn(habitManagementDtos.getFirst());
        when(habitRepo.findAll(any(HabitFilter.class), eq(pageRequest))).thenReturn(listHabits);
        assertEquals(result, managementHabitService.getAllHabitsDto(null, null, null,
            null, false, false, pageRequest));
    }

    @Test
    void saveHabitAndTranslationsTest() {
        when(languageService.findByCode("en")).thenReturn(LanguageDTO.builder().id(1L).code("en").build());
        when(modelMapper.map(languageService.findByCode("en"),
            Language.class)).thenReturn(Language.builder().id(1L).code("en").build());
        HabitManagementDto habitManagementDto = HabitManagementDto.builder().id(1L)
            .image(AppConstant.DEFAULT_HABIT_IMAGE)
            .habitTranslations(Arrays.asList(
                HabitTranslationManagementDto.builder().habitItem("Item").description("Description").languageCode("en")
                    .name("Name").build()))
            .build();
        Habit habit = Habit.builder()
            .image(AppConstant.DEFAULT_HABIT_IMAGE)
            .habitTranslations(
                habitManagementDto.getHabitTranslations().stream()
                    .map(habitTranslationDto -> HabitTranslation.builder()
                        .description(habitTranslationDto.getDescription())
                        .habitItem(habitTranslationDto.getHabitItem())
                        .name(habitTranslationDto.getName())
                        .language(modelMapper.map(
                            languageService.findByCode(habitTranslationDto.getLanguageCode()),
                            Language.class))
                        .build())
                    .collect(Collectors.toList()))
            .build();
        habit.getHabitTranslations().forEach(habitTranslation -> habitTranslation.setHabit(habit));

        when(habitRepo.save(habit)).thenReturn(habit);
        when(habitTranslationRepo.saveAll(habit.getHabitTranslations())).thenReturn(habit.getHabitTranslations());
        when(modelMapper.map(habit, HabitManagementDto.class)).thenReturn(habitManagementDto);
        assertEquals(habitManagementDto, managementHabitService.saveHabitAndTranslations(habitManagementDto, null));

    }

    @Test
    void updateTest() {
        when(habitRepo.findById(1L)).thenReturn(Optional.of(Habit.builder().id(1L).habitTranslations(Arrays
            .asList(HabitTranslation.builder().habitItem("Item").description("Description")
                .language(Language.builder().id(1L).code("en").build()).name("Name").build()))
            .build()));
        HabitManagementDto habitManagementDto = HabitManagementDto.builder().id(1l).image("image")
            .habitTranslations(Arrays
                .asList(HabitTranslationManagementDto.builder().habitItem("Item").description("Description")
                    .languageCode("en").name("Name").build()))
            .build();
        Map<String, HabitTranslationManagementDto> managementDtoMap = habitManagementDto.getHabitTranslations().stream()
            .collect(Collectors.toMap(HabitTranslationManagementDto::getLanguageCode,
                Function.identity()));
        Habit habit = habitRepo.findById(1L).orElse(null);
        assert habit != null;
        habit.getHabitTranslations().forEach(
            ht -> enhanceTranslationWithDto(managementDtoMap.get(ht.getLanguage().getCode()), ht));
        when(habitRepo.save(habit)).thenReturn(habit);
        managementHabitService.update(habitManagementDto, null);
        verify(habitRepo, times(1)).save(habit);
    }

    private void enhanceTranslationWithDto(HabitTranslationManagementDto htDto, HabitTranslation ht) {
        ht.setDescription(htDto.getDescription());
        ht.setHabitItem(htDto.getHabitItem());
        ht.setName(htDto.getName());
    }

    @Test
    void deleteTest() {
        when(habitRepo.findById(1L)).thenReturn(Optional.of(Habit.builder().id(1l).build()));
        Habit habit = habitRepo.findById(1L).orElse(null);
        when(modelMapper.map(habit, HabitVO.class)).thenReturn(HabitVO.builder().id(1L).build());
        managementHabitService.delete(1L);
        verify(habitTranslationRepo, times(1)).deleteAllByHabit(habit);
        verify(habitFactService, times(1)).deleteAllByHabit(modelMapper.map(habit, HabitVO.class));
        verify(adviceService, times(1)).deleteAllByHabit(modelMapper.map(habit, HabitVO.class));
        verify(habitAssignService, times(1)).deleteAllHabitAssignsByHabit(modelMapper.map(habit, HabitVO.class));
        verify(habitRepo, times(1)).delete(habit);

    }

    @Test
    void deleteAllTest() {
        Habit habit = new Habit();
        HabitVO habitVO = new HabitVO();
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        list.add(4L);
        list.add(5L);
        when(habitRepo.findById(anyLong())).thenReturn(Optional.of(habit));
        when(modelMapper.map(habit, HabitVO.class)).thenReturn(habitVO);
        doNothing().when(habitTranslationRepo).deleteAllByHabit(habit);
        doNothing().when(habitFactService).deleteAllByHabit(habitVO);
        doNothing().when(adviceService).deleteAllByHabit(habitVO);
        doNothing().when(habitAssignService).deleteAllHabitAssignsByHabit(habitVO);
        doNothing().when(habitRepo).delete(habit);
        managementHabitService.deleteAll(list);
        verify(habitRepo, times(5)).delete(any(Habit.class));
        verify(habitTranslationRepo, times(5)).deleteAllByHabit(any(Habit.class));

    }

}