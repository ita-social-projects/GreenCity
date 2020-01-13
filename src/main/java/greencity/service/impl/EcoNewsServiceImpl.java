package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.repository.NewsSubscriberRepo;
import greencity.service.EcoNewsService;
import greencity.service.EmailService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class EcoNewsServiceImpl implements EcoNewsService {
    private final EcoNewsRepo ecoNewsRepo;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final NewsSubscriberRepo newsSubscriberRepo;
    private final EcoNewsTranslationRepo ecoNewsTranslationRepo;

    /**
     * Constructor with parameters.
     *
     * @author Yuriy Olkhovskyi.
     */
    @Autowired
    public EcoNewsServiceImpl(EcoNewsRepo ecoNewsRepo, ModelMapper modelMapper,
                              EmailService emailService, NewsSubscriberRepo newsSubscriberRepo,
                              EcoNewsTranslationRepo ecoNewsTranslationRepo) {
        this.ecoNewsRepo = ecoNewsRepo;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
        this.newsSubscriberRepo = newsSubscriberRepo;
        this.ecoNewsTranslationRepo = ecoNewsTranslationRepo;
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @Override
    public AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest, String languageCode) {
        EcoNews toSave = modelMapper.map(addEcoNewsDtoRequest, EcoNews.class);
        toSave.setCreationDate(ZonedDateTime.now());
        try {
            ecoNewsRepo.save(toSave);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED);
        }
        List<NewsSubscriberResponseDto> subscribers = modelMapper.map(newsSubscriberRepo.findAll(),
            new TypeToken<List<NewsSubscriberResponseDto>>() {
            }.getType());
        if (!subscribers.isEmpty()) {
            AddEcoNewsDtoResponse newsToSend = modelMapper.map(toSave, AddEcoNewsDtoResponse.class);
            newsToSend.setTitle(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(toSave,
                AppConstant.DEFAULT_LANGUAGE_CODE).getTitle());

            emailService.sendNewNewsForSubscriber(subscribers, newsToSend);
        }
        return modelMapper.map(toSave, AddEcoNewsDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
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
     * @author Yuriy Olkhovskyi.
     */
    @Override
    public List<EcoNewsDto> findAll(String languageCode) {
        return ecoNewsTranslationRepo.findAllByLanguageCode(languageCode)
            .stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());
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
     * @author Yuriy Olkhovskyi.
     */
    public void delete(Long id) {
        ecoNewsRepo.deleteById(findById(id).getId());
    }
}
