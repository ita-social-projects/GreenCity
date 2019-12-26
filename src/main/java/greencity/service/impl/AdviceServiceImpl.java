package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.advice.AdviceDTO;
import greencity.dto.advice.AdvicePostDTO;
import greencity.entity.Advice;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AdviceRepo;
import greencity.repository.HabitDictionaryRepo;
import greencity.service.AdviceService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AdviceService}.
 *
 * @author Vitaliy Dzen
 */
@Service
@AllArgsConstructor
public class AdviceServiceImpl implements AdviceService {
    private AdviceRepo adviceRepo;
    private HabitDictionaryRepo habitDictionaryRepo;

    @Autowired
    private final ModelMapper modelMapper;

    /**
     * Method finds all {@link Advice}.
     *
     * @return List of all {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public List<AdviceDTO> getAllAdvices() {
        return modelMapper.map(adviceRepo.findAll(), new TypeToken<List<AdviceDTO>>() {
        }.getType());
    }

    /**
     * Method finds random {@link Advice}.
     *
     * @return random {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public AdviceDTO getRandomAdviceByHabitId(Long id) {
        return modelMapper.map(adviceRepo.getRandomAdviceByHabitId(id).orElseThrow(() ->
            new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)), AdviceDTO.class);
    }

    /**
     * Method find {@link Advice} by id.
     *
     * @param id of {@link Advice}
     * @return {@link AdviceDTO}
     * @author Vitaliy Dzen
     */
    @Override
    public AdviceDTO getAdviceById(Long id) {
        return modelMapper.map(adviceRepo.findById(id).orElseThrow(() ->
            new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)), AdviceDTO.class);
    }

    /**
     * Method find {@link Advice} by advice.
     *
     * @param name of {@link Advice}
     * @return {@link AdviceDTO}
     * @author Vitaliy Dzen
     */
    @Override
    public AdviceDTO getAdviceByName(String name) {
        return modelMapper.map(adviceRepo.findAdviceByAdvice(name).orElseThrow(() ->
            new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_NAME + name)), AdviceDTO.class);
    }

    /**
     * Method saves new {@link Advice}.
     *
     * @param advicePostDTO {@link AdviceDTO}
     * @return instance of {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public Advice save(AdvicePostDTO advicePostDTO) {
        if (adviceRepo.findAdviceByAdvice(advicePostDTO.getAdvice()).isPresent()) {
            throw new NotSavedException(ErrorMessage.ADVICE_NOT_SAVED_BY_NAME);
        }
        return adviceRepo.save(modelMapper.map(advicePostDTO, Advice.class));
    }

    /**
     * Method updates {@link Advice}.
     *
     * @param advice {@link AdviceDTO} Object
     * @return instance of {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public Advice update(AdvicePostDTO advice, Long id) {
        return adviceRepo.findById(id)
            .map(employee -> {
                employee.setHabitDictionary(habitDictionaryRepo.findById(advice.getHabitDictionary().getId()).get());
                employee.setAdvice(advice.getAdvice());
                return adviceRepo.save(employee);
            })
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.ADVICE_NOT_UPDATED));
    }

    /**
     * Method delete {@link Advice} by id.
     *
     * @param id Long
     * @return id of deleted {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public Long delete(Long id) {
        if (!(adviceRepo.findById(id).isPresent())) {
            throw new NotDeletedException(ErrorMessage.ADVICE_NOT_DELETED);
        }
        adviceRepo.deleteById(id);
        return id;
    }
}
