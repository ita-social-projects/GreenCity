package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.constant.RabbitConstants;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.message.AddEcoNewsMessage;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EcoNewsServiceImpl implements EcoNewsService {
    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;

    private final EcoNewsRepo ecoNewsRepo;

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final EcoNewsTranslationRepo ecoNewsTranslationRepo;

    private final RabbitTemplate rabbitTemplate;

    private final NewsSubscriberService newsSubscriberService;

    private final LanguageService languageService;

    private final TagService tagService;

    private final FileService fileService;

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest,
                                      MultipartFile image, String email) {
        EcoNews toSave = modelMapper.map(addEcoNewsDtoRequest, EcoNews.class);
        toSave.setAuthor(userService.findByEmail(email));
        toSave.setCreationDate(ZonedDateTime.now());
        toSave.setImagePath(fileService.upload(image).toString());

        toSave.setTags(addEcoNewsDtoRequest.getTags()
            .stream()
            .map(tagService::findByName)
            .collect(Collectors.toList())
        );

        List<EcoNewsTranslation> translations = new ArrayList<>();

        for (EcoNewsTranslation translation : toSave.getTranslations()) {
            translation.setLanguage(languageService.findByCode(translation.getLanguage().getCode()));
            translation.setEcoNews(toSave);

            translations.add(translation);
        }
        toSave.setTranslations(translations);

        try {
            ecoNewsRepo.save(toSave);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED);
        }

        rabbitTemplate.convertAndSend(sendEmailTopic, RabbitConstants.ADD_ECO_NEWS_ROUTING_KEY,
            buildAddEcoNewsMessage(toSave));

        AddEcoNewsDtoResponse addEcoNewsDtoResponse = modelMapper.map(toSave, AddEcoNewsDtoResponse.class);

        EcoNewsTranslation translation = ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(toSave,
            languageService.extractLanguageCodeFromRequest());
        addEcoNewsDtoResponse.setTitle(translation.getTitle());
        addEcoNewsDtoResponse.setText(translation.getText());

        return addEcoNewsDtoResponse;
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @Cacheable(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, key = "#languageCode")
    @Override
    public List<EcoNewsDto> getThreeLastEcoNews(String languageCode) {
        List<EcoNewsTranslation> ecoNewsTranslations = ecoNewsTranslationRepo
            .getNLastEcoNewsByLanguageCode(3, languageCode);
        if (ecoNewsTranslations.isEmpty()) {
            throw new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND);
        }
        return ecoNewsTranslations
            .stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Kovaliv Taras.
     */
    @Override
    public PageableDto<EcoNewsDto> findAll(Pageable page, String languageCode) {
        Page<EcoNewsTranslation> pages = ecoNewsTranslationRepo.findAllByLanguageCode(page, languageCode);
        List<EcoNewsDto> ecoNewsDtos = pages
            .stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());
        return new PageableDto<>(
            ecoNewsDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber()
        );
    }

    /**
     * {@inheritDoc}
     *
     * @author Kovaliv Taras.
     */
    @Override
    public PageableDto<EcoNewsDto> find(Pageable page, String language, List<String> tags) {
        Page<EcoNewsTranslation> pages = ecoNewsTranslationRepo
            .find(page, tags, language);

        List<EcoNewsDto> ecoNewsDtos = pages.stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            ecoNewsDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber()
        );
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @Override
    public EcoNews findById(Long id) {
        return ecoNewsRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     *
     * @author Kovaliv Taras.
     */
    @Override
    public EcoNewsDto findById(Long id, String language) {
        EcoNews ecoNews = findById(id);
        EcoNewsTranslation ecoNewsTranslation = null;

        for (EcoNewsTranslation ent : ecoNews.getTranslations()) {
            if (ent.getLanguage().getCode().equals(language)) {
                ecoNewsTranslation = ent;
                break;
            }
        }

        if (ecoNewsTranslation == null) {
            ecoNewsTranslation = ecoNews.getTranslations().get(0);
        }

        return modelMapper.map(ecoNewsTranslation, EcoNewsDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public void delete(Long id) {
        ecoNewsRepo.deleteById(findById(id).getId());
    }

    /**
     * Method for building message for sending email about adding new eco news.
     *
     * @param ecoNews {@link EcoNews} which was added.
     * @return {@link AddEcoNewsMessage} which contains needed info about {@link EcoNews} and subscribers.
     */
    private AddEcoNewsMessage buildAddEcoNewsMessage(EcoNews ecoNews) {
        AddEcoNewsDtoResponse addEcoNewsDtoResponse = modelMapper.map(ecoNews, AddEcoNewsDtoResponse.class);
        addEcoNewsDtoResponse.setTitle(
            ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(ecoNews, AppConstant.DEFAULT_LANGUAGE_CODE).getTitle());

        return new AddEcoNewsMessage(newsSubscriberService.findAll(), addEcoNewsDtoResponse);
    }
}