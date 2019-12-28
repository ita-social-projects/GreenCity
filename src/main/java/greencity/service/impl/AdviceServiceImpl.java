package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.advice.AdviceDTO;
import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.advice.AdviceTranslationDTO;
import greencity.entity.Advice;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AdviceRepo;
import greencity.repository.AdviceTranslationRepo;
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
    private AdviceTranslationRepo adviceTranslationRepo;

    @Autowired
    private final ModelMapper modelMapper;

    /**
     * Method finds all {@link Advice}.
     *
     * @return List of all {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public List<AdviceTranslationDTO> getAllAdvices() {
        return modelMapper.map(adviceTranslationRepo.findAll(), new TypeToken<List<AdviceTranslationDTO>>() {
        }.getType());
    }

    /**
     * Method finds random {@link Advice}.
     *
     * @return random {@link Advice}
     * @author Vitaliy Dzen
     */
    @Override
    public AdviceTranslationDTO getRandomAdviceByHabitIdAndLanguage(Long id, String language) {
        return modelMapper.map(adviceTranslationRepo.getRandomAdviceTranslationByHabitIdAndLanguage(language, id)
            .orElseThrow(() ->
                new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)), AdviceTranslationDTO.class);
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
     * Method find {@link Advice} by content.
     *
     * @param name of {@link Advice}
     * @return {@link AdviceDTO}
     * @author Vitaliy Dzen
     */
    @Override
    public AdviceDTO getAdviceByName(String language, String name) {
        return modelMapper.map(adviceTranslationRepo
            .findAdviceTranslationByLanguage_CodeAndAdvice(language, name).orElseThrow(() ->
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
