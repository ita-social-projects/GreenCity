package greencity.service.impl;

import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.constant.RabbitConstants;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.search.SearchNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.message.AddEcoNewsMessage;
import greencity.repository.EcoNewsRepo;
import greencity.service.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import lombok.RequiredArgsConstructor;

import static greencity.constant.ValidationConstants.NUMBER_OF_RECOMMENDED_ECO_NEWS;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@EnableCaching
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
        if (addEcoNewsDtoRequest.getImage() != null) {
            image = convertToMultipartImage(addEcoNewsDtoRequest.getImage());
        }
        if (image != null) {
            toSave.setImagePath(fileService.upload(image).toString());
        }

        Set<String> tagsSet = new HashSet<>(addEcoNewsDtoRequest.getTags());

        if (tagsSet.size() < addEcoNewsDtoRequest.getTags().size()) {
            throw new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED);
        }

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
     * @author Zhurakovskyi Yurii.
     */
    @Override
    public List<EcoNewsDto> getThreeRecommendedEcoNews(Long openedEcoNewsId) {
        List<EcoNews> ecoNewsList = new ArrayList<>();
        EcoNews openedEcoNews = findById(openedEcoNewsId);
        List<Long> tags = openedEcoNews.getTags().stream().map(Tag::getId).collect(Collectors.toList());
        if (tags.size() == 3)
            ecoNewsList = ecoNewsRepo.getThreeRecommendedEcoNews(3, tags.get(0), tags.get(1), tags.get(2), openedEcoNewsId);
        else if (tags.size() == 2)
            ecoNewsList = ecoNewsRepo.getThreeRecommendedEcoNews(2, tags.get(0), tags.get(1), 0L, openedEcoNewsId);
        else if (tags.size() == 1)
            ecoNewsList = ecoNewsRepo.getThreeRecommendedEcoNews(1, tags.get(0), 0L, 0L, openedEcoNewsId);
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
        Page<EcoNews> pages = ecoNewsRepo.findAllByOrderByCreationDateDesc(page);
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
     * Method for getting EcoNews by searchQuery.
     *
     * @param searchQuery query to search
     * @return list of {@link EcoNewsDto}
     * @author Kovaliv Taras
     */
    @Override
    public PageableDto<SearchNewsDto> search(String searchQuery) {
        Page<EcoNews> page = ecoNewsRepo.searchEcoNews(PageRequest.of(0, 3), searchQuery);

        List<SearchNewsDto> ecoNews = page.stream()
                .map(ecoNews1 -> modelMapper.map(ecoNews1, SearchNewsDto.class))
                .collect(Collectors.toList());

        return new PageableDto<>(
                ecoNews,
                page.getTotalElements(),
                page.getPageable().getPageNumber()
        );
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

    private MultipartFile convertToMultipartImage(String image) {
        String imageToConvert = image.substring(image.indexOf(',') + 1);
        File tempFile = new File("tempImage.jpg");
        byte[] imageByte = decodeBase64(imageToConvert);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        try {
            BufferedImage bufferedImage = ImageIO.read(bis);
            ImageIO.write(bufferedImage, "png", tempFile);
            return new MockMultipartFile(tempFile.getPath(), new FileInputStream(tempFile));
        } catch (IOException e) {
            throw new NotSavedException("Cannot to convert BASE64 image");
        }
    }
}
