package greencity.service;

import static greencity.enums.FactOfDayStatus.CURRENT;

import greencity.constant.CacheConstants;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.entity.Language;
import greencity.enums.FactOfDayStatus;
import greencity.repository.HabitFactTranslationRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link HabitFactTranslationService}.
 *
 * @author Vitaliy Dzen
 */
@EnableCaching
@Service
@AllArgsConstructor
public class HabitFactTranslationServiceImpl implements HabitFactTranslationService {
    private final HabitFactTranslationRepo habitFactTranslationRepo;
    private final HabitFactService habitFactService;
    private final ModelMapper modelMapper;

    /**
     * Method saves new {@link HabitFact} and list of new
     * {@link HabitFactTranslation} with relationship to {@link HabitFact}.
     *
     * @param habitFactPostDTO {@link HabitFactPostDto}.
     * @return List of {@link HabitFactTranslationVO}.
     * @author Vitaliy Dzen.
     */
    @Override
    public HabitFactDtoResponse saveHabitFactAndFactTranslation(HabitFactPostDto habitFactPostDTO) {
        HabitFactVO habitFactVO = habitFactService.save(habitFactPostDTO);
        HabitFact habitFact = modelMapper.map(habitFactVO, HabitFact.class);
        List<HabitFactTranslation> habitFactTranslations = modelMapper.map(habitFactPostDTO.getTranslations(),
            new TypeToken<List<HabitFactTranslation>>() {
            }.getType());
        habitFactTranslations.forEach(a -> {
            a.setHabitFact(habitFact);
            a.setFactOfDayStatus(FactOfDayStatus.POTENTIAL);
        });
        List<HabitFactTranslationVO> habitFactTranslationVOS = habitFactTranslations
            .stream().map(habitFactTranslation -> modelMapper
                .map(habitFactTranslation, HabitFactTranslationVO.class))
            .collect(Collectors.toList());
        habitFactVO.setTranslations(saveHabitFactTranslation(habitFactTranslationVOS));
        return modelMapper.map(habitFactVO, HabitFactDtoResponse.class);
    }

    /**
     * Method saves new {@link HabitFactTranslation}.
     *
     * @param habitFactTranslationVOS {@link HabitFactTranslationVO}.
     * @return List of {@link HabitFactTranslationVO}.
     * @author Vitaliy Dzen.
     */
    @Override
    public List<HabitFactTranslationVO> saveHabitFactTranslation(List<HabitFactTranslationVO> habitFactTranslationVOS) {
        List<HabitFactTranslation> habitFactTranslations = habitFactTranslationVOS
            .stream().map(habitFactTranslation -> modelMapper.map(
                habitFactTranslation, HabitFactTranslation.class))
            .collect(Collectors.toList());
        return habitFactTranslationRepo.saveAll(habitFactTranslations)
            .stream().map(habitFactTranslation -> modelMapper
                .map(habitFactTranslation, HabitFactTranslationVO.class))
            .collect(Collectors.toList());
    }

    /**
     * Method to get today's {@link HabitFact} of day by {@link Language} id.
     *
     * @param languageId id of {@link Language} of the {@link HabitFact}.
     * @return {@link LanguageTranslationDTO} of today's {@link HabitFact} of day.
     */
    @Cacheable(value = CacheConstants.HABIT_FACT_OF_DAY_CACHE)
    @Override
    public LanguageTranslationDTO getHabitFactOfTheDay(Long languageId) {
        return modelMapper.map(
            habitFactTranslationRepo.findAllByFactOfDayStatusAndLanguageId(CURRENT, languageId),
            LanguageTranslationDTO.class);
    }
}
