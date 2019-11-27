package greencity.service.impl;

import greencity.dto.goal.GoalDto;
import greencity.repository.GoalRepo;
import greencity.service.GoalService;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoalServiceImpl implements GoalService {
    private final GoalRepo goalRepo;
    private final ModelMapper modelMapper;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public GoalServiceImpl(GoalRepo goalRepo, ModelMapper modelMapper) {
        this.goalRepo = goalRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GoalDto> findAll() {
        return goalRepo
            .findAll()
            .stream()
            .map(goal -> modelMapper.map(goal, GoalDto.class))
            .collect(Collectors.toList());
    }
}
