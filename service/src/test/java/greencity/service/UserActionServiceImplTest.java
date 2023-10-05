package greencity.service;

import greencity.ModelUtils;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.AchievementCategory;
import greencity.entity.UserAction;
import greencity.repository.AchievementCategoryRepo;
import greencity.repository.UserActionRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserActionServiceImplTest {
    @InjectMocks
    private UserActionServiceImpl userActionService;
    @Mock
    private UserActionRepo userActionRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepo userRepo;
    @Mock
    private AchievementCategoryRepo achievementCategoryRepo;

    @Test
    void updateUserActions() {
        UserAction userAction = ModelUtils.getUserAction();
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        when(userActionRepo.findById(1L)).thenReturn(Optional.of(userAction));
        when(userActionRepo.save(userAction)).thenReturn(userAction);
        when(modelMapper.map(userAction, UserActionVO.class)).thenReturn(userActionVO);
        assertEquals(userActionVO, userActionService.updateUserActions(userActionVO));
    }

    @Test
    void updateUserActionsNull() {
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        when(userActionRepo.findById(1L)).thenReturn(Optional.empty());
        assertNull(userActionService.updateUserActions(userActionVO));
    }

    @Test
    void findUserActionByUserId() {
        UserAction userAction = ModelUtils.getUserAction();
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        when(userActionRepo.findByUserIdAndAchievementCategoryId(1L, 1L)).thenReturn(userAction);
        when(modelMapper.map(userAction, UserActionVO.class)).thenReturn(userActionVO);
        assertEquals(userActionVO, userActionService.findUserActionByUserIdAndAchievementCategory(1L, 1L));
    }

    @Test
    void findUserActionByUserId_NoSuchCategory() {
        when(userActionRepo.findByUserIdAndAchievementCategoryId(1L, 1L)).thenReturn(null);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(ModelUtils.getUser()));
        assertThrows(NoSuchElementException.class,
            () -> userActionService.findUserActionByUserIdAndAchievementCategory(1L, 1L));
    }

    @Test
    void findUserActionByUserIdNull() {
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        when(userActionRepo.findByUserIdAndAchievementCategoryId(1L, 1L)).thenReturn(null);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(ModelUtils.getUser()));
        when(achievementCategoryRepo.findById(anyLong())).thenReturn(Optional.of(ModelUtils.getAchievementCategory()));
        UserAction userAction = UserAction.builder()
            .user(ModelUtils.getUser())
            .achievementCategory(AchievementCategory.builder()
                .id(1L)
                .name("HABIT")
                .achievementList(Collections.emptyList())
                .build())
            .count(0)
            .build();

        when(modelMapper.map(userAction, UserActionVO.class)).thenReturn(userActionVO);
        UserActionVO resultUserActionVO = userActionService.findUserActionByUserIdAndAchievementCategory(1L, 1L);
        assertEquals(userActionVO, resultUserActionVO);
        verify(userActionRepo).save(any());

    }

    @Test
    void save() {
        UserAction userAction = ModelUtils.getUserAction();
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        when(modelMapper.map(userActionVO, UserAction.class)).thenReturn(userAction);
        when(userActionRepo.save(userAction)).thenReturn(userAction);
        when(modelMapper.map(userAction, UserActionVO.class)).thenReturn(userActionVO);
        assertEquals(userActionVO, userActionService.save(userActionVO));
    }
}
