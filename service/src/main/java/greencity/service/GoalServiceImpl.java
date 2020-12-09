package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.goal.*;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.Goal;
import greencity.entity.HabitAssign;
import greencity.entity.UserGoal;
import greencity.entity.localization.GoalTranslation;
import greencity.enums.GoalStatus;
import greencity.exception.exceptions.*;
import greencity.filters.GoalSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserGoalRepo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GoalServiceImpl implements GoalService {
    private final GoalTranslationRepo goalTranslationRepo;
    private final GoalRepo goalRepo;
    private final ModelMapper modelMapper;
    private final UserGoalRepo userGoalRepo;
    private final HabitAssignRepo habitAssignRepo;

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
            updateTranslations(updatedGoal.getTranslations(), goalPostDto.getTranslations());
            goalRepo.save(updatedGoal);
            return modelMapper.map(updatedGoal.getTranslations(),
                new TypeToken<List<LanguageTranslationDTO>>() {
                }.getType());
        } else {
            throw new GoalNotFoundException(ErrorMessage.GOAL_NOT_FOUND_BY_ID);
        }
    }

    private void updateTranslations(List<GoalTranslation> oldTranslations,
        List<LanguageTranslationDTO> newTranslations) {
        oldTranslations.forEach(goalTranslation -> goalTranslation.setContent(newTranslations.stream()
            .filter(newTranslation -> newTranslation.getLanguage().getCode()
                .equals(goalTranslation.getLanguage().getCode()))
            .findFirst().get()
            .getContent()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoalResponseDto findGoalById(Long id) {
        Optional<Goal> goal = goalRepo.findById(id);
        if (goal.isPresent()) {
            return modelMapper.map(goal.get(), GoalResponseDto.class);
        } else {
            throw new GoalNotFoundException(ErrorMessage.GOAL_NOT_FOUND_BY_ID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long delete(Long goalId) {
        try {
            goalRepo.deleteById(goalId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotDeletedException(ErrorMessage.GOAL_NOT_DELETED);
        }
        return goalId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<GoalManagementDto> findGoalForManagementByPage(Pageable pageable) {
        Page<Goal> goals = goalRepo.findAll(pageable);
        List<GoalManagementDto> goalManagementDtos =
            goals.getContent().stream()
                .map(goal -> modelMapper.map(goal, GoalManagementDto.class))
                .collect(Collectors.toList());
        return getPagebleAdvancedDto(goalManagementDtos, goals);
    }

    private PageableAdvancedDto<GoalManagementDto> getPagebleAdvancedDto(
        List<GoalManagementDto> goalManagementDtos, Page<Goal> goals) {
        return new PageableAdvancedDto<>(
            goalManagementDtos,
            goals.getTotalElements(),
            goals.getPageable().getPageNumber(),
            goals.getTotalPages(),
            goals.getNumber(),
            goals.hasPrevious(),
            goals.hasNext(),
            goals.isFirst(),
            goals.isLast());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> deleteAllGoalByListOfId(List<Long> listId) {
        listId.forEach(this::delete);
        return listId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<GoalManagementDto> searchBy(Pageable paging, String query) {
        Page<Goal> goals = goalRepo.searchBy(paging, query);
        List<GoalManagementDto> goalManagementDtos = goals.stream()
            .map(goal -> modelMapper.map(goal, GoalManagementDto.class))
            .collect(Collectors.toList());
        return getPagebleAdvancedDto(goalManagementDtos, goals);
    }

    /**
     * * This method used for build {@link SearchCriteria} depends on
     * {@link GoalDto}.
     *
     * @param goalDto used for receive parameters for filters from UI.
     * @return {@link SearchCriteria}.
     */
    private List<SearchCriteria> buildSearchCriteria(GoalViewDto goalDto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        setValueIfNotEmpty(criteriaList, "id", goalDto.getId());
        setValueIfNotEmpty(criteriaList, "content", goalDto.getContent());
        return criteriaList;
    }

    /**
     * Returns {@link GoalSpecification} for entered filter parameters.
     *
     * @param goalViewDto contains data from filters
     */
    private GoalSpecification getSpecification(GoalViewDto goalViewDto) {
        List<SearchCriteria> searchCriteria = buildSearchCriteria(goalViewDto);
        return new GoalSpecification(searchCriteria);
    }

    /**
     * Method that adds new {@link SearchCriteria}.
     *
     * @param searchCriteria - list of existing {@link SearchCriteria}
     * @param key            - key of field
     * @param value          - value of field
     */
    private void setValueIfNotEmpty(List<SearchCriteria> searchCriteria, String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            searchCriteria.add(SearchCriteria.builder()
                .key(key)
                .type(key)
                .value(value)
                .build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<GoalManagementDto> getFilteredDataForManagementByPage(Pageable pageable,
        GoalViewDto goal) {
        Page<Goal> pages = goalRepo.findAll(getSpecification(goal), pageable);
        return getPagesFilteredPages(pages);
    }

    private PageableAdvancedDto<GoalManagementDto> getPagesFilteredPages(Page<Goal> pages) {
        List<GoalManagementDto> goalManagementDtos = pages.getContent()
            .stream()
            .map(goal -> modelMapper.map(goal, GoalManagementDto.class))
            .collect(Collectors.toList());
        return getPagebleAdvancedDto(goalManagementDtos, pages);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<UserGoalResponseDto> saveUserGoals(Long userId, Long habitId, List<GoalRequestDto> goalDtos,
        String language) {
        if (goalDtos != null) {
            saveGoalsForHabitAssign(getHabitAssignIfExist(userId, habitId), goalDtos);
        }
        return getUserGoals(userId, habitId, language);
    }

    /**
     * Method return habit assign if it exist.
     *
     * @author Dmytro Khonko
     */
    private HabitAssign getHabitAssignIfExist(Long userId, Long habitId) {
        Optional<HabitAssign> habitAssignOptional =
            habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habitId, userId);
        if (habitAssignOptional.isPresent()) {
            return habitAssignOptional.get();
        } else {
            throw new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID);
        }
    }

    /**
     * Method save user goals with goal dictionary.
     *
     * @param goals list {@link GoalRequestDto} for saving
     * @author Dmytro Khonko
     */
    private void saveGoalsForHabitAssign(HabitAssign habitAssign, List<GoalRequestDto> goals) {
        for (GoalRequestDto el : goals) {
            saveUserGoalForGoal(el, habitAssign);
        }
    }

    private void saveUserGoalForGoal(GoalRequestDto goal, HabitAssign habitAssign) {
        if (isAssignedToHabit(goal, habitAssign) && isAssignedToUser(goal, habitAssign)) {
            saveUserGoal(goal, habitAssign);
        }
    }

    private boolean isAssignedToHabit(GoalRequestDto goal, HabitAssign habitAssign) {
        List<Long> ids = userGoalRepo.getAllGoalsIdForHabit(habitAssign.getHabit().getId());
        try {
            if (ids.contains(goal.getId())) {
                return true;
            }
        } catch (Exception e) {
            throw new NotFoundException(ErrorMessage.GOAL_NOT_ASSIGNED_FOR_THIS_HABIT + goal.getId());
        }
        return false;
    }

    private boolean isAssignedToUser(GoalRequestDto goal, HabitAssign habitAssign) {
        List<Long> assignedIds = userGoalRepo.getAllAssignedGoals(habitAssign.getId());
        try {
            if (!assignedIds.contains(goal.getId())) {
                return true;
            }
        } catch (Exception e) {
            throw new WrongIdException(ErrorMessage.GOAL_ALREADY_SELECTED + goal.getId());
        }
        return false;
    }

    private void saveUserGoal(GoalRequestDto goal, HabitAssign habitAssign) {
        UserGoal userGoal = modelMapper.map(goal, UserGoal.class);
        userGoal.setHabitAssign(habitAssign);
        habitAssign.getUserGoals().add(userGoal);
        userGoalRepo.saveAll(habitAssign.getUserGoals());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<UserGoalResponseDto> getUserGoals(Long userId, Long habitId, String language) {
        List<UserGoalResponseDto> goalDtos = getAllUserGoals(getHabitAssignIfExist(userId, habitId));
        if (goalDtos.isEmpty()) {
            throw new UserHasNoGoalsException(ErrorMessage.USER_HAS_NO_GOALS);
        }
        goalDtos.forEach(el -> setTextForUserGoal(el, language));
        return goalDtos;
    }

    private List<UserGoalResponseDto> getAllUserGoals(HabitAssign habitAssign) {
        return userGoalRepo
            .findAllByHabitAssingId(habitAssign.getId())
            .stream()
            .map(userGoal -> modelMapper.map(userGoal, UserGoalResponseDto.class))
            .collect(Collectors.toList());
    }

    /**
     * Method for setting text either for UserGoal with localization.
     *
     * @param dto {@link GoalDto}
     */
    private void setTextForUserGoal(UserGoalResponseDto dto, String language) {
        String text = goalTranslationRepo.findByLangAndUserGoalId(language, dto.getId()).getContent();
        dto.setText(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUserGoalByGoalIdAndUserIdAndHabitId(Long goalId, Long userId, Long habitId) {
        userGoalRepo.deleteByGoalIdAndHabitAssignId(goalId,
            getHabitAssignByHabitIdAndUserIdAndSuspendedFalse(userId, habitId).getId());
    }

    private HabitAssign getHabitAssignByHabitIdAndUserIdAndSuspendedFalse(Long userId, Long habitId) {
        Optional<HabitAssign> habitAssign = habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habitId, userId);
        if (habitAssign.isPresent()) {
            return habitAssign.get();
        }
        throw new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserGoalResponseDto updateUserGoalStatus(Long userId, Long goalId, String language) {
        UserGoal userGoal;
        userGoal = userGoalRepo.getOne(goalId);
        if (isActive(userGoal)) {
            changeStatusToDone(userGoal);
        } else {
            throw new UserGoalStatusNotUpdatedException(
                    ErrorMessage.USER_GOAL_STATUS_IS_ALREADY_DONE + userGoal.getId());
        }
        UserGoalResponseDto updatedUserGoal = modelMapper.map(userGoal, UserGoalResponseDto.class);
        setTextForUserGoal(updatedUserGoal, language);
        return updatedUserGoal;
    }

    private boolean isActive(UserGoal userGoal) {
        try {
            if (GoalStatus.ACTIVE.equals(userGoal.getStatus())) {
                return true;
            }
        } catch (Exception e) {
            throw new UserGoalStatusNotUpdatedException(
                ErrorMessage.USER_GOAL_NOT_FOUND + userGoal.getId());
        }
        return false;
    }

    private void changeStatusToDone(UserGoal userGoal) {
        userGoal.setStatus(GoalStatus.DONE);
        userGoal.setDateCompleted(LocalDateTime.now());
        userGoalRepo.save(userGoal);
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko
     */
    @Transactional
    @Override
    public List<Long> deleteUserGoals(String ids) {
        List<Long> arrayId = Arrays.stream(ids.split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList());

        List<Long> deleted = new ArrayList<>();
        for (Long id : arrayId) {
            deleted.add(deleteUserGoal(id));
        }
        return deleted;
    }

    /**
     * Method for deleting user goal by id.
     *
     * @param id {@link UserGoal} id.
     * @return id of deleted {@link UserGoal}
     * @author Bogdan Kuzenko
     */
    private Long deleteUserGoal(Long id) {
        UserGoal userGoal = userGoalRepo
            .findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_GOAL_NOT_FOUND + id));
        userGoalRepo.delete(userGoal);
        return id;
    }
}
