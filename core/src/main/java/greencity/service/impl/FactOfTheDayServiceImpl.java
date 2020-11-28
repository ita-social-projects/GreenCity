package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.entity.FactOfTheDay;
import greencity.entity.FactOfTheDayTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.FactOfTheDayRepo;
import greencity.service.FactOfTheDayService;
import greencity.service.FactOfTheDayTranslationService;
import greencity.service.LanguageService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link FactOfTheDayService}.
 *
 * @author Mykola Lehkyi
 */
@Service
@EnableCaching
@AllArgsConstructor
public class FactOfTheDayServiceImpl implements FactOfTheDayService {
    private FactOfTheDayRepo factOfTheDayRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    LanguageService languageService;
    @Autowired
    FactOfTheDayTranslationService factOfTheDayTranslationService;
    @Autowired
    private PlaceCommentServiceImpl placeCommentServiceImpl;

    /**
     * Constructor with parameters.
     *
     * @author Mykola Lehkyi
     */
    public FactOfTheDayServiceImpl(FactOfTheDayRepo factOfTheDayRepo) {
        this.factOfTheDayRepo = factOfTheDayRepo;
    }

    /**
     * Method finds all {@link FactOfTheDay} with pageable configuration.
     *
     * @param pageable {@link Pageable}
     * @return {@link PageableDto} with list of all {@link FactOfTheDayDTO}
     * @author Mykola Lehkyi
     */
    @Override
    public PageableDto<FactOfTheDayDTO> getAllFactsOfTheDay(Pageable pageable) {
        Page<FactOfTheDay> factsOfTheDay = factOfTheDayRepo.findAll(pageable);
        List<FactOfTheDayDTO> factOfTheDayDTOs =
            factsOfTheDay.getContent().stream()
                .map(factOfTheDay -> modelMapper.map(factOfTheDay, FactOfTheDayDTO.class))
                .collect(Collectors.toList());
        return new PageableDto<>(
            factOfTheDayDTOs,
            factsOfTheDay.getTotalElements(),
            factsOfTheDay.getPageable().getPageNumber(),
            factsOfTheDay.getTotalPages());
    }

    /**
     * Method find {@link FactOfTheDay} by id.
     *
     * @param id of {@link FactOfTheDay}
     * @return {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    @Override
    public FactOfTheDayDTO getFactOfTheDayById(Long id) {
        return modelMapper.map(factOfTheDayRepo.findById(id).orElseThrow(() ->
            new NotFoundException(ErrorMessage.FACT_OF_THE_DAY_NOT_FOUND)), FactOfTheDayDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FactOfTheDay> getAllFactOfTheDayByName(String name) {
        return factOfTheDayRepo.findAllByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactOfTheDayPostDTO saveFactOfTheDayAndTranslations(FactOfTheDayPostDTO factPost) {
        FactOfTheDay factOfTheDay = FactOfTheDay.builder()
            .name(factPost.getName())
            .factOfTheDayTranslations(
                factPost.getFactOfTheDayTranslations().stream()
                    .map(el -> FactOfTheDayTranslation.builder()
                        .content(el.getContent())
                        .language(languageService.findByCode(el.getLanguageCode()))
                        .build()
                    ).collect(Collectors.toList())
            )
            .build();
        factOfTheDay.getFactOfTheDayTranslations().forEach(el -> el.setFactOfTheDay(factOfTheDay));
        factOfTheDayRepo.save(factOfTheDay);
        factOfTheDayTranslationService.saveAll(factOfTheDay.getFactOfTheDayTranslations());
        return factPost;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactOfTheDayPostDTO updateFactOfTheDayAndTranslations(FactOfTheDayPostDTO factPost) {
        FactOfTheDay factOfTheDayFromDB =
            factOfTheDayRepo.findById(factPost.getId()).orElseThrow(() -> new NotUpdatedException(
                ErrorMessage.FACT_OF_THE_DAY_NOT_UPDATED));
        factOfTheDayTranslationService.deleteAll(factOfTheDayFromDB.getFactOfTheDayTranslations());
        FactOfTheDay factOfTheDay = FactOfTheDay.builder()
            .id(factPost.getId())
            .name(factPost.getName())
            .factOfTheDayTranslations(
                factPost.getFactOfTheDayTranslations().stream()
                    .map(el -> FactOfTheDayTranslation.builder()
                        .content(el.getContent())
                        .language(languageService.findByCode(el.getLanguageCode()))
                        .build()
                    ).collect(Collectors.toList())
            )
            .build();
        factOfTheDay.getFactOfTheDayTranslations().forEach(el -> el.setFactOfTheDay(factOfTheDay));
        factOfTheDayRepo.save(factOfTheDay);
        factOfTheDayTranslationService.saveAll(factOfTheDay.getFactOfTheDayTranslations());
        return factPost;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactOfTheDay update(FactOfTheDayPostDTO fact) {
        FactOfTheDay factOfTheDay = factOfTheDayRepo.findById(fact.getId()).orElseThrow(() -> new NotUpdatedException(
            ErrorMessage.FACT_OF_THE_DAY_NOT_UPDATED));
        return factOfTheDayRepo.save(factOfTheDay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long deleteFactOfTheDayAndTranslations(Long id) {
        FactOfTheDay factOfTheDay = factOfTheDayRepo.findById(id).orElseThrow(() -> new NotUpdatedException(
            ErrorMessage.FACT_OF_THE_DAY_NOT_DELETED));
        factOfTheDayRepo.deleteById(id);
        factOfTheDayTranslationService.deleteAll(factOfTheDay.getFactOfTheDayTranslations());
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> deleteAllFactOfTheDayAndTranslations(List<Long> listId) {
        listId.forEach(id -> {
            FactOfTheDay factOfTheDay = factOfTheDayRepo.findById(id).orElseThrow(() -> new NotUpdatedException(
                ErrorMessage.FACT_OF_THE_DAY_NOT_DELETED));
            factOfTheDayRepo.deleteById(id);
            factOfTheDayTranslationService.deleteAll(factOfTheDay.getFactOfTheDayTranslations());
        });
        return listId;
    }
}
