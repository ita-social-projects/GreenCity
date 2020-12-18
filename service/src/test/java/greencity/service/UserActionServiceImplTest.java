package greencity.service;

import greencity.ModelUtils;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.UserAction;
import greencity.repository.UserActionRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserActionServiceImplTest {
    @InjectMocks
    private UserActionServiceImpl userActionService;
    @Mock
    private UserActionRepo userActionRepo;
    @Mock
    private ModelMapper modelMapper;

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
    void save() {
        UserAction userAction = ModelUtils.getUserAction();
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        when(modelMapper.map(userActionVO, UserAction.class)).thenReturn(userAction);
        when(userActionRepo.save(userAction)).thenReturn(userAction);
        when(modelMapper.map(userAction, UserActionVO.class)).thenReturn(userActionVO);
        assertEquals(userActionVO, userActionService.save(userActionVO));
    }
}
