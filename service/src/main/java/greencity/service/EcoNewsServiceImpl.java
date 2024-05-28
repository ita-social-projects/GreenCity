package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.constant.CacheConstants;
import greencity.constant.EmailNotificationMessagesConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewContentSourceDto;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsDtoManagement;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.econews.UpdateEcoNewsDto;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.EcoNews_;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import greencity.enums.AchievementAction;
import greencity.enums.AchievementCategoryType;
import greencity.enums.CommentStatus;
import greencity.enums.NotificationType;
import greencity.enums.RatingCalculationEnum;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.exceptions.UnsupportedSortException;
import greencity.filters.EcoNewsSpecification;
import greencity.filters.SearchCriteria;
import greencity.message.GeneralEmailMessage;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsSearchRepo;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@EnableCaching
@RequiredArgsConstructor
public class EcoNewsServiceImpl implements EcoNewsService {
    private final EcoNewsRepo ecoNewsRepo;
    private final RestClient restClient;
    private final ModelMapper modelMapper;
    private final TagsService tagService;
    private final FileService fileService;
    private final AchievementCalculation achievementCalculation;
    private final greencity.rating.RatingCalculation ratingCalculation;
    private final HttpServletRequest httpServletRequest;
    private final EcoNewsSearchRepo ecoNewsSearchRepo;
    private final NotificationService notificationService;
    private final List<String> languageCode = List.of("en", "ua");
    private final UserService userService;
    private final UserNotificationService userNotificationService;

