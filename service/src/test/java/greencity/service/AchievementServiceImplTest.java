package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.*;
import greencity.dto.language.LanguageVO;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.entity.localization.AchievementTranslation;
import greencity.enums.AchievementStatus;
import greencity.enums.ActionContextType;
import greencity.enums.UserActionType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {
    @Mock
    private AchievementRepo achievementRepo;
    @Mock
    private UserActionRepo userActionRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AchievementCategoryService achievementCategoryService;
    @Mock
    private RestClient restClient;
    @Mock
    private UserAchievementRepo userAchievementRepo;
    @InjectMocks
    private AchievementServiceImpl achievementService;
    @Mock
    private AchievementTranslationRepo achievementTranslationRepo;
    @Mock
    private FileService fileService;

    @Test
    void save() {
        AchievementPostDto dto = ModelUtils.getAchievementPostDto();
        Achievement achievement = ModelUtils.getAchievement();
        MultipartFile file = new MockMultipartFile("icon.png", new byte[] {});

        when(modelMapper.map(dto, Achievement.class)).thenReturn(achievement);
        when(achievementCategoryService.findByName(dto.getAchievementCategory().getName()))
            .thenReturn(ModelUtils.getAchievementCategoryVO());
        when(modelMapper.map(ModelUtils.getAchievementCategoryVO(), AchievementCategory.class))
            .thenReturn(ModelUtils.getAchievementCategory());
        when(fileService.upload(file)).thenReturn("https://link.for.file/icon.png");
        when(achievementRepo.save(achievement)).thenReturn(achievement);
        when(userRepo.findAll()).thenReturn(List.of(ModelUtils.getUser()));
        when(modelMapper.map(achievement, AchievementVO.class)).thenReturn(ModelUtils.getAchievementVO());
        when(userAchievementRepo.existsByUserAndAchievement(ModelUtils.getUser(), achievement)).thenReturn(true);

        achievementService.save(dto, file);

        verify(achievementRepo).save(achievement);
    }

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

        when(achievementRepo.findById(achievementManagementDto.getId())).thenReturn(Optional.empty());

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

    @Test
    void isAchievedDefaultAchievement() {
        User user = ModelUtils.getUser();
        Achievement defaultAchievement = ModelUtils.getAchievement();

        when(userActionRepo.countAllByUserAndActionType(user, UserActionType.ECO_NEWS_CREATED)).thenReturn(0L);
        assertFalse(achievementService.isAchieved(user, defaultAchievement));

        when(userActionRepo.countAllByUserAndActionType(user, UserActionType.ECO_NEWS_CREATED)).thenReturn(1L);
        assertTrue(achievementService.isAchieved(user, defaultAchievement));
    }

    @Test
    void isAchievedRegisteredAchievement() {
        User user = ModelUtils.getUser();
        Achievement registeredAchievement = ModelUtils.getRegisteredAchievement();

        assertTrue(achievementService.isAchieved(user, registeredAchievement));
    }

    @Test
    void isAchievedHabitAcquiredAchievement() {
        User user = ModelUtils.getUser();
        Achievement habitAcquiredAchievement = ModelUtils.getHabitAcquiredAchievement();

        when(userActionRepo.existsByUserAndActionTypeAndContextTypeAndContextId(
            user, UserActionType.HABIT_ACQUIRED, ActionContextType.HABIT, 1L)).thenReturn(false);
        assertFalse(achievementService.isAchieved(user, habitAcquiredAchievement));

        when(userActionRepo.existsByUserAndActionTypeAndContextTypeAndContextId(
            user, UserActionType.HABIT_ACQUIRED, ActionContextType.HABIT, 1L)).thenReturn(true);
        assertTrue(achievementService.isAchieved(user, habitAcquiredAchievement));
    }

    @Test
    void tryToGiveUserAchievementSuccess() {
        User user = ModelUtils.getUser();
        Achievement achievement = ModelUtils.getAchievement();

        when(userAchievementRepo.existsByUserAndAchievement(user, achievement)).thenReturn(false);
        when(userActionRepo.countAllByUserAndActionType(user, UserActionType.ECO_NEWS_CREATED)).thenReturn(1L);

        achievementService.tryToGiveUserAchievement(user, achievement);

        verify(userAchievementRepo).save(any(UserAchievement.class));
    }

    @Test
    void tryToGiveUserAchievementFailureInactiveAchievement() {
        User user = ModelUtils.getUser();
        Achievement achievement = ModelUtils.getAchievement();
        achievement.setAchievementStatus(AchievementStatus.INACTIVE);

        achievementService.tryToGiveUserAchievement(user, achievement);

        verify(userAchievementRepo, never()).save(any(UserAchievement.class));
    }

    @Test
    void tryToGiveUserAchievementFailureAlreadyExists() {
        User user = ModelUtils.getUser();
        Achievement achievement = ModelUtils.getAchievement();

        when(userAchievementRepo.existsByUserAndAchievement(user, achievement)).thenReturn(true);

        achievementService.tryToGiveUserAchievement(user, achievement);

        verify(userAchievementRepo, never()).save(any(UserAchievement.class));
    }

    @Test
    void tryToGiveUserAchievementFailureNotAchieved() {
        User user = ModelUtils.getUser();
        Achievement achievement = ModelUtils.getAchievement();

        when(userAchievementRepo.existsByUserAndAchievement(user, achievement)).thenReturn(false);
        when(userActionRepo.countAllByUserAndActionType(user, UserActionType.ECO_NEWS_CREATED)).thenReturn(0L);

        achievementService.tryToGiveUserAchievement(user, achievement);

        verify(userAchievementRepo, never()).save(any(UserAchievement.class));
    }

    @Test
    void tryToGiveUserAchievementsByActionType() {
        User user = ModelUtils.getUser();
        UserActionType actionType = UserActionType.ECO_NEWS_CREATED;

        when(achievementRepo.findAllByActionType(actionType)).thenReturn(List.of(ModelUtils.getAchievement()));
        when(userAchievementRepo.existsByUserAndAchievement(eq(user), any(Achievement.class))).thenReturn(false);
        when(userActionRepo.countAllByUserAndActionType(user, UserActionType.ECO_NEWS_CREATED)).thenReturn(1L);

        achievementService.tryToGiveUserAchievementsByActionType(user, actionType);

        verify(userAchievementRepo).save(any(UserAchievement.class));
    }
}
