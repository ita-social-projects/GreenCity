package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.*;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.language.LanguageVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.Achievement;
import greencity.entity.AchievementCategory;
import greencity.entity.Language;
import greencity.entity.UserAchievement;
import greencity.entity.localization.AchievementTranslation;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AchievementRepo;

import java.util.*;

import greencity.repository.AchievementTranslationRepo;
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
    private RestClient restClient;
    @Mock
    private UserActionService userActionService;
    @Mock
    private UserAchievementRepo userAchievementRepo;
    @InjectMocks
    private AchievementServiceImpl achievementService;
    @Mock
    private AchievementTranslationRepo achievementTranslationRepo;

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
        when(achievementRepo.findById(1L)).thenReturn(Optional.of(achievement));
        long expected = achievementService.delete(1L);
        Assertions.assertEquals(expected, achievement.getId());
    }

    @Test
    void deleteWithNonExistingId() {
        when(achievementRepo.findById(345L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> achievementService.delete(345L));
    }

    @Test
    void deleteAll() {
        List<Long> listId = Arrays.asList(1L, 2L, 3L, 4L);

        listId.forEach(l -> when(achievementRepo.findById(l))
            .thenReturn(Optional.ofNullable(Achievement.builder().id(l).build())));
        achievementService.deleteAll(listId);
        verify(achievementRepo, times(4)).save(any());
    }

    @Test
    void findAchievementsWithStatusActive() {
        List<AchievementNotification> achievementNotifications =
            Collections.singletonList(AchievementNotification.builder()
                .id(1L)
                .message("test")
                .description("test")
                .title("test")
                .build());
        UserVO userVO = ModelUtils.getUserVO();
        userVO.setLanguageVO(LanguageVO.builder()
            .id(1L)
            .code("ua")
            .build());
        Achievement achievement = ModelUtils.getAchievement();
        List<AchievementTranslation> achievementTranslations = Collections
            .singletonList(AchievementTranslation.builder()
                .id(1L)
                .achievement(achievement)
                .message("test")
                .description("test")
                .title("test")
                .language(Language.builder()
                    .id(1L)
                    .code("ua")
                    .build())
                .build());
        UserAchievement userAchievement = ModelUtils.getUserAchievement();
        when(restClient.findById(1L)).thenReturn(userVO);
        when(achievementTranslationRepo.findAllUnnotifiedForUser(1L, 1L))
            .thenReturn(achievementTranslations);
        when(userAchievementRepo.getUserAchievementByIdAndAchievementId(1L, 1L)).thenReturn(userAchievement);
        userAchievement.setNotified(true);
        when(userAchievementRepo.save(userAchievement)).thenReturn(userAchievement);
        assertEquals(achievementNotifications, achievementService.findAllUnnotifiedForUser(1L));
    }
}
