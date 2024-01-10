package greencity.service;

import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDto;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactTranslationUpdateDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.habitfact.HabitFactViewDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.Habit;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.entity.Language;
import greencity.enums.FactOfDayStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.filters.HabitFactSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.HabitFactRepo;
import greencity.repository.HabitFactTranslationRepo;
import greencity.repository.HabitRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import static greencity.enums.FactOfDayStatus.CURRENT;

/**
 * Implementation of {@link HabitFactService}.
 *
 * @author Vitaliy Dzen
 */
@Service
@AllArgsConstructor
public class HabitFactServiceImpl implements HabitFactService {
    private final HabitFactRepo habitFactRepo;
    private final HabitFactTranslationRepo habitFactTranslationRepo;
    private final ModelMapper modelMapper;
    private final HabitRepo habitRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<LanguageTranslationDTO> getAllHabitFacts(Pageable page, String language) {
        Page<HabitFactTranslation> habitFactTranslation = habitFactTranslationRepo
            .findAllByLanguageCode(page, language);
        if (habitFactTranslation.getTotalPages() < page.getPageNumber()) {
            throw new BadRequestException(ErrorMessage.PAGE_INDEX_IS_MORE_THAN_TOTAL_PAGES
                + habitFactTranslation.getTotalPages());
        }
        return getPagesWithLanguageTranslationDTO(habitFactTranslation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageTranslationDTO> getAllHabitFactsList(Pageable page, String language) {
        Page<HabitFactTranslation> habitFactTranslation = habitFactTranslationRepo
            .findAllByLanguageCode(page, language);
        if (habitFactTranslation.getTotalPages() < page.getPageNumber()) {
            throw new BadRequestException(ErrorMessage.PAGE_INDEX_IS_MORE_THAN_TOTAL_PAGES
                + habitFactTranslation.getTotalPages());
        }
        return habitFactTranslation.stream()
            .map(habitFact -> modelMapper.map(habitFactTranslation, LanguageTranslationDTO.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<HabitFactVO> getAllHabitFactsVO(Pageable pageable) {
        Page<HabitFact> page = habitFactRepo.findAll(pageable);
        List<HabitFactVO> habitFactVOS = page.stream()
            .map(habitFact -> modelMapper.map(habitFact, HabitFactVO.class))
            .collect(Collectors.toList());
        return new PageableDto<>(
            habitFactVOS,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageTranslationDTO getRandomHabitFactByHabitIdAndLanguage(Long id, String language) {
        return modelMapper.map(habitFactTranslationRepo.getRandomHabitFactTranslationByHabitIdAndLanguage(language, id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id)),
            LanguageTranslationDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactDtoResponse getHabitFactById(Long id) {
        HabitFactVO habitFactVO = modelMapper.map(habitFactRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id)),
            HabitFactVO.class);
        return modelMapper.map(habitFactVO, HabitFactDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactDto getHabitFactByName(String language, String name) {
        return modelMapper.map(habitFactTranslationRepo.findFactTranslationByLanguageCodeAndContent(language, name)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + name)),
            HabitFactDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactVO save(HabitFactPostDto fact) {
        checkIfHabitExists(fact.getHabit().getId());
        HabitFact habitFact = modelMapper.map(fact, HabitFact.class);
        habitFact.getTranslations().forEach(habitFactTranslation -> {
            habitFactTranslation.setHabitFact(habitFact);
            habitFactTranslation.setFactOfDayStatus(FactOfDayStatus.POTENTIAL);
        });

        return modelMapper.map(habitFactRepo.save(habitFact), HabitFactVO.class);
    }

    private void checkIfHabitExists(Long id) {
        if (habitRepo.findById(id).isEmpty()) {
            throw new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactVO update(HabitFactUpdateDto factDto, Long id) {
        HabitFact habitFact = habitFactRepo.findById(id)
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id));
        Habit habit = habitRepo.findById(factDto.getHabit().getId())
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id));
        habitFact.setHabit(habit);
        habitFactTranslationsSetter(habitFact, factDto);
        return modelMapper.map(habitFactRepo.save(habitFact), HabitFactVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long delete(Long id) {
        if (habitFactRepo.findById(id).isEmpty()) {
            throw new NotDeletedException(ErrorMessage.HABIT_FACT_NOT_DELETED_BY_ID);
        }
        habitFactRepo.deleteById(id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteAllByHabit(HabitVO habit) {
        habitFactRepo.findAllByHabitId(habit.getId())
            .forEach(habitFact -> {
                habitFactTranslationRepo.deleteAllByHabitFact(habitFact);
                habitFactRepo.delete(habitFact);
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public List<Long> deleteAllHabitFactsByListOfId(List<Long> listId) {
        listId.forEach(id -> {
            HabitFact habitFact = habitFactRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id));
            habitFactTranslationRepo.deleteAllByHabitFact(habitFact);
            habitFactRepo.delete(habitFact);
        });
        return listId;
    }

    private PageableDto<LanguageTranslationDTO> getPagesWithLanguageTranslationDTO(Page<HabitFactTranslation> pages) {
        List<LanguageTranslationDTO> languageTranslationDTOS = pages
            .stream()
            .map(habitFactTranslation -> modelMapper.map(habitFactTranslation, LanguageTranslationDTO.class))
            .collect(Collectors.toList());
        return new PageableDto<>(
            languageTranslationDTOS,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<HabitFactVO> searchHabitFactWithFilter(Pageable paging, String filter) {
        Page<HabitFact> page = habitFactRepo.searchHabitFactByFilter(paging, filter);
        List<HabitFactVO> habitFactVOS = page.stream()
            .map(habitFact -> modelMapper.map(habitFact, HabitFactVO.class))
            .collect(Collectors.toList());
        return new PageableDto<>(
            habitFactVOS,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<HabitFactVO> getFilteredDataForManagementByPage(
        Pageable pageable,
        HabitFactViewDto habitFactViewDto) {
        Page<HabitFact> pages = habitFactRepo.findAll(getSpecification(habitFactViewDto), pageable);
        return getPagesWithHabitFactVO(pages);
    }

    /**
     * * This method used for build {@link SearchCriteria} depends on
     * {@link HabitFactViewDto}.
     *
     * @param habitFactViewDto used for receive parameters for filters from UI.
     * @return {@link SearchCriteria}.
     */
    private List<SearchCriteria> buildSearchCriteria(HabitFactViewDto habitFactViewDto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        setValueIfNotEmpty(criteriaList, "id", habitFactViewDto.getId());
        setValueIfNotEmpty(criteriaList, "habitId", habitFactViewDto.getHabitId());
        setValueIfNotEmpty(criteriaList, "content", habitFactViewDto.getContent());
        return criteriaList;
    }

    /**
     * Returns {@link HabitFactSpecification} for entered filter parameters.
     *
     * @param habitFactViewDto contains data from filters
     */
    private HabitFactSpecification getSpecification(HabitFactViewDto habitFactViewDto) {
        List<SearchCriteria> searchCriteria = buildSearchCriteria(habitFactViewDto);
        return new HabitFactSpecification(searchCriteria);
    }

    private PageableDto<HabitFactVO> getPagesWithHabitFactVO(Page<HabitFact> pages) {
        List<HabitFactVO> habitFactVOS = pages.getContent()
            .stream()
            .map(habitFact -> modelMapper.map(habitFact, HabitFactVO.class))
            .collect(Collectors.toList());
        return new PageableDto<>(
            habitFactVOS,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
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

    /**
     * {@inheritDoc}
     */
    public PageableDto<HabitFactVO> getAllHabitFactVOsWithFilter(String filter, Pageable pageable) {
        return filter == null || filter.isEmpty()
            ? getAllHabitFactsVO(pageable)
            : searchHabitFactWithFilter(pageable, filter);
    }

    /**
     * Method that adds new {@link SearchCriteria}.
     *
     * @param searchCriteria - list of existing {@link SearchCriteria}
     * @param key            - key of field
     * @param value          - value of field
     */
    private void setValueIfNotEmpty(List<SearchCriteria> searchCriteria, String key, String value) {
        if (StringUtils.hasLength(value)) {
            searchCriteria.add(SearchCriteria.builder()
                .key(key)
                .type(key)
                .value(value)
                .build());
        }
    }

    private void habitFactTranslationsSetter(HabitFact habitFact, HabitFactUpdateDto factDto) {
        habitFact.getTranslations()
            .forEach(habitFactTranslation -> {
                Optional<HabitFactTranslationUpdateDto> content = factDto.getTranslations().stream()
                    .filter(newTranslation -> newTranslation.getLanguage().getCode()
                        .equals(habitFactTranslation.getLanguage().getCode()))
                    .findFirst();
                content.ifPresent(habitFactTranslationUpdateDto -> habitFactTranslation
                    .setContent(habitFactTranslationUpdateDto.getContent()));
                habitFactTranslation.setFactOfDayStatus(factDto.getTranslations().get(0).getFactOfDayStatus());
            });
    }
}
