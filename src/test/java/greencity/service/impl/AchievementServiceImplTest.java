package greencity.service.impl;

import greencity.dto.achievement.AchievementDTO;
import greencity.entity.Achievement;
import greencity.repository.AchievementRepo;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AchievementServiceImplTest {

    @Mock
    private AchievementRepo achievementRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private AchievementServiceImpl achievementService;

    @Test
    public void findAllWithEmptyListTest() {
        when(achievementRepo.findAll()).thenReturn(Collections.emptyList());
        List<AchievementDTO> findAllResult = achievementService.findAll();
        assertTrue(findAllResult.isEmpty());
    }

    @Test
    public void findAllWithOneValueInRepoTest() {
        Achievement achievement = new Achievement(1L, "foo", null, null, null);
        when(achievementRepo.findAll())
            .thenReturn(Collections.singletonList(achievement));
        when(modelMapper.map(achievement, AchievementDTO.class))
            .thenReturn(new AchievementDTO(achievement.getId(), achievement.getTitle(), null, null));
        List<AchievementDTO> findAllResult = achievementService.findAll();
        assertEquals("foo", findAllResult.get(0).getTitle());
        assertEquals(1L, (long) findAllResult.get(0).getId());
    }
}
