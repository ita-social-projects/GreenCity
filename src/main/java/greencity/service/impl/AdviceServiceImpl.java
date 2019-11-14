package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.advice.AdviceAdminDTO;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.advice.AllAdvicesDTO;
import greencity.entity.Advice;
import greencity.exception.NotFoundException;
import greencity.exception.NotSavedException;
import greencity.repository.AdviceRepo;
import greencity.repository.HabitDictionaryRepo;
import greencity.service.AdviceService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
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

    /**
     * Method finds all {@link Advice}.
     *
     * @return List of all {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public List<AllAdvicesDTO> getAllAdvices() {
        return adviceRepo.findAll().stream().map(AllAdvicesDTO::new).collect(Collectors.toList());
    }

    /**
     * Method finds random {@link Advice}.
     *
     * @return random {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public AdviceDto getRandomAdviceByHabitId(Long id) {
        return new AdviceDto(adviceRepo.getRandomAdviceByHabitId(id).orElseThrow(()
            -> new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)));
    }

    /**
     * Method find {@link Advice} by id.
     *
     * @param id of {@link Advice}
     * @return {@link AdviceAdminDTO}
     * @author Vitaliy Dzen
     */
    @Override
    public AdviceAdminDTO getAdviceById(Long id) {
        return new AdviceAdminDTO(adviceRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)));
    }

    /**
     * Method find {@link Advice} by name.
     *
     * @param name of {@link Advice}
     * @return {@link AdviceAdminDTO}
     * @author Vitaliy Dzen
     */
    @Override
    public AdviceAdminDTO getAdviceByName(String name) {
        return new AdviceAdminDTO(adviceRepo.findAdviceByName(name)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + name)));
    }

    /**
     * Method saves new {@link Advice}.
     *
     * @param advice {@link AdviceDto}
     * @return instance of {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public Advice save(AdvicePostDTO advice) {
        if (adviceRepo.findAdviceByName(advice.getName()).isPresent()) {
            throw new NotSavedException(ErrorMessage.ADVICE_NOT_SAVED_BY_NAME);
        }
        return adviceRepo.save(new Advice(advice, habitDictionaryRepo.findById(advice.getHabitDictionaryId())
            .orElseThrow(() -> new NotSavedException(ErrorMessage.ADVICE_NOT_SAVED_BY_NAME)))
           );
    }

    /**
     * Method updates {@link Advice}.
     *
     * @param advice {@link AdviceAdminDTO} Object
     * @return instance of {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public Advice update(AdviceAdminDTO advice, Long id) {
        return adviceRepo.findById(id)
            .map(employee -> {
                employee.setHabitDictionary(habitDictionaryRepo.findById(advice.getHabitDictionaryId()).get());
                employee.setName(advice.getName());
                return adviceRepo.save(employee);
            })
            .orElseThrow(() -> new NotSavedException(ErrorMessage.ADVICE_NOT_SAVED_BY_NAME));
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
            throw new NotSavedException(ErrorMessage.ADVICE_NOT_SAVED_BY_NAME);
        }
        adviceRepo.deleteById(id);
        return id;
    }
}
