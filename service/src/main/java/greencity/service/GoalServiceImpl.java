package greencity.service;

import greencity.dto.goal.*;
import greencity.entity.Goal;
import greencity.entity.localization.GoalTranslation;
import greencity.exception.exceptions.GoalNotFoundException;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
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

    private List<GoalTranslationVO> saveTranslations(GoalPostDto goalPostDto, Goal goal) {
        List<GoalTranslationVO> list = modelMapper.map(goalPostDto.getTranslations(),
            new TypeToken<List<GoalTranslationVO>>() {
            }.getType());
        list.forEach(a -> a.setGoal(modelMapper.map(goal, GoalVO.class)));
        List<GoalTranslation> collect = modelMapper.map(list,
            new TypeToken<List<GoalTranslation>>() {
            }.getType());
        List<GoalTranslation> save = goalTranslationRepo.saveAll(collect);
        return modelMapper.map(save, new TypeToken<List<GoalTranslationVO>>() {
        }.getType());
    }

    @Override
    public List<GoalTranslationVO> update(GoalPostDto goalPostDto) {
        Goal updated = goalRepo.findById(goalPostDto.getGoal().getId()).get();
        goalTranslationRepo.deleteAll(updated.getTranslations());
        return modelMapper.map(saveTranslations(goalPostDto, updated), new TypeToken<List<GoalTranslationVO>>() {
        }.getType());
    }

    @Override
    public void delete(Long goalId) {
        try {
            goalRepo.findById(goalId);
            goalRepo.deleteById(goalId);
        } catch (Exception e) {
            throw new GoalNotFoundException("goal doesn`t exist");
        }
    }
}
