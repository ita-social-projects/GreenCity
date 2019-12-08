package greencity.service.impl;

import static greencity.constant.ErrorMessage.*;

import greencity.dto.goal.*;
import greencity.entity.CustomGoal;
import greencity.entity.User;
import greencity.exception.exceptions.CustomGoalNotSavedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomGoalRepo;
import greencity.service.CustomGoalService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class provides implementation of the {@code CustomGoalService}.
 */
@Service
@AllArgsConstructor
public class CustomGoalServiceImpl implements CustomGoalService {
    /**
     * Autowired repository.
     */
    private CustomGoalRepo customGoalRepo;

    /**
     * Autowired mapper.
     */
    private ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<CustomGoalResponseDto> save(BulkSaveCustomGoalDto bulkSave, User user) {
        List<CustomGoalSaveRequestDto> dto = bulkSave.getCustomGoalSaveRequestDtoList();
        List<String> errorMessages = new ArrayList<>();
        for (CustomGoalSaveRequestDto el : dto) {
            CustomGoal customGoal = modelMapper.map(el, CustomGoal.class);
            List<CustomGoal> duplicate = user.getCustomGoals().stream()
                .filter(o -> o.getText().equals(customGoal.getText())).collect(Collectors.toList());
            if (duplicate.isEmpty()) {
                customGoal.setUser(user);
                user.getCustomGoals().add(customGoal);
            } else {
                errorMessages.add(customGoal.getText());
            }
        }
        customGoalRepo.saveAll(user.getCustomGoals());
        if (!errorMessages.isEmpty()) {
            throw new CustomGoalNotSavedException(CUSTOM_GOAL_WHERE_NOT_SAVED + errorMessages.toString());
        }
        return user.getCustomGoals().stream()
            .map(customGoal -> modelMapper.map(customGoal, CustomGoalResponseDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<CustomGoalResponseDto> findAll() {
        return customGoalRepo.findAll().stream()
            .map(customGoal -> modelMapper.map(customGoal, CustomGoalResponseDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public CustomGoalResponseDto findById(Long id) {
        CustomGoal customGoal = customGoalRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(CUSTOM_GOAL_NOT_FOUND_BY_ID + " " + id));
        return modelMapper.map(customGoal, CustomGoalResponseDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<CustomGoalResponseDto> updateBulk(BulkCustomGoalDto bulkCustomGoalDto) {
        List<CustomGoalResponseDto> updatable = bulkCustomGoalDto.getCustomGoals();
        List<CustomGoalResponseDto> returned = new ArrayList<>();
        for (CustomGoalResponseDto el : updatable) {
            returned.add(update(el));
        }
        return returned;
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<CustomGoalResponseDto> findAllByUser(Long userId) {
        List<CustomGoalResponseDto> customGoals = customGoalRepo.findAllByUserId(userId).stream()
            .map(customGoal -> modelMapper.map(customGoal, CustomGoalResponseDto.class))
            .collect(Collectors.toList());
        if (!customGoals.isEmpty()) {
            return customGoals;
        } else {
            throw new NotFoundException(CUSTOM_GOAL_NOT_FOUND);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko.
     */
    @Transactional
    @Override
    public List<Long> bulkDelete(BulkDeleteCustomGoalDto dto) {
        List<CustomGoalRequestDto> cgDto = dto.getCustomGoals();
        List<Long> deleted = new ArrayList<>();
        for (CustomGoalRequestDto el : cgDto) {
            deleted.add(delete(el.getId()));
        }
        return deleted;
    }

    /**
     * Method for deleting custom goal by id.
     *
     * @param id {@link CustomGoal} id.
     * @return id of deleted {@link CustomGoal}
     * @author Bogdan Kuzenko.
     */
    private Long delete(Long id) {
        customGoalRepo.delete(findOne(id));
        return id;
    }

    /**
     * Method for get one custom goal by id.
     *
     * @param id a value of {@link Long}
     * @return {@link CustomGoal}
     * @author Bogdan Kuzenko.
     */
    private CustomGoal findOne(Long id) {
        return customGoalRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(CUSTOM_GOAL_NOT_FOUND_BY_ID + " " + id));
    }

    /**
     * Method for update one object of user custom goal.
     *
     * @param dto object for update.
     * @return {@link CustomGoalResponseDto}
     * @author Bogdan Kuzenko.
     */
    private CustomGoalResponseDto update(CustomGoalResponseDto dto) {
        CustomGoal updatable = findOne(dto.getId());
        List<CustomGoal> duplicate = updatable.getUser().getCustomGoals()
            .stream().filter(o -> o.getText().equals(dto.getText())).collect(Collectors.toList());
        if (duplicate.isEmpty()) {
            updatable.setText(dto.getText());
        } else {
            throw new CustomGoalNotSavedException(CUSTOM_GOAL_WHERE_NOT_SAVED + dto.getText());
        }
        return modelMapper.map(updatable, CustomGoalResponseDto.class);
    }
}
