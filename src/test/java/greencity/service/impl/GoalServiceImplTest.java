package greencity.service.impl;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import greencity.dto.goal.GoalDto;
import greencity.entity.Goal;
import greencity.repository.GoalRepo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

@RunWith(MockitoJUnitRunner.class)
public class GoalServiceImplTest {

    @Mock
    GoalRepo goalRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GoalServiceImpl goalService;

    @Test
    public void findAllTest() {
        List<Goal> goals = new ArrayList<>(Arrays.asList(new Goal(), new Goal()));
        List<GoalDto> goalsDto = goals
            .stream()
            .map(goal -> modelMapper.map(goal, GoalDto.class))
            .collect(Collectors.toList());
        when(goalRepo.findAll()).thenReturn(goals);
        assertEquals(goalService.findAll(), goalsDto);
    }
}