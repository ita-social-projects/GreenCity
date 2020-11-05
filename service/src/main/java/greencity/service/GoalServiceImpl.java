package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.goal.*;
import greencity.entity.Goal;
import greencity.entity.localization.GoalTranslation;
import greencity.exception.exceptions.GoalNotFoundException;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GoalServiceImpl implements GoalService {
    private final GoalTranslationRepo goalTranslationRepo;
    private final GoalRepo goalRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GoalDto> findAll(String language) {
        return goalTranslationRepo
            .findAllByLanguageCode(language)
            .stream()
            .map(g -> modelMapper.map(g, GoalDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<GoalTranslationVO> saveGoal(GoalPostDto goal) {
        Goal saved =  goalRepo.save(modelMapper.map(goal, Goal.class));
        return saveTranslations(goal, saved);
    }

    public List<GoalTranslationVO> saveTranslations(GoalPostDto goalPostDto, Goal goal) {
        List<GoalTranslation> translations = modelMapper.map(goalPostDto.getTranslations(),
            new TypeToken<List<GoalTranslation>>() {
            }.getType());
        translations.forEach(a -> a.setGoal(goal));
        List<GoalTranslation> save = goalTranslationRepo.saveAll(translations);
        return modelMapper.map(save, new TypeToken<List<GoalTranslationVO>>() {
        }.getType());
    }

    @Override
    public List<GoalTranslationVO> update(GoalPostDto goalPostDto) {
        Optional<Goal> optionalGoal = goalRepo.findById(goalPostDto.getGoal().getId());
        if (optionalGoal.isPresent()) {
            Goal updated = optionalGoal.get();
            goalTranslationRepo.deleteAll(updated.getTranslations());
            return saveTranslations(goalPostDto, updated);
        } else {
            throw new GoalNotFoundException(ErrorMessage.GOAL_NOT_FOUND_BY_ID);
        }
    }

    @Override
    public void delete(Long goalId) {
        if (goalRepo.findById(goalId).isPresent()) {
            goalRepo.deleteById(goalId);
        } else {
            throw new GoalNotFoundException(ErrorMessage.GOAL_NOT_FOUND_BY_ID);
        }
    }
}
