/*
 * package greencity.service.impl;
 * 
 * import greencity.ModelUtils;
 * 
 * import greencity.dto.habit.HabitDto; import greencity.entity.Habit; import
 * greencity.entity.HabitDictionary; import
 * greencity.entity.HabitDictionaryTranslation; import greencity.entity.User;
 * import greencity.exception.exceptions.WrongIdException; import
 * greencity.repository.HabitTranslationRepo; import
 * greencity.repository.HabitRepo; import org.junit.jupiter.api.Test; import
 * org.junit.jupiter.api.extension.ExtendWith; import org.mockito.InjectMocks;
 * import org.mockito.Mock; import org.modelmapper.ModelMapper; import
 * org.springframework.data.domain.Page; import
 * org.springframework.data.domain.PageImpl; import
 * org.springframework.data.domain.Pageable; import
 * org.springframework.test.context.junit.jupiter.SpringExtension;
 * 
 * import java.time.ZonedDateTime; import java.util.Arrays; import
 * java.util.Collections; import java.util.List; import java.util.Optional;
 * import java.util.stream.Collectors;
 * 
 * import static org.junit.jupiter.api.Assertions.assertEquals; import static
 * org.junit.jupiter.api.Assertions.assertThrows; import static
 * org.mockito.ArgumentMatchers.*; import static org.mockito.Mockito.when;
 * 
 * @ExtendWith(SpringExtension.class) class HabitServiceImplTest {
 * 
 * @InjectMocks private HabitServiceImpl habitService;
 * 
 * @Mock private HabitTranslationRepo habitTranslationRepo;
 * 
 * @Mock private ModelMapper modelMapper;
 * 
 * @Mock private HabitRepo habitRepo;
 * 
 * @Test void getHabitDictionaryTranslation() { Habit habit =
 * ModelUtils.getHabit(); HabitDictionaryTranslation habitDictionaryTranslation
 * = ModelUtils.getHabitDictionaryTranslation();
 * when(habitTranslationRepo.findByHabitDictionaryAndLanguageCode(any(
 * HabitDictionary.class), anyString()))
 * .thenReturn(Optional.of(habitDictionaryTranslation));
 * assertEquals(habitDictionaryTranslation,
 * habitService.getHabitDictionaryTranslation(habit, "en")); }
 * 
 * @Test void getById() { Habit habit = ModelUtils.getHabit();
 * when(habitRepo.findById(anyLong())).thenReturn(Optional.of(habit));
 * assertEquals(habit, habitService.getById(1L)); }
 * 
 * @Test void assignHabitForUserExceptionTest() { User user =
 * ModelUtils.getUser();
 * when(habitRepo.findById(1L)).thenReturn(Optional.empty());
 * assertThrows(WrongIdException.class, () ->
 * habitService.assignHabitForUser(1L, user)); }
 * 
 * @Test void getAllHabitsDtoTest() { List<Habit> habitList = Arrays.asList(
 * Habit.builder() .statusHabit(true) .createDate(ZonedDateTime.now()) .build(),
 * Habit.builder() .statusHabit(true) .createDate(ZonedDateTime.now())
 * .build());
 * 
 * when(habitRepo.findAll()).thenReturn(habitList); List<HabitDto> expected =
 * habitList .stream() .map(habit -> modelMapper.map(habit, HabitDto.class))
 * .collect(Collectors.toList());
 * 
 * List<HabitDto> actual = habitService.getAllHabitsDto();
 * assertEquals(expected, actual); }
 * 
 * @Test void getAllHabitsByLanguageCode() { HabitDictionaryTranslation
 * habitDictionaryTranslation = ModelUtils.getHabitDictionaryTranslation();
 * List<HabitDictionaryTranslation> habitDictionaryTranslationList =
 * Collections.singletonList(habitDictionaryTranslation);
 * Page<HabitDictionaryTranslation> pages = new
 * PageImpl<>(habitDictionaryTranslationList); Pageable pageable = null;
 * when(habitTranslationRepo.findAllByLanguageCode(anyObject(),anyString())).
 * thenReturn(pages); assertThrows(UnsupportedOperationException.class, () ->
 * habitService.getAllHabitsByLanguageCode(pageable,"en")); } }
 */
