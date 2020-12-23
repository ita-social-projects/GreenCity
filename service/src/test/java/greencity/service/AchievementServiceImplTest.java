package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.*;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.language.LanguageVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.Achievement;
import greencity.entity.AchievementCategory;
import greencity.entity.UserAchievement;
import greencity.entity.localization.AchievementTranslation;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AchievementRepo;
import java.util.*;
import java.util.stream.Collectors;

import greencity.repository.UserAchievementRepo;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {
    @Mock
    private AchievementRepo achievementRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AchievementCategoryService achievementCategoryService;
    @Mock
    private UserService userService;
    @Mock
    private UserActionService userActionService;
    @Mock
    private UserAchievementRepo userAchievementRepo;
    @InjectMocks
    private AchievementServiceImpl achievementService;

    @Test
    void findAllWithEmptyListTest() {
        when(achievementRepo.findAll()).thenReturn(Collections.emptyList());
        List<AchievementVO> findAllResult = achievementService.findAll();
        assertTrue(findAllResult.isEmpty());
    }

    @Test
    void findAllWithOneValueInRepoTest() {
        Achievement achievement = ModelUtils.getAchievement();
        when(achievementRepo.findAll())
            .thenReturn(Collections.singletonList(achievement));
        when(modelMapper.map(achievement, AchievementVO.class))
            .thenReturn(ModelUtils.getAchievementVO());
        List<AchievementVO> findAllResult = achievementService.findAll();
        assertEquals(1L, (long) findAllResult.get(0).getId());
    }

    @Test
    void findAllByPageableTest() {
        Pageable pageable = PageRequest.of(0, 2);
        Achievement achievement = ModelUtils.getAchievement();
        AchievementVO achievementVO = ModelUtils.getAchievementVO();
        Page<Achievement> pages = new PageImpl<>(Collections.singletonList(achievement), pageable, 10);
        when(achievementRepo.findAll(pageable)).thenReturn(pages);
        when(modelMapper.map(achievement, AchievementVO.class)).thenReturn(achievementVO);
        PageableAdvancedDto<AchievementVO> pageableAdvancedDto = achievementService.findAll(pageable);
        assertEquals(10, pageableAdvancedDto.getTotalElements());
    }

    @Test
    void searchAchievementByTest() {
        Pageable pageable = PageRequest.of(0, 2);
        Achievement achievement = ModelUtils.getAchievement();
        Page<Achievement> page = new PageImpl<>(Collections.singletonList(achievement), pageable, 10);
        AchievementVO achievementVO = ModelUtils.getAchievementVO();
        when(achievementRepo.searchAchievementsBy(pageable, "")).thenReturn(page);
        when(modelMapper.map(achievement, AchievementVO.class)).thenReturn(achievementVO);
        PageableAdvancedDto<AchievementVO> pageableAdvancedDto = achievementService.searchAchievementBy(pageable, "");
        assertEquals(10, pageableAdvancedDto.getTotalElements());
    }

    @Test
    void saveTest() {
        Achievement achievement = ModelUtils.getAchievement();
        AchievementCategory achievementCategory = ModelUtils.getAchievementCategory();
        AchievementPostDto achievementPostDto = ModelUtils.getAchievementPostDto();
        AchievementCategoryVO achievementCategoryVO = ModelUtils.getAchievementCategoryVO();
        AchievementVO achievementVO = ModelUtils.getAchievementVO();
        UserVO userVO = ModelUtils.getUserVO();
        UserAchievementVO userAchievement = ModelUtils.getUserAchievementVO();
        List<UserAchievementVO> userAchievements = new ArrayList<>();
        userAchievements.add(userAchievement);
        userVO.setUserAchievements(userAchievements);
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        List<UserActionVO> userActionVOS = new ArrayList<>();
        userActionVOS.add(userActionVO);
        userVO.setUserActions(userActionVOS);
        when(modelMapper.map(achievementPostDto, Achievement.class)).thenReturn(achievement);
        when(achievementCategoryService.findByName("Test")).thenReturn(achievementCategoryVO);
        when(modelMapper.map(achievementCategoryVO, AchievementCategory.class)).thenReturn(achievementCategory);
        when(achievementRepo.save(achievement)).thenReturn(achievement);
        when(modelMapper.map(achievement, AchievementVO.class)).thenReturn(achievementVO);
        when(userService.findAll()).thenReturn(Collections.singletonList(userVO));
        when(userActionService.findUserActionByUserIdAndAchievementCategory(1L, 1L)).thenReturn(null);

        AchievementVO expected = achievementService.save(achievementPostDto);
        assertEquals(expected, achievementVO);
    }

    @Test
    void findByIdTest() {
        Achievement achievement = ModelUtils.getAchievement();
        AchievementVO achievementVO = ModelUtils.getAchievementVO();
        when(achievementRepo.findById(1L)).thenReturn(Optional.of(achievement));
        when(modelMapper.map(achievement, AchievementVO.class)).thenReturn(achievementVO);
        AchievementVO expected = achievementService.findById(1L);
        assertEquals(expected, achievementVO);
    }

    @Test
    void updateTest() {
        Achievement achievement = ModelUtils.getAchievement();
        AchievementTranslation achievementTranslation = ModelUtils.getAchievementTranslation();
        achievement.setTranslations(Collections.singletonList(achievementTranslation));
        AchievementPostDto achievementPostDto = ModelUtils.getAchievementPostDto();
        AchievementManagementDto achievementManagementDto = ModelUtils.getAchievementManagementDto();
        AchievementTranslationVO achievementTranslationVO = ModelUtils.getAchievementTranslationVO();
        achievementManagementDto.setTranslations(Collections.singletonList(achievementTranslationVO));
        achievementPostDto.setTranslations(Collections.singletonList(ModelUtils.getAchievementTranslationVO()));
        when(achievementRepo.findById(1L)).thenReturn(Optional.of(achievement));
        when(achievementRepo.save(achievement)).thenReturn(achievement);
        when(modelMapper.map(achievement, AchievementPostDto.class)).thenReturn(achievementPostDto);
        AchievementPostDto expected = achievementService.update(achievementManagementDto);
        assertEquals(expected, achievementPostDto);
    }

    @Test
    void updateWithUnknownId() {
        AchievementManagementDto achievementManagementDto = ModelUtils.getAchievementManagementDto();
        when(achievementRepo.findById(achievementManagementDto.getId()))
            .thenThrow(
                new NotUpdatedException(ErrorMessage.ACHIEVEMENT_NOT_FOUND_BY_ID + achievementManagementDto.getId()));
        assertThrows(NotUpdatedException.class, () -> achievementService.update(achievementManagementDto));
    }

    @Test
    void deleteTest() {
        Achievement achievement = ModelUtils.getAchievement();
        doNothing().when(achievementRepo).deleteById(1L);
        achievementRepo.deleteById(1L);
        verify(achievementRepo, times(1)).deleteById(1L);
        long expected = achievementService.delete(1L);
        Assertions.assertEquals(expected, achievement.getId());
    }

    @Test
    void deleteWithNonExistingId() {
        doThrow(EmptyResultDataAccessException.class).when(achievementRepo).deleteById(345L);
        assertThrows(NotDeletedException.class, () -> achievementService.delete(345L));
    }

    @Test
    void deleteAll() {
        List<Long> listId = Arrays.asList(1L, 2L, 3L, 4L);
        listId.forEach(l -> {
            doNothing().when(achievementRepo).deleteById(l);
            achievementRepo.deleteById(l);
            verify(achievementRepo, times(1)).deleteById(l);
        });
        achievementService.deleteAll(listId);
    }

    @Test
    void findByCategoryIdAndCondition() {
        Achievement achievement = ModelUtils.getAchievement();
        AchievementVO achievementVO = ModelUtils.getAchievementVO();
        achievement.setAchievementCategory(ModelUtils.getAchievementCategory());
        achievementVO.setAchievementCategory(ModelUtils.getAchievementCategoryVO());
        when(achievementRepo.findByAchievementCategoryIdAndCondition(1L, 1)).thenReturn(Optional.of(achievement));
        when(modelMapper.map(achievement, AchievementVO.class)).thenReturn(achievementVO);
        assertEquals(achievementVO, achievementService.findByCategoryIdAndCondition(1L, 1));
    }

    @Test
    void findAchievementsWithStatusActive() {
        UserAchievement userAchievementt = ModelUtils.getUserAchievement();
        List<AchievementNotification> achievementNotifications = new ArrayList<>();
        List<UserAchievement> userAchievementList = Collections.singletonList(userAchievementt);
        userAchievementList.forEach(userAchievement -> {
            Achievement achievement = userAchievement.getAchievement();
            achievementNotifications.add(AchievementNotification.builder()
                .id(userAchievement.getId())
                .achievementCategory(AchievementCategoryVO.builder()
                    .id(achievement.getAchievementCategory().getId())
                    .name(achievement.getAchievementCategory().getName())
                    .build())
                .translations(achievement.getTranslations().stream()
                    .map(achievementTranslation -> AchievementTranslationVO.builder()
                        .id(achievementTranslation.getId())
                        .language(LanguageVO.builder()
                            .id(achievementTranslation.getLanguage().getId())
                            .code(achievementTranslation.getLanguage().getCode())
                            .build())
                        .message(achievementTranslation.getMessage())
                        .description(achievementTranslation.getDescription())
                        .title(achievementTranslation.getTitle())
                        .build())
                    .collect(Collectors.toList()))
                .build());
        });
        when(userAchievementRepo.findAchievementsWithStatusActive(1L)).thenReturn(userAchievementList);
        assertEquals(achievementNotifications, achievementService.findAchievementsWithStatusActive(1L));
    }
}
