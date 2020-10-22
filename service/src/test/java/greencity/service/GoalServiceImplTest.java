package greencity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.goal.GoalDto;
import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserGoalVO;
import greencity.entity.CustomGoal;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.entity.localization.GoalTranslation;
import greencity.enums.GoalStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.GoalNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {
    @Mock
    private GoalTranslationRepo goalTranslationRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private LanguageService languageService;
    @Mock
    private GoalRepo goalRepo;
    @Mock
    private CustomGoalRepo customGoalRepo;
    @InjectMocks
    private GoalServiceImpl goalService;

    private UserGoal predefinedUserGoal = ModelUtils.getPredefinedUserGoal();
    private UserGoal customUserGoal = ModelUtils.getCustomUserGoal();
    private UserGoalResponseDto predefinedUserGoalDto = ModelUtils.getPredefinedUserGoalDto();
    private UserGoalResponseDto customUserGoalDto = ModelUtils.getCustomUserGoalDto();

    @Test
    void findAllTest() {
        List<GoalTranslation> goalTranslations = ModelUtils.getGoalTranslations();

        List<GoalDto> goalsDto = goalTranslations
            .stream()
            .map(goalTranslation -> new GoalDto(goalTranslation.getGoal().getId(), goalTranslation.getContent()))
            .collect(Collectors.toList());

        when(modelMapper.map(goalTranslations.get(0), GoalDto.class)).thenReturn(goalsDto.get(0));
        when(modelMapper.map(goalTranslations.get(1), GoalDto.class)).thenReturn(goalsDto.get(1));
        when(goalTranslationRepo.findAllByLanguageCode(AppConstant.DEFAULT_LANGUAGE_CODE))
            .thenReturn(goalTranslations);

        assertEquals(goalService.findAll(AppConstant.DEFAULT_LANGUAGE_CODE), goalsDto);
    }

    @Test
    void getUserGoalResponseDtoFromPredefinedGoal() {
        UserGoalVO map = ModelUtils.getUserGoalVO();
        Goal goal = predefinedUserGoal.getGoal();
        String code = ModelUtils.getLanguage().getCode();

        when(modelMapper.map(map, UserGoal.class)).thenReturn(predefinedUserGoal);
        when(modelMapper.map(predefinedUserGoal, UserGoalResponseDto.class)).thenReturn(predefinedUserGoalDto);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(code);
        when(goalRepo.findById(1L)).thenReturn(Optional.ofNullable(goal));
        when(goalTranslationRepo.findByGoalAndLanguageCode(goal, code))
            .thenReturn(Optional.of(ModelUtils.getGoalTranslation()));

        UserGoalResponseDto actual = goalService.getUserGoalResponseDtoFromPredefinedGoal(map);

        assertEquals(predefinedUserGoalDto, actual);
    }

    @Test
    void getUserGoalResponseDtoFromPredefinedGoalFailed() {
        UserGoalVO map = ModelUtils.getUserGoalVO();

        when(modelMapper.map(map, UserGoal.class)).thenReturn(predefinedUserGoal);
        when(modelMapper.map(predefinedUserGoal, UserGoalResponseDto.class)).thenReturn(predefinedUserGoalDto);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(ModelUtils.getLanguage().getCode());
        when(goalRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () ->
            goalService.getUserGoalResponseDtoFromPredefinedGoal(map));
    }

    @Test
    void getUserGoalResponseDtoFromCustomGoal() {
        UserGoalVO map = ModelUtils.getUserGoalVO();
        CustomGoal customGoal = customUserGoal.getCustomGoal();

        when(modelMapper.map(map, UserGoal.class)).thenReturn(customUserGoal);
        when(modelMapper.map(customUserGoal, UserGoalResponseDto.class)).thenReturn(customUserGoalDto);
        when(customGoalRepo.findById(8L)).thenReturn(Optional.ofNullable(customGoal));

        UserGoalResponseDto actual = goalService.getUserGoalResponseDtoFromCustomGoal(map);

        assertEquals(customUserGoalDto, actual);
    }

    @Test
    void getUserGoalResponseDtoFromCustomGoalFailed() {
        UserGoalVO map = ModelUtils.getUserGoalVO();

        when(modelMapper.map(map, UserGoal.class)).thenReturn(customUserGoal);
        when(modelMapper.map(customUserGoal, UserGoalResponseDto.class)).thenReturn(customUserGoalDto);
        when(customGoalRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            goalService.getUserGoalResponseDtoFromCustomGoal(map));
    }

    @Test
    void getShoppingListTest() {
        ShoppingListDtoResponse shoppingListDtoResponse = ModelUtils.getShoppingListDtoResponse();
        List<ShoppingListDtoResponse> expected = List.of(shoppingListDtoResponse);

        when(goalRepo.getShoppingList(1L, "en")).thenReturn(expected);

        List<ShoppingListDtoResponse> actual = goalService.getShoppingList(1L, "en");

        assertEquals(expected, actual);

        verify(goalRepo, timeout(1)).getShoppingList(1L, "en");
    }

    @Test
    void changeGoalOrCustomGoalStatusTest_WithDefaultGoal() {
        String trueStatus = GoalStatus.DONE.toString();
        LocalDateTime dateTime = LocalDateTime.now().withNano(0);

        when(goalRepo.findById(1L)).thenReturn(Optional.of(new Goal()));

        goalService.changeGoalOrCustomGoalStatus(1L, true, 1L, null);

        verify(goalRepo, times(1)).changeGoalStatus(1L, 1L, trueStatus, dateTime);
    }

    @Test
    void changeGoalOrCustomGoalStatusTest_WithDefaultCustomGoal() {
        String falseStatus = GoalStatus.ACTIVE.toString();
        LocalDateTime dateTime = LocalDateTime.now().withNano(0);

        when(customGoalRepo.findById(1L)).thenReturn(Optional.of(new CustomGoal()));

        goalService.changeGoalOrCustomGoalStatus(1L, false, null, 1L);

        verify(customGoalRepo, times(1)).changeCustomGoalStatus(1L, 1L, falseStatus, dateTime);
    }

    @Test
    void changeGoalOrCustomGoalStatusTest_throwBadRequestExceptionWithTwoId() {
        assertThrows(BadRequestException.class,
            () -> goalService.changeGoalOrCustomGoalStatus(1L, true, 1L, 1L));
    }

    @Test
    void changeGoalOrCustomGoalStatusTest_throwBadRequestExceptionWithTwoNulls() {
        assertThrows(BadRequestException.class,
            () -> goalService.changeGoalOrCustomGoalStatus(1L, true, null, null));
    }

    @Test
    void changeGoalOrCustomGoalStatusTest_throwNotFoundExceptionWithDefaultGoalId() {
        when(goalRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> goalService.changeGoalOrCustomGoalStatus(1L, true, 1L, null));
    }

    @Test
    void changeGoalOrCustomGoalStatusTest_throwNotFoundExceptionWithCustomGoalId() {
        when(customGoalRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> goalService.changeGoalOrCustomGoalStatus(1L, true, null, 1L));
    }
}
