package greencity.service;

import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.repository.UserActionRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActionServiceImplTest {
    @InjectMocks
    private UserActionServiceImpl userActionService;
    @Mock
    private UserActionRepo userActionRepo;
    @Mock
    private AchievementServiceImpl achievementService;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void log() {
        when(userActionRepo.existsByUserAndActionTypeAndActionId(any(), any(), any())).thenReturn(false);

        userActionService.log(new User(), null, null);
        userActionService.log(new UserVO(), null, null);

        verify(userActionRepo, times(2)).saveAndFlush(any());
        verify(achievementService, times(2)).tryToGiveUserAchievementsByActionType(any(), any());
    }
}
