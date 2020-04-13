package greencity.service.impl;

import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.constant.RabbitConstants;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.message.AddEcoNewsMessage;
import greencity.repository.EcoNewsRepo;
import greencity.service.*;
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

    private final RabbitTemplate rabbitTemplate;

    private final NewsSubscriberService newsSubscriberService;

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
        toSave.setImagePath(fileService.upload(image).toString());

        toSave.setTags(addEcoNewsDtoRequest.getTags()
            .stream()
            .map(tagService::findByName)
            .collect(Collectors.toList())
        );

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
    @Cacheable(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME)
    @Override
    public List<EcoNewsDto> getThreeLastEcoNews() {
        List<EcoNews> ecoNewsList = ecoNewsRepo.getThreeLastEcoNews();

        if (ecoNewsList.isEmpty()) {
            throw new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND);
        }

        return ecoNewsList
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
    public PageableDto<EcoNewsDto> findAll(Pageable page) {
        Page<EcoNews> pages = ecoNewsRepo.findAll(page);
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
    public PageableDto<EcoNewsDto> find(Pageable page, List<String> tags) {
        Page<EcoNews> pages = ecoNewsRepo.find(page, tags);

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
    public EcoNewsDto findDtoById(Long id) {
        EcoNews ecoNews = findById(id);

        return modelMapper.map(ecoNews, EcoNewsDto.class);
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
     * Method for getting EcoNews by searchQuery
     *
     * @param searchQuery query to search
     * @return list of {@link EcoNewsDto}
     * @author Kovaliv Taras
     */
    @Override
    public List<EcoNewsDto> search(String searchQuery) {
        return null;
    }

    /**
     * Method for building message for sending email about adding new eco news.
     *
     * @param ecoNews {@link EcoNews} which was added.
     * @return {@link AddEcoNewsMessage} which contains needed info about {@link EcoNews} and subscribers.
     */
    private AddEcoNewsMessage buildAddEcoNewsMessage(EcoNews ecoNews) {
        AddEcoNewsDtoResponse addEcoNewsDtoResponse = modelMapper.map(ecoNews, AddEcoNewsDtoResponse.class);

        return new AddEcoNewsMessage(newsSubscriberService.findAll(), addEcoNewsDtoResponse);
    }
}