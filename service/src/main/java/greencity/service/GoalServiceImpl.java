package greencity.service;

import greencity.dto.advice.AdviceTranslationVO;
import greencity.dto.advice.AdviceVO;
import greencity.dto.goal.*;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserGoalVO;
import greencity.entity.CustomGoal;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.entity.localization.AdviceTranslation;
import greencity.entity.localization.GoalTranslation;
import greencity.enums.GoalStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.GoalNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.constant.ErrorMessage;
import java.util.LinkedList;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public UserGoalResponseDto getUserGoalResponseDtoFromPredefinedGoal(UserGoalVO userGoalVO) {
        /*UserGoal userGoal = modelMapper.map(userGoalVO, UserGoal.class);
        UserGoalResponseDto userGoalResponseDto = modelMapper.map(userGoal, UserGoalResponseDto.class);
        String languageCode = languageService.extractLanguageCodeFromRequest();
        if (userGoal.getCustomGoal() == null) {
            Goal goal = goalRepo
                .findById(userGoal
                    .getGoal().getId()).orElseThrow(() -> new GoalNotFoundException(ErrorMessage.GOAL_NOT_FOUND_BY_ID));
            userGoalResponseDto.setText(goalTranslationRepo.findByGoalAndLanguageCode(goal, languageCode)
                .orElseThrow(() ->
                        new GoalNotFoundException(ErrorMessage.GOAL_NOT_FOUND_BY_LANGUAGE_CODE)).getContent());
        }
        return userGoalResponseDto;*/
        return null;
    }

    @Override
    public List<GoalTranslationVO> saveGoal(GoalPostDto goal) {
        Goal saved =  goalRepo.save(modelMapper.map(goal, Goal.class));
        List<GoalTranslationVO> list = modelMapper.map(goal.getTranslations(),
            new TypeToken<List<GoalTranslationVO>>() {
            }.getType());
        list.forEach(a -> a.setGoal(modelMapper.map(saved, GoalVO.class)));
        List<GoalTranslation> collect = modelMapper.map(list,
            new TypeToken<List<GoalTranslation>>() {
            }.getType());
        List<GoalTranslation> save = goalTranslationRepo.saveAll(collect);
        return modelMapper.map(save, new TypeToken<List<GoalTranslationVO>>() {
        }.getType());
    }

    @Override
    public List<GoalTranslationVO> update(GoalPostDto dto, Long goalId) {
        goalRepo.findById(goalId);
        return saveGoal(dto);
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
