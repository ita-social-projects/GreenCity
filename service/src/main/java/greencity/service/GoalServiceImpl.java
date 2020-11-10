package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.goal.*;
import greencity.dto.language.LanguageTranslationDTO;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageTranslationDTO> saveGoal(GoalPostDto goal) {
        Goal savedGoal = modelMapper.map(goal, Goal.class);
        savedGoal.getTranslations().forEach(a -> a.setGoal(savedGoal));
        goalRepo.save(savedGoal);
        return modelMapper.map(savedGoal.getTranslations(),
            new TypeToken<List<LanguageTranslationDTO>>() {
            }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageTranslationDTO> update(GoalPostDto goalPostDto) {
        Optional<Goal> optionalGoal = goalRepo.findById(goalPostDto.getGoal().getId());
        if (optionalGoal.isPresent()) {
            Goal updatedGoal = optionalGoal.get();
            List<GoalTranslation> translations = modelMapper.map(goalPostDto.getTranslations(),
                new TypeToken<List<GoalTranslation>>() {
                }.getType());
            translations.forEach(a -> a.setGoal(updatedGoal));
            updatedGoal.getTranslations().clear();
            updatedGoal.getTranslations().addAll(translations);
            goalRepo.save(updatedGoal);
            return modelMapper.map(updatedGoal.getTranslations(),
                new TypeToken<List<LanguageTranslationDTO>>() {
                }.getType());
        } else {
            throw new GoalNotFoundException(ErrorMessage.GOAL_NOT_FOUND_BY_ID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long goalId) {
        if (goalRepo.findById(goalId).isPresent()) {
            goalRepo.deleteById(goalId);
        } else {
            throw new GoalNotFoundException(ErrorMessage.GOAL_NOT_FOUND_BY_ID);
        }
    }
}
