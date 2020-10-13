package greencity.service.impl;

import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.entity.FactOfTheDay;
import greencity.entity.FactOfTheDayTranslation;
import greencity.entity.Language;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.FactOfTheDayRepo;
import greencity.service.FactOfTheDayService;
import greencity.service.FactOfTheDayTranslationService;
import greencity.service.LanguageService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link FactOfTheDayService}.
 *
 * @author Mykola Lehkyi
 */
@AllArgsConstructor
@EnableCaching
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FactOfTheDayServiceImpl implements FactOfTheDayService {
    private final FactOfTheDayRepo factOfTheDayRepo;
    private final ModelMapper modelMapper;
    private final LanguageService languageService;
    private final FactOfTheDayTranslationService factOfTheDayTranslationService;
    private final PlaceCommentServiceImpl placeCommentServiceImpl;
    @Resource
    private FactOfTheDayService self;

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
    public FactOfTheDayPostDTO saveFactOfTheDayAndTranslations(FactOfTheDayPostDTO factPost) {
        FactOfTheDay factOfTheDay = FactOfTheDay.builder()
            .name(factPost.getName())
            .factOfTheDayTranslations(
                factPost.getFactOfTheDayTranslations().stream()
                    .map(el -> FactOfTheDayTranslation.builder()
                        .content(el.getContent())
                        .language(modelMapper.map(languageService.findByCode(el.getLanguageCode()), Language.class))
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
                    .map(el ->
                        FactOfTheDayTranslation.builder()
                        .content(el.getContent())
                        .language(modelMapper.map(languageService.findByCode(el.getLanguageCode()), Language.class))
                        .build()
                    ).collect(Collectors.toList())
            )
            .createDate(ZonedDateTime.now())
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

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<FactOfTheDayDTO> searchBy(Pageable pageable, String searchQuery) {
        Page<FactOfTheDay> factsOfTheDay = factOfTheDayRepo.searchBy(pageable, searchQuery);
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
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = CacheConstants.FACT_OF_THE_DAY_CACHE_NAME)
    public FactOfTheDay getRandomFactOfTheDay() {
        return factOfTheDayRepo.getRandomFactOfTheDay().orElseThrow(() ->
            new NotFoundException(ErrorMessage.FACT_OF_THE_DAY_NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactOfTheDayTranslationDTO getRandomFactOfTheDayByLanguage(String languageCode) {
        FactOfTheDay factOfTheDay = self.getRandomFactOfTheDay();
        FactOfTheDayTranslation factOfTheDayTranslation = factOfTheDay.getFactOfTheDayTranslations()
            .stream()
            .filter(fact -> fact.getLanguage().getCode().equals(languageCode))
            .findAny()
            .orElseThrow(() -> new NotFoundException(ErrorMessage.FACT_OF_THE_DAY_NOT_FOUND));
        return modelMapper.map(factOfTheDayTranslation, FactOfTheDayTranslationDTO.class);
    }
}
