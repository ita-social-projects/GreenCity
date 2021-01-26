package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.annotations.RatingCalculation;
import greencity.annotations.RatingCalculationEnum;
import greencity.client.RestClient;
import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.constant.RabbitConstants;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsDtoManagement;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.econews.UpdateEcoNewsDto;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.EcoNews_;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.enums.AchievementCategory;
import greencity.enums.AchievementType;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.filters.EcoNewsSpecification;
import greencity.filters.SearchCriteria;
import greencity.message.AddEcoNewsMessage;
import greencity.repository.EcoNewsRepo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@EnableCaching
@RequiredArgsConstructor
public class EcoNewsServiceImpl implements EcoNewsService {
    private final EcoNewsRepo ecoNewsRepo;
    private final RestClient restClient;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;
    private final NewsSubscriberService newsSubscriberService;
    private final TagsService tagService;
    private final FileService fileService;
    private final AchievementCalculation achievementCalculation;
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
        User user = modelMapper.map(restClient.findByEmail(email), User.class);
        toSave.setAuthor(user);
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

        List<TagVO> tagVOS = tagService.findTagsByNamesAndType(
            addEcoNewsDtoRequest.getTags(), TagType.ECO_NEWS);

        toSave.setTags(modelMapper.map(tagVOS,
            new TypeToken<List<Tag>>() {
            }.getType()));
        try {
            ecoNewsRepo.save(toSave);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED);
        }

        rabbitTemplate.convertAndSend(sendEmailTopic, RabbitConstants.ADD_ECO_NEWS_ROUTING_KEY,
            buildAddEcoNewsMessage(toSave));
        CompletableFuture.runAsync(() -> achievementCalculation
            .calculateAchievement(user.getId(), AchievementType.INCREMENT, AchievementCategory.ECO_NEWS, 0));
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
        return getEcoNewsList(ecoNewsList);
    }

    /**
     * {@inheritDoc}
     *
     * @author Zhurakovskyi Yurii.
     */
    @Override
    public List<EcoNewsDto> getThreeRecommendedEcoNews(Long openedEcoNewsId) {
        List<EcoNews> ecoNewsList = ecoNewsRepo.getThreeRecommendedEcoNews(openedEcoNewsId);
        return getEcoNewsList(ecoNewsList);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kovaliv Taras.
     */
    @Override
    public PageableAdvancedDto<EcoNewsDto> findAll(Pageable page) {
        Page<EcoNews> pages = ecoNewsRepo.findAllByOrderByCreationDateDesc(page);
        return buildPageableAdvancedDto(pages);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kovaliv Taras.
     */
    @Override
    public PageableAdvancedDto<EcoNewsDto> find(Pageable page, List<String> tags) {
        List<String> lowerCaseTags = tags.stream().map(String::toLowerCase).collect(Collectors.toList());
        Page<EcoNews> pages = ecoNewsRepo.findByTags(page, lowerCaseTags);

        return buildPageableAdvancedDto(pages);
    }

    private PageableAdvancedDto<EcoNewsDto> buildPageableAdvancedDto(Page<EcoNews> ecoNewsPage) {
        List<EcoNewsDto> ecoNewsDtos = ecoNewsPage.stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());

        return new PageableAdvancedDto<>(
            ecoNewsDtos,
            ecoNewsPage.getTotalElements(),
            ecoNewsPage.getPageable().getPageNumber(),
            ecoNewsPage.getTotalPages(),
            ecoNewsPage.getNumber(),
            ecoNewsPage.hasPrevious(),
            ecoNewsPage.hasNext(),
            ecoNewsPage.isFirst(),
            ecoNewsPage.isLast());
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @Override
    public EcoNewsVO findById(Long id) {
        EcoNews ecoNews = ecoNewsRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + id));
        return modelMapper.map(ecoNews, EcoNewsVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kovaliv Taras.
     */
    @Override
    public EcoNewsDto findDtoById(Long id) {
        EcoNews ecoNews = ecoNewsRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + id));

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
    public void delete(Long id, UserVO user) {
        EcoNewsVO ecoNewsVO = findById(id);
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(ecoNewsVO.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        ecoNewsRepo.deleteById(ecoNewsVO.getId());
    }

    @Transactional
    @Override
    public void deleteAll(List<Long> listId) {
        ecoNewsRepo.deleteEcoNewsWithIds(listId);
    }

    /**
     * Method for getting EcoNews by searchQuery.
     *
     * @param searchQuery query to search
     * @return list of {@link EcoNewsDto}
     * @author Kovaliv Taras
     */
    @Override
    public PageableDto<SearchNewsDto> search(String searchQuery, String languageCode) {
        Page<EcoNews> page = ecoNewsRepo.searchEcoNews(PageRequest.of(0, 3), searchQuery, languageCode);

        return getSearchNewsDtoPageableDto(page);
    }

    @Override
    public PageableDto<SearchNewsDto> search(Pageable pageable, String searchQuery, String languageCode) {
        Page<EcoNews> page = ecoNewsRepo.searchEcoNews(pageable, searchQuery, languageCode);
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
            page.getTotalPages());
    }

    /**
     * Method for building message for sending email about adding new eco news.
     *
     * @param ecoNews {@link EcoNews} which was added.
     * @return {@link AddEcoNewsMessage} which contains needed info about
     * {@link EcoNews} and subscribers.
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
    public void likeComment(UserVO user, EcoNewsCommentVO comment) {
        comment.getUsersLiked().add(user);
        CompletableFuture.runAsync(() -> achievementCalculation
            .calculateAchievement(user.getId(), AchievementType.INCREMENT, AchievementCategory.ECO_NEWS_LIKE, 0));
    }

    /**
     * Method to mark comment as unliked by User.
     *
     * @param user    {@link User}.
     * @param comment {@link EcoNewsComment}
     * @author Dovganyuk Taras
     */
    @RatingCalculation(rating = RatingCalculationEnum.UNLIKE_COMMENT)
    public void unlikeComment(UserVO user, EcoNewsCommentVO comment) {
        comment.getUsersLiked().removeIf(u -> u.getId().equals(user.getId()));
    }

    @Override
    public PageableAdvancedDto<EcoNewsDto> searchEcoNewsBy(Pageable paging, String query) {
        Page<EcoNews> page = ecoNewsRepo.searchEcoNewsBy(paging, query);
        return buildPageableAdvancedDto(page);
    }

    private void enhanceWithNewManagementData(EcoNews toUpdate, EcoNewsDtoManagement ecoNewsDtoManagement,
                                              MultipartFile image) {
        toUpdate.setTitle(ecoNewsDtoManagement.getTitle());
        toUpdate.setText(ecoNewsDtoManagement.getText());
        toUpdate.setTags(modelMapper
            .map(tagService.findTagsByNamesAndType(ecoNewsDtoManagement.getTags(), TagType.ECO_NEWS),
                new TypeToken<List<Tag>>() {
                }.getType()));
        if (image != null) {
            toUpdate.setImagePath(fileService.upload(image).toString());
        }
    }

    private void enhanceWithNewData(EcoNews toUpdate, UpdateEcoNewsDto updateEcoNewsDto,
                                    MultipartFile image) {
        toUpdate.setTitle(updateEcoNewsDto.getTitle());
        toUpdate.setText(updateEcoNewsDto.getText());
        toUpdate.setSource(updateEcoNewsDto.getSource());
        toUpdate.setTags(modelMapper.map(tagService
                .findTagsByNamesAndType(updateEcoNewsDto.getTags(), TagType.ECO_NEWS),
            new TypeToken<List<Tag>>() {
            }.getType()));
        if (updateEcoNewsDto.getImage() != null) {
            image = fileService.convertToMultipartImage(updateEcoNewsDto.getImage());
        }
        if (image != null) {
            toUpdate.setImagePath(fileService.upload(image).toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public void update(EcoNewsDtoManagement ecoNewsDtoManagement, MultipartFile image) {
        EcoNews toUpdate = modelMapper.map(findById(ecoNewsDtoManagement.getId()), EcoNews.class);
        enhanceWithNewManagementData(toUpdate, ecoNewsDtoManagement, image);

        ecoNewsRepo.save(toUpdate);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public EcoNewsDto update(UpdateEcoNewsDto updateEcoNewsDto, MultipartFile image, UserVO user) {
        EcoNews toUpdate = modelMapper.map(findById(updateEcoNewsDto.getId()), EcoNews.class);
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(toUpdate.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        enhanceWithNewData(toUpdate, updateEcoNewsDto, image);

        return modelMapper.map(ecoNewsRepo.save(toUpdate), EcoNewsDto.class);
    }

    @Override
    public PageableAdvancedDto<EcoNewsDto> getFilteredDataForManagementByPage(
        Pageable pageable, EcoNewsViewDto ecoNewsViewDto) {
        Page<EcoNews> page = ecoNewsRepo.findAll(getSpecification(ecoNewsViewDto), pageable);
        return buildPageableAdvancedDto(page);
    }

    /**
     * Method to like or dislike {@link EcoNews} by id.
     *
     * @param userVO - current {@link User} that like/dislike news.
     * @param id     - @{@link Long} eco news id.
     */
    @Override
    public void like(UserVO userVO, Long id) {
        EcoNewsVO ecoNewsVO = findById(id);
        if (ecoNewsVO.getUsersLikedNews().stream().anyMatch(u -> u.getId().equals(userVO.getId()))) {
            ecoNewsVO.getUsersLikedNews().removeIf(u -> u.getId().equals(userVO.getId()));
        } else {
            ecoNewsVO.getUsersLikedNews().add(userVO);
        }

        ecoNewsRepo.save(modelMapper.map(ecoNewsVO, EcoNews.class));
    }

    /**
     * Method to get amount of likes by eco news id.
     *
     * @param id - @{@link Integer} eco news id.
     * @return amount of likes by eco news id.
     */
    @Override
    public Integer countLikesForEcoNews(Long id) {
        EcoNewsVO ecoNewsVO = findById(id);
        return ecoNewsVO.getUsersLikedNews().size();
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
     * * This method used for build {@link SearchCriteria} depends on
     * {@link RatingStatisticsViewDto}.
     *
     * @param ecoNewsViewDto used for receive parameters for filters from UI.
     * @return {@link SearchCriteria}.
     */
    public List<SearchCriteria> buildSearchCriteria(EcoNewsViewDto ecoNewsViewDto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        SearchCriteria searchCriteria;

        setValueIfNotEmpty(criteriaList, EcoNews_.ID, ecoNewsViewDto.getId());
        setValueIfNotEmpty(criteriaList, EcoNews_.TITLE, ecoNewsViewDto.getTitle());
        setValueIfNotEmpty(criteriaList, EcoNews_.AUTHOR, ecoNewsViewDto.getAuthor());
        setValueIfNotEmpty(criteriaList, EcoNews_.TEXT, ecoNewsViewDto.getText());
        setValueIfNotEmpty(criteriaList, EcoNews_.IMAGE_PATH, ecoNewsViewDto.getImagePath());
        setValueIfNotEmpty(criteriaList, EcoNews_.SOURCE, ecoNewsViewDto.getSource());
        setValueIfNotEmpty(criteriaList, EcoNews_.TAGS, ecoNewsViewDto.getTags());

        if (!ecoNewsViewDto.getStartDate().isEmpty() && !ecoNewsViewDto.getEndDate().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key(EcoNews_.CREATION_DATE)
                .type("dateRange")
                .value(new String[] {ecoNewsViewDto.getStartDate(), ecoNewsViewDto.getEndDate()})
                .build();
            criteriaList.add(searchCriteria);
        }

        return criteriaList;
    }

    private void setValueIfNotEmpty(List<SearchCriteria> searchCriteria, String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            searchCriteria.add(SearchCriteria.builder()
                .key(key)
                .type(key)
                .value(value)
                .build());
        }
    }

    private List<EcoNewsDto> getEcoNewsList(List<EcoNews> ecoNewsList) {
        return ecoNewsList
            .stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());
    }
}
