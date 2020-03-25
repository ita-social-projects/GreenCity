package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.constant.RabbitConstants;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.exception.exceptions.BadEmailException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.message.AddEcoNewsMessage;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.repository.UserRepo;
import greencity.service.EcoNewsService;
import greencity.service.NewsSubscriberService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EcoNewsServiceImpl implements EcoNewsService {
    private final EcoNewsRepo ecoNewsRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final EcoNewsTranslationRepo ecoNewsTranslationRepo;
    private RabbitTemplate rabbitTemplate;
    private NewsSubscriberService newsSubscriberService;
    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;

    /**
     * Constructor with parameters.
     *
     * @author Yuriy Olkhovskyi.
     */
    @Autowired
    public EcoNewsServiceImpl(EcoNewsRepo ecoNewsRepo, UserRepo userRepo, ModelMapper modelMapper,
                              EcoNewsTranslationRepo ecoNewsTranslationRepo,
                              RabbitTemplate rabbitTemplate,
                              NewsSubscriberService newsSubscriberService) {
        this.ecoNewsRepo = ecoNewsRepo;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
        this.ecoNewsTranslationRepo = ecoNewsTranslationRepo;
        this.rabbitTemplate = rabbitTemplate;
        this.newsSubscriberService = newsSubscriberService;
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest, String email) {
        addEcoNewsDtoRequest.setAuthor(modelMapper.map(
            userRepo.findByEmail(email).orElseThrow(() -> new BadEmailException(
                ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email)), EcoNewsAuthorDto.class));
        EcoNews toSave = modelMapper.map(addEcoNewsDtoRequest, EcoNews.class);
        toSave.setCreationDate(ZonedDateTime.now());

        try {
            ecoNewsRepo.save(toSave);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED);
        }

        rabbitTemplate.convertAndSend(sendEmailTopic, RabbitConstants.ADD_ECO_NEWS_ROUTING_KEY,
            buildAddEcoNewsMessage(toSave));

        return modelMapper.map(toSave, AddEcoNewsDtoResponse.class);
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