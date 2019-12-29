package greencity.service.impl;

import greencity.dto.goal.GoalDto;
import greencity.repository.GoalTranslationRepo;
import greencity.service.GoalService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoalServiceImpl implements GoalService {
    private final GoalTranslationRepo goalTranslationRepo;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public GoalServiceImpl(GoalTranslationRepo goalTranslationRepo) {
        this.goalTranslationRepo = goalTranslationRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GoalDto> findAll(String language) {
        return goalTranslationRepo
            .findAllByLanguageCode(language)
            .stream()
            .map(g -> new GoalDto(g.getGoal().getId(), g.getText()))
            .collect(Collectors.toList());
    }
}