    private static final String ECO_NEWS_TITLE = "title";
    private static final String ECO_NEWS_JOIN_TAG = "tags";
    private static final String ECO_NEWS_TAG_TRANSLATION = "tagTranslations";
    private static final String ECO_NEWS_TAG_TRANSLATION_NAME = "name";

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest,
        MultipartFile image, String email) {
        EcoNews toSave = genericSave(addEcoNewsDtoRequest, image, email);
        UserVO userVO = userService.findById(toSave.getAuthor().getId());
        achievementCalculation
            .calculateAchievement(userVO, AchievementCategoryType.CREATE_NEWS, AchievementAction.ASSIGN);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.CREATE_NEWS, modelMapper.map(toSave, UserVO.class));
        notificationService.sendEmailNotification(GeneralEmailMessage.builder()
            .email(email)
            .subject(EmailNotificationMessagesConstants.ECONEWS_CREATION_SUBJECT)
            .message(String.format(EmailNotificationMessagesConstants.ECONEWS_CREATION_MESSAGE, toSave.getTitle()))
            .build());
        userNotificationService.createNewNotification(userVO, NotificationType.ECONEWS_CREATED, toSave.getId(),
            toSave.getTitle());
        return modelMapper.map(toSave, AddEcoNewsDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Danylo Hlynskyi.
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public EcoNewsGenericDto saveEcoNews(AddEcoNewsDtoRequest addEcoNewsDtoRequest, MultipartFile image, String email) {
        EcoNews toSave = genericSave(addEcoNewsDtoRequest, image, email);
        final EcoNewsGenericDto ecoNewsDto = getEcoNewsGenericDtoWithAllTags(toSave);
        UserVO user = userService.findByEmail(email);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.CREATE_NEWS, user);
        achievementCalculation.calculateAchievement(user,
            AchievementCategoryType.CREATE_NEWS, AchievementAction.ASSIGN);
        notificationService.sendEmailNotification(GeneralEmailMessage.builder()
            .email(toSave.getAuthor().getEmail())
            .subject(EmailNotificationMessagesConstants.ECONEWS_CREATION_SUBJECT)
            .message(String.format(EmailNotificationMessagesConstants.ECONEWS_CREATION_MESSAGE, toSave.getTitle()))
            .build());
        return ecoNewsDto;
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
        Page<EcoNews> pages;
        if (page.getSort().isEmpty()) {
            pages = ecoNewsRepo.findAllByOrderByCreationDateDesc(page);
        } else {
            if (page.getSort().isUnsorted()) {
                pages = ecoNewsRepo.findAll(page);
            } else {
                throw new UnsupportedSortException(ErrorMessage.INVALID_SORTING_VALUE);
            }
        }
        return buildPageableAdvancedDto(pages);
    }

    /**
     * {@inheritDoc}
     *
     * @author Danylo Hlynskyi.
     */
    @Override
    public PageableAdvancedDto<EcoNewsGenericDto> findGenericAll(Pageable page) {
        Page<EcoNews> pages;
        if (page.getSort().isEmpty()) {
            pages = ecoNewsRepo.findAllByOrderByCreationDateDesc(page);
        } else {
            if (page.getSort().isUnsorted()) {
                pages = ecoNewsRepo.findAll(page);
            } else {
                throw new UnsupportedSortException(ErrorMessage.INVALID_SORTING_VALUE);
            }
        }
        return buildPageableAdvancedGeneticDto(pages);
    }

    /**
     * {@inheritDoc}
     *
     * @author Danylo Hlynskyi.
     */
    @Override
    public PageableAdvancedDto<EcoNewsGenericDto> findAllByUser(UserVO user, Pageable page) {
        Page<EcoNews> pages;
        if (page.getSort().isEmpty()) {
            pages = ecoNewsRepo.findAllByAuthorOrderByCreationDateDesc(modelMapper.map(user, User.class), page);
        } else {
            throw new UnsupportedSortException(ErrorMessage.INVALID_SORTING_VALUE);
        }
        return buildPageableAdvancedGeneticDto(pages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<EcoNewsGenericDto> find(Pageable page, List<String> tags) {
        List<String> lowerCaseTags = tags.stream().map(String::toLowerCase).collect(Collectors.toList());
        Page<EcoNews> pages = ecoNewsRepo.findByTags(page, lowerCaseTags);

        return buildPageableAdvancedGeneticDto(pages);
    }

    @Override
    public PageableAdvancedDto<EcoNewsGenericDto> findByFilters(Pageable page, List<String> tags, String title) {
        return CollectionUtils.isEmpty(tags) && StringUtils.isEmpty(title)
            ? buildPageableAdvancedGeneticDto(ecoNewsRepo.findAll(
                PageRequest.of(page.getPageNumber(), page.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "creationDate"))))
            : buildPageableAdvancedGeneticDto(ecoNewsRepo.findAll(
                (root, query, criteriaBuilder) -> getPredicate(root, criteriaBuilder, tags, title),
                PageRequest.of(page.getPageNumber(), page.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "creationDate"))));
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

    private PageableAdvancedDto<EcoNewsGenericDto> buildPageableAdvancedGeneticDto(Page<EcoNews> ecoNewsPage) {
        List<EcoNewsGenericDto> ecoNewsDtos = ecoNewsPage.stream()
            .map(this::getEcoNewsGenericDtoWithEnTags)
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
    public EcoNewsDto getById(Long id) {
        EcoNews ecoNews = ecoNewsRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + id));
        return modelMapper.map(ecoNews, EcoNewsDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Veremchuk Zakhar.
     */
    @Override
    public EcoNewsDto findDtoByIdAndLanguage(Long id, String language) {
        var ecoNews = ecoNewsRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + id));
        List<String> tags = new ArrayList<>();
        for (String lang : languageCode) {
            tags.addAll(ecoNews.getTags().stream().flatMap(t -> t.getTagTranslations().stream())
                .filter(tagTranslation -> tagTranslation.getLanguage().getCode().equals(lang))
                .map(TagTranslation::getName)
                .collect(Collectors.toList()));
        }
        return getEcoNewsDto(ecoNews, tags);
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public void delete(Long id, UserVO user) {
        EcoNewsVO ecoNewsVO = findById(id);
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(ecoNewsVO.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_CREATE_NEWS, user);
        achievementCalculation.calculateAchievement(user,
            AchievementCategoryType.CREATE_NEWS, AchievementAction.DELETE);

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
        Page<EcoNews> page = ecoNewsSearchRepo.find(PageRequest.of(0, 3), searchQuery, languageCode);
        return getSearchNewsDtoPageableDto(page);
    }

    @Override
    public PageableDto<SearchNewsDto> search(Pageable pageable, String searchQuery, String languageCode) {
        Page<EcoNews> page = ecoNewsSearchRepo.find(pageable, searchQuery, languageCode);
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
     * Method for getting all published news by user id.
     *
     * @param userId {@link Long} user id.
     * @return list of {@link EcoNewsDto} instances.
     */
    @Override
    public List<EcoNewsDto> getAllPublishedNewsByUserId(Long userId) {
        return ecoNewsRepo.findAllByUserId(userId).stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());
    }

    /**
     * Method for getting all published news by authorised user.
     *
     * @param user {@link UserVO}.
     * @return list of {@link EcoNewsDto} instances.
     * @author Vira Maksymets
     */
    @Override
    public List<EcoNewsDto> getAllPublishedNewsByUser(UserVO user) {
        return ecoNewsRepo.findAllByUserId(user.getId()).stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());
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
    public void likeComment(UserVO user, EcoNewsCommentVO comment) {
        comment.getUsersLiked().add(user);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.LIKE_COMMENT_OR_REPLY, user);
        achievementCalculation.calculateAchievement(user, AchievementCategoryType.LIKE_COMMENT_OR_REPLY,
            AchievementAction.ASSIGN);
    }

    /**
     * Method to mark comment as unliked by User.
     *
     * @param user    {@link User}.
     * @param comment {@link EcoNewsComment}
     * @author Dovganyuk Taras
     */
    public void unlikeComment(UserVO user, EcoNewsCommentVO comment) {
        comment.getUsersLiked().removeIf(u -> u.getId().equals(user.getId()));
        ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_LIKE_COMMENT_OR_REPLY, user);
        achievementCalculation.calculateAchievement(user,
            AchievementCategoryType.LIKE_COMMENT_OR_REPLY, AchievementAction.DELETE);
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
            toUpdate.setImagePath(fileService.upload(image));
        }
    }

    private void enhanceWithNewData(EcoNews toUpdate, UpdateEcoNewsDto updateEcoNewsDto,
        MultipartFile image) {
        toUpdate.setTitle(updateEcoNewsDto.getTitle());
        toUpdate.setText(updateEcoNewsDto.getContent());
        toUpdate.setShortInfo(updateEcoNewsDto.getShortInfo());
        toUpdate.setSource(updateEcoNewsDto.getSource());
        toUpdate.setTags(modelMapper.map(tagService
            .findTagsByNamesAndType(updateEcoNewsDto.getTags(), TagType.ECO_NEWS),
            new TypeToken<List<Tag>>() {
            }.getType()));
        if (updateEcoNewsDto.getImage() != null) {
            image = fileService.convertToMultipartImage(updateEcoNewsDto.getImage());
        }
        if (image != null) {
            toUpdate.setImagePath(fileService.upload(image));
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
     *
     * @return EcoNewsGenericDto
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public EcoNewsGenericDto update(UpdateEcoNewsDto updateEcoNewsDto, MultipartFile image, UserVO user) {
        EcoNews toUpdate = modelMapper.map(findById(updateEcoNewsDto.getId()), EcoNews.class);
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(toUpdate.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        enhanceWithNewData(toUpdate, updateEcoNewsDto, image);
        ecoNewsRepo.save(toUpdate);
        return getEcoNewsGenericDtoWithAllTags(toUpdate);
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
        if (ecoNewsVO.getUsersDislikedNews().stream().anyMatch(u -> u.getId().equals(userVO.getId()))) {
            ecoNewsVO.getUsersDislikedNews().removeIf(u -> u.getId().equals(userVO.getId()));
        }
        if (ecoNewsVO.getUsersLikedNews().stream().anyMatch(u -> u.getId().equals(userVO.getId()))) {
            achievementCalculation.calculateAchievement(userVO,
                AchievementCategoryType.LIKE_COMMENT_OR_REPLY, AchievementAction.DELETE);
            ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_LIKE_COMMENT_OR_REPLY, userVO);
            ecoNewsVO.getUsersLikedNews().removeIf(u -> u.getId().equals(userVO.getId()));
            userNotificationService.removeActionUserFromNotification(ecoNewsVO.getAuthor(), userVO,
                id, NotificationType.ECONEWS_LIKE);
        } else {
            if (ecoNewsVO.getAuthor().getId().equals(userVO.getId())) {
                throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
            }
            achievementCalculation.calculateAchievement(userVO,
                AchievementCategoryType.LIKE_COMMENT_OR_REPLY, AchievementAction.ASSIGN);
            ratingCalculation.ratingCalculation(RatingCalculationEnum.LIKE_COMMENT_OR_REPLY, userVO);
            ecoNewsVO.getUsersLikedNews().add(userVO);
            notificationService.sendEmailNotification(GeneralEmailMessage.builder()
                .email(ecoNewsVO.getAuthor().getEmail())
                .subject(EmailNotificationMessagesConstants.ECONEWS_LIKE_SUBJECT)
                .message(String.format(EmailNotificationMessagesConstants.ECONEWS_LIKE_MESSAGE, ecoNewsVO.getTitle()))
                .build());
        }
        ecoNewsRepo.save(modelMapper.map(ecoNewsVO, EcoNews.class));
    }

    /**
     * Method to like or dislike {@link EcoNews} by id.
     *
     * @param userVO - current {@link User} that like/dislike news.
     * @param id     - @{@link Long} eco news id.
     */
    @Override
    public void dislike(UserVO userVO, Long id) {
        EcoNewsVO ecoNewsVO = findById(id);
        if (ecoNewsVO.getUsersLikedNews().stream().anyMatch(user -> user.getId().equals(userVO.getId()))) {
            ecoNewsVO.getUsersLikedNews().removeIf(u -> u.getId().equals(userVO.getId()));
            userNotificationService.removeActionUserFromNotification(ecoNewsVO.getAuthor(), userVO, id,
                NotificationType.ECONEWS_LIKE);
        }
        if (ecoNewsVO.getUsersDislikedNews().stream().anyMatch(user -> user.getId().equals(userVO.getId()))) {
            ecoNewsVO.getUsersDislikedNews().removeIf(u -> u.getId().equals(userVO.getId()));
        } else {
            ecoNewsVO.getUsersDislikedNews().add(userVO);
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
     * Method to get amount of dislikes by eco news id.
     *
     * @param id - @{@link Integer} eco news id.
     * @return amount of dislikes by eco news id.
     */
    @Override
    public Integer countDislikesForEcoNews(Long id) {
        EcoNewsVO ecoNewsV0 = findById(id);
        return ecoNewsV0.getUsersDislikedNews().size();
    }

    /**
     * Method to check if user liked news.
     *
     * @param id     - id of {@link EcoNewsVO} to check liked or not.
     * @param userVO - current {@link UserVO}.
     * @return user liked news or not.
     */

    @Override
    public Boolean checkNewsIsLikedByUser(Long id, UserVO userVO) {
        EcoNewsVO ecoNewsVO = findById(id);
        return ecoNewsVO.getUsersLikedNews().stream().anyMatch(u -> u.getId().equals(userVO.getId()));
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
     * Method to upload news images.
     *
     * @param images - array of eco news images
     * @return array of images path
     */
    @Override
    public String[] uploadImages(MultipartFile[] images) {
        return Arrays.stream(images).map(fileService::upload).toArray(String[]::new);
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
        if (StringUtils.isNotEmpty(value)) {
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

    private EcoNewsGenericDto getEcoNewsGenericDtoWithAllTags(EcoNews ecoNews) {
        List<String> tags = ecoNews.getTags().stream()
            .flatMap(t -> t.getTagTranslations().stream())
            .map(TagTranslation::getName)
            .collect(Collectors.toList());

        return buildEcoNewsGenericDto(ecoNews, tags);
    }

    private EcoNewsGenericDto getEcoNewsGenericDtoWithEnTags(EcoNews ecoNews) {
        List<String> tags = new ArrayList<>();
        for (String language : languageCode) {
            tags.addAll(ecoNews.getTags().stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .filter(t -> t.getLanguage().getCode().equals(language))
                .map(TagTranslation::getName)
                .toList());
        }

        return buildEcoNewsGenericDto(ecoNews, tags);
    }

    private EcoNewsGenericDto buildEcoNewsGenericDto(EcoNews ecoNews, List<String> tags) {
        User author = ecoNews.getAuthor();
        var ecoNewsAuthorDto = new EcoNewsAuthorDto(author.getId(), author.getName());
        int countOfComments = ecoNews.getEcoNewsComments() != null
            ? (int) ecoNews.getEcoNewsComments().stream()
                .filter(ecoNewsComment -> !ecoNewsComment.getStatus().equals(CommentStatus.DELETED)).count()
            : 0;
        int countOfEcoNews = ecoNewsRepo.totalCountOfCreationNews();
        return EcoNewsGenericDto.builder()
            .id(ecoNews.getId())
            .imagePath(ecoNews.getImagePath())
            .author(ecoNewsAuthorDto)
            .tagsEn(tags.stream().filter(tag -> tag.matches("^([A-Za-z-])+$")).collect(Collectors.toList()))
            .tagsUa(tags.stream().filter(tag -> tag.matches("^([А-Яа-яієїґ'-])+$")).collect(Collectors.toList()))
            .shortInfo(ecoNews.getShortInfo())
            .content(ecoNews.getText())
            .title(ecoNews.getTitle())
            .creationDate(ecoNews.getCreationDate())
            .source(ecoNews.getSource())
            .likes(ecoNews.getUsersLikedNews() != null ? ecoNews.getUsersLikedNews().size() : 0)
            .countComments(countOfComments)
            .countOfEcoNews(countOfEcoNews)
            .build();
    }

    private EcoNewsDto getEcoNewsDto(EcoNews ecoNews, List<String> list) {
        User author = ecoNews.getAuthor();
        var ecoNewsAuthorDto = new EcoNewsAuthorDto(author.getId(),
            author.getName());

        return EcoNewsDto.builder()
            .id(ecoNews.getId())
            .imagePath(ecoNews.getImagePath())
            .author(ecoNewsAuthorDto)
            .likes(ecoNews.getUsersLikedNews().size())
            .tags(list.stream().filter(tag -> tag.matches("^([A-Za-z-])+$")).collect(Collectors.toList()))
            .tagsUa(list.stream().filter(tag -> tag.matches("^([А-Яа-яієїґ'-])+$")).collect(Collectors.toList()))
            .shortInfo(ecoNews.getShortInfo())
            .content(ecoNews.getText())
            .title(ecoNews.getTitle())
            .creationDate(ecoNews.getCreationDate())
            .build();
    }

    /**
     * Method for getting some fields in eco news by id.
     *
     * @param id - {@link Long} eco news id.
     * @return dto {@link EcoNewContentSourceDto}.
     */
    public EcoNewContentSourceDto getContentAndSourceForEcoNewsById(Long id) {
        EcoNews ecoNews = ecoNewsRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + id));
        return getContentSourceEcoNewsDto(ecoNews);
    }

    private EcoNewContentSourceDto getContentSourceEcoNewsDto(EcoNews ecoNews) {
        return EcoNewContentSourceDto.builder()
            .content(ecoNews.getText())
            .source(ecoNews.getSource())
            .build();
    }

    private EcoNews genericSave(AddEcoNewsDtoRequest addEcoNewsDtoRequest,
        MultipartFile image, String email) {
        EcoNews toSave = modelMapper.map(addEcoNewsDtoRequest, EcoNews.class);
        UserVO byEmail = restClient.findByEmail(email);
        User user = modelMapper.map(byEmail, User.class);
        toSave.setAuthor(user);
        if (addEcoNewsDtoRequest.getImage() != null) {
            image = fileService.convertToMultipartImage(addEcoNewsDtoRequest.getImage());
        }
        if (image != null) {
            toSave.setImagePath(fileService.upload(image));
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
        return toSave;
    }

    @Override
    public Set<UserVO> findUsersWhoLikedPost(Long id) {
        EcoNews ecoNews = ecoNewsRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + id));
        Set<User> usersLikedNews = ecoNews.getUsersLikedNews();
        return usersLikedNews.stream().map(u -> modelMapper.map(u, UserVO.class)).collect(Collectors.toSet());
    }

    @Override
    public Set<UserVO> findUsersWhoDislikedPost(Long id) {
        EcoNews ecoNews = ecoNewsRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + id));
        Set<User> usersDislikedNews = ecoNews.getUsersDislikedNews();
        return usersDislikedNews.stream().map(u -> modelMapper.map(u, UserVO.class)).collect(Collectors.toSet());
    }

    Predicate getPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder, List<String> tags,
        String title) {
        List<Predicate> predicates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tags)) {
            predicates.add(predicateForTags(criteriaBuilder, root, tags));
        }
        if (StringUtils.isNotEmpty(title)) {
            predicates
                .add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ECO_NEWS_TITLE)),
                    '%' + title.toLowerCase() + '%'));
        }

        return predicates.size() == 1
            ? predicates.getFirst()
            : predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate predicateForTags(CriteriaBuilder criteriaBuilder, Root<EcoNews> root, List<String> tags) {
        Join<EcoNews, Tag> ecoNewsTag = root.join(ECO_NEWS_JOIN_TAG);
        List<Predicate> predicateList = new ArrayList<>();
        tags.forEach(partOfSearchingText -> predicateList.add(criteriaBuilder.and(
            criteriaBuilder.like(
                criteriaBuilder.lower(ecoNewsTag.get(ECO_NEWS_TAG_TRANSLATION).get(ECO_NEWS_TAG_TRANSLATION_NAME)),
                "%" + partOfSearchingText.toLowerCase() + "%"))));
        return predicateList.size() == 1
            ? predicateList.getFirst()
            : criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
    }
}