package greencity.service.impl;

import greencity.annotations.RatingCalculation;
import greencity.annotations.RatingCalculationEnum;
import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.constant.RabbitConstants;
import greencity.dto.PageableDto;
import greencity.dto.econews.*;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.dto.search.SearchNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.enums.ROLE;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.filters.EcoNewsSpecification;
import greencity.filters.SearchCriteria;
import greencity.message.AddEcoNewsMessage;
import greencity.repository.EcoNewsRepo;
import greencity.service.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@EnableCaching
@RequiredArgsConstructor
public class EcoNewsServiceImpl implements EcoNewsService {
    private final EcoNewsRepo ecoNewsRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;
    private final NewsSubscriberService newsSubscriberService;
    private final TagsService tagService;
    private final FileService fileService;
    private final BeanFactory beanFactory;
    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @RatingCalculation(rating = RatingCalculationEnum.ADD_ECO_NEWS)
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest,
                                      MultipartFile image, String email) {
        EcoNews toSave = modelMapper.map(addEcoNewsDtoRequest, EcoNews.class);
        toSave.setAuthor(userService.findByEmail(email));
        if (addEcoNewsDtoRequest.getImage() != null) {
            image = fileService.convertToMultipartImage(addEcoNewsDtoRequest.getImage());
        }
        if (image != null) {
            toSave.setImagePath(fileService.upload(image).toString());
        }

        Set<String> tagsSet = new HashSet<>(addEcoNewsDtoRequest.getTags());

        if (tagsSet.size() < addEcoNewsDtoRequest.getTags().size()) {
            throw new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED);
        }

        toSave.setTags(modelMapper.map(tagService.findEcoNewsTagsByNames(addEcoNewsDtoRequest.getTags()),
            new TypeToken<List<Tag>>() {
            }.getType()));
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
        List<EcoNews> ecoNewsList = ecoNewsRepo.getThreeRecommendedEcoNews(openedEcoNewsId);
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
            pages.getPageable().getPageNumber(),
            pages.getTotalPages()
        );
    }

    /**
     * {@inheritDoc}
     *
     * @author Kovaliv Taras.
     */
    @Override
    public PageableDto<EcoNewsDto> find(Pageable page, List<String> tags) {
        List<String> lowerCaseTags = tags.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        Page<EcoNews> pages = ecoNewsRepo.find(page, lowerCaseTags);

        List<EcoNewsDto> ecoNewsDtos = pages.stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            ecoNewsDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages()
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
    @RatingCalculation(rating = RatingCalculationEnum.DELETE_ECO_NEWS)
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public void delete(Long id) {
        ecoNewsRepo.deleteById(findById(id).getId());
    }

    @Override
    public void deleteAll(List<Long> listId) {
        listId.forEach(ecoNewsRepo::deleteById);
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

        return getSearchNewsDtoPageableDto(page);
    }

    @Override
    public PageableDto<SearchNewsDto> search(Pageable pageable, String searchQuery) {
        Page<EcoNews> page = ecoNewsRepo.searchEcoNews(pageable, searchQuery);
        return getSearchNewsDtoPageableDto(page);
    }

    private PageableDto<SearchNewsDto> getSearchNewsDtoPageableDto(Page<EcoNews> page) {
        List<SearchNewsDto> searchNewsDtos = page.stream()
            .map(ecoNews -> modelMapper.map(ecoNews, SearchNewsDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            searchNewsDtos,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages()
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

    /**
     * Method for getting amount of published news by user id.
     *
     * @param id {@link Long} user id.
     * @return amount of published news by user id.
     * @author Marian Datsko
     */
    @Override
    public Long getAmountOfPublishedNewsByUserId(Long id) {
        return ecoNewsRepo.getAmountOfPublishedNewsByUserId(id);
    }

    /**
     * Method to mark comment as liked by User.
     *
     * @param user    {@link User}.
     * @param comment {@link EcoNewsComment}
     * @author Dovganyuk Taras
     */
    @RatingCalculation(rating = RatingCalculationEnum.LIKE_COMMENT)
    public void likeComment(User user, EcoNewsComment comment) {
        comment.getUsersLiked().add(user);
    }

    /**
     * Method to mark comment as unliked by User.
     *
     * @param user    {@link User}.
     * @param comment {@link EcoNewsComment}
     * @author Dovganyuk Taras
     */
    @RatingCalculation(rating = RatingCalculationEnum.UNLIKE_COMMENT)
    public void unlikeComment(User user, EcoNewsComment comment) {
        comment.getUsersLiked().remove(user);
    }

    @Override
    public PageableDto<EcoNewsDto> searchEcoNewsBy(Pageable paging, String query) {
        Page<EcoNews> page = ecoNewsRepo.searchEcoNewsBy(paging, query);
        List<EcoNewsDto> ecoNews = page.stream()
            .map(ecoNew -> modelMapper.map(ecoNew, EcoNewsDto.class))
            .collect(Collectors.toList());
        return new PageableDto<>(ecoNews,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages()
        );
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public void update(EcoNewsDtoManagement ecoNewsDtoManagement, MultipartFile image) {
        EcoNews toUpdate = findById(ecoNewsDtoManagement.getId());
        toUpdate.setTitle(ecoNewsDtoManagement.getTitle());
        toUpdate.setText(ecoNewsDtoManagement.getText());
        toUpdate.setTags(modelMapper
            .map(tagService.findTipsAndTricksTagsByNames(ecoNewsDtoManagement.getTags()), new TypeToken<List<Tag>>() {
            }.getType()));
        if (image != null) {
            toUpdate.setImagePath(fileService.upload(image).toString());
        }
        ecoNewsRepo.save(toUpdate);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public EcoNewsDto update(UpdateEcoNewsDto updateEcoNewsDto, MultipartFile image, User user) {
        EcoNews toUpdate = findById(updateEcoNewsDto.getId());
        if (user.getRole() != ROLE.ROLE_ADMIN && !user.getId().equals(toUpdate.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        toUpdate.setTitle(updateEcoNewsDto.getTitle());
        toUpdate.setText(updateEcoNewsDto.getText());
        toUpdate.setSource(updateEcoNewsDto.getSource());
        toUpdate.setTags(modelMapper.map(tagService.findEcoNewsTagsByNames(updateEcoNewsDto.getTags()),
            new TypeToken<List<Tag>>() {
            }.getType()));
        if (updateEcoNewsDto.getImage() != null) {
            image = fileService.convertToMultipartImage(updateEcoNewsDto.getImage());
        }
        if (image != null) {
            toUpdate.setImagePath(fileService.upload(image).toString());
        }
        return modelMapper.map(ecoNewsRepo.save(toUpdate), EcoNewsDto.class);
    }

    @Override
    public PageableDto<EcoNewsDto> getFilteredDataForManagementByPage(
        Pageable pageable, EcoNewsViewDto ecoNewsViewDto) {
        Page<EcoNews> page = ecoNewsRepo.findAll(getSpecification(ecoNewsViewDto), pageable);
        List<EcoNewsDto> ecoNews = page.stream()
            .map(ecoNew -> modelMapper.map(ecoNew, EcoNewsDto.class))
            .collect(Collectors.toList());
        return new PageableDto<>(ecoNews,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages()
        );
    }

    /**
     * Returns {@link EcoNewsSpecification} for entered filter parameters.
     *
     * @param ecoNewsViewDto contains data from filters
     */
    public EcoNewsSpecification getSpecification(EcoNewsViewDto ecoNewsViewDto) {
        List<SearchCriteria> searchCriteria = buildSearchCriteria(ecoNewsViewDto);
        return new EcoNewsSpecification(searchCriteria);
    }

    /**
     * * This method used for build {@link SearchCriteria} depends on {@link RatingStatisticsViewDto}.
     *
     * @param ecoNewsViewDto used for receive parameters for filters from UI.
     * @return {@link SearchCriteria}.
     */
    public List<SearchCriteria> buildSearchCriteria(EcoNewsViewDto ecoNewsViewDto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        SearchCriteria searchCriteria;
        if (!ecoNewsViewDto.getId().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(ecoNewsViewDto.getId())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ecoNewsViewDto.getTitle().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("title")
                .type("title")
                .value(ecoNewsViewDto.getTitle())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ecoNewsViewDto.getAuthor().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("author")
                .type("author")
                .value(ecoNewsViewDto.getAuthor())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ecoNewsViewDto.getText().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("text")
                .type("text")
                .value(ecoNewsViewDto.getText())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ecoNewsViewDto.getStartDate().isEmpty() && !ecoNewsViewDto.getEndDate().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("creationDate")
                .type("dateRange")
                .value(new String[] {ecoNewsViewDto.getStartDate(), ecoNewsViewDto.getEndDate()})
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ecoNewsViewDto.getImagePath().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("imagePath")
                .type("imagePath")
                .value(ecoNewsViewDto.getImagePath())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ecoNewsViewDto.getSource().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("source")
                .type("source")
                .value(ecoNewsViewDto.getSource())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!ecoNewsViewDto.getTags().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("tags")
                .type("tags")
                .value(ecoNewsViewDto.getTags())
                .build();
            criteriaList.add(searchCriteria);
        }
        return criteriaList;
    }
}
