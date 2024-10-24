package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.constant.CacheConstants;
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
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNews_;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import greencity.enums.AchievementAction;
import greencity.enums.AchievementCategoryType;
import greencity.enums.NotificationType;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.repository.RatingPointsRepo;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.filters.EcoNewsSpecification;
import greencity.filters.SearchCriteria;
import greencity.rating.RatingCalculation;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsSearchRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@EnableCaching
@Transactional
@RequiredArgsConstructor
public class EcoNewsServiceImpl implements EcoNewsService {
    private final EcoNewsRepo ecoNewsRepo;
    private final RestClient restClient;
    private final ModelMapper modelMapper;
    private final TagsService tagService;
    private final FileService fileService;
    private final AchievementCalculation achievementCalculation;
    private final RatingCalculation ratingCalculation;
    private final EcoNewsSearchRepo ecoNewsSearchRepo;
    private final List<String> languageCode = List.of("en", "ua");
    private final UserService userService;
    private final UserRepo userRepo;
    private final CommentService commentService;
    private final UserNotificationService userNotificationService;
    private final RatingPointsRepo ratingPointsRepo;

    private static final String ECO_NEWS_TITLE = "title";
    private static final String ECO_NEWS_JOIN_TAG = "tags";
    private static final String ECO_NEWS_TAG_TRANSLATION = "tagTranslations";
    private static final String ECO_NEWS_TAG_TRANSLATION_NAME = "name";
    private static final String ECO_NEWS_AUTHOR_ID = "id";

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest,
        MultipartFile image, String email) {
        EcoNews toSave = genericSave(addEcoNewsDtoRequest, image, email);
        UserVO userVO = userService.findById(toSave.getAuthor().getId());
        achievementCalculation
            .calculateAchievement(userVO, AchievementCategoryType.CREATE_NEWS, AchievementAction.ASSIGN);
        ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("CREATE_NEWS"),
            modelMapper.map(toSave, UserVO.class));
        userNotificationService.createNewNotification(userVO, NotificationType.ECONEWS_CREATED, toSave.getId(),
            toSave.getTitle());
        return modelMapper.map(toSave, AddEcoNewsDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public EcoNewsGenericDto saveEcoNews(AddEcoNewsDtoRequest addEcoNewsDtoRequest, MultipartFile image, String email) {
        EcoNews toSave = genericSave(addEcoNewsDtoRequest, image, email);
        final EcoNewsGenericDto ecoNewsDto = getEcoNewsGenericDtoWithAllTags(toSave);
        UserVO user = userService.findByEmail(email);
        ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("CREATE_NEWS"), user);
        achievementCalculation.calculateAchievement(user,
            AchievementCategoryType.CREATE_NEWS, AchievementAction.ASSIGN);
        return ecoNewsDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcoNewsDto> getThreeRecommendedEcoNews(Long ecoNewsId) {
        return mapEcoNewsListToEcoNewsDtoList(ecoNewsRepo.getThreeRecommendedEcoNews(ecoNewsId));
    }

    /**
     * {@inheritDoc}
     *
     * @author Danylo Hlynskyi.
     */
    @Override
    public List<EcoNewsDto> getAllByUser(UserVO user) {
        return ecoNewsRepo.findAllByAuthorId(user.getId()).stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<EcoNewsGenericDto> find(Pageable page, List<String> tags, String title, Long authorId) {
        return CollectionUtils.isEmpty(tags) && StringUtils.isEmpty(title) && authorId == null
            ? buildPageableAdvancedGenericDto(ecoNewsRepo.findAll(
                PageRequest.of(page.getPageNumber(), page.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "creationDate"))))
            : buildPageableAdvancedGenericDto(ecoNewsRepo.findAll(
                (root, query, criteriaBuilder) -> getPredicate(root, criteriaBuilder, tags, title, authorId),
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

    private PageableAdvancedDto<EcoNewsGenericDto> buildPageableAdvancedGenericDto(Page<EcoNews> ecoNewsPage) {
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
     */
    @Override
    public EcoNewsVO findById(Long id) {
        return ecoNewsRepo.findById(id)
            .map(n -> modelMapper.map(n, EcoNewsVO.class))
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EcoNewsDto findDtoByIdAndLanguage(Long id, String language) {
        EcoNews ecoNews = ecoNewsRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + id));
        List<String> tags = new ArrayList<>();
        for (String lang : languageCode) {
            tags.addAll(ecoNews.getTags().stream().flatMap(t -> t.getTagTranslations().stream())
                .filter(tagTranslation -> tagTranslation.getLanguage().getCode().equals(lang))
                .map(TagTranslation::getName)
                .toList());
        }
        return getEcoNewsDto(ecoNews, tags);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EcoNewsDto> findByTitle(String title) {
        List<EcoNews> ecoNewsList = ecoNewsRepo.findByTitleContaining(title);
        return mapEcoNewsListToEcoNewsDtoList(ecoNewsList);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public void delete(Long id, UserVO user) {
        EcoNewsVO ecoNewsVO = findById(id);
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(ecoNewsVO.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("UNDO_CREATE_NEWS"), user);
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public Long getAmountOfPublishedNews(Long authorId) {
        return authorId == null ? ecoNewsRepo.count() : ecoNewsRepo.countByAuthorId(authorId);
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
            fileService.delete(toUpdate.getImagePath());
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
        if (image != null) {
            fileService.delete(toUpdate.getImagePath());
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
        try {
            ecoNewsRepo.save(toUpdate);
        } catch (Exception e) {
            fileService.delete(toUpdate.getImagePath());
            throw new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public EcoNewsGenericDto update(UpdateEcoNewsDto updateEcoNewsDto, MultipartFile image, UserVO user) {
        EcoNews toUpdate = modelMapper.map(findById(updateEcoNewsDto.getId()), EcoNews.class);
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(toUpdate.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        enhanceWithNewData(toUpdate, updateEcoNewsDto, image);
        try {
            ecoNewsRepo.save(toUpdate);
        } catch (Exception e) {
            fileService.delete(toUpdate.getImagePath());
            throw new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED);
        }
        return getEcoNewsGenericDtoWithAllTags(toUpdate);
    }

    @Override
    public List<EcoNewsDto> getFavorites(String email) {
        User currentUser = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        List<EcoNews> favoriteEcoNews = new ArrayList<>(currentUser.getFavoriteEcoNews());

        return mapEcoNewsListToEcoNewsDtoList(favoriteEcoNews);
    }

    @Override
    public void addToFavorites(Long ecoNewsId, String email) {
        EcoNews ecoNews = ecoNewsRepo.findById(ecoNewsId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + ecoNewsId));

        User currentUser = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        if (ecoNews.getFollowers().contains(currentUser)) {
            throw new BadRequestException(ErrorMessage.USER_HAS_ALREADY_ADDED_ECO_NEW_TO_FAVORITES);
        }

        ecoNews.getFollowers().add(currentUser);
        ecoNewsRepo.save(ecoNews);
    }

    @Override
    public void removeFromFavorites(Long ecoNewsId, String email) {
        EcoNews ecoNews = ecoNewsRepo.findById(ecoNewsId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + ecoNewsId));

        User currentUser = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        if (!ecoNews.getFollowers().contains(currentUser)) {
            throw new BadRequestException(ErrorMessage.ECO_NEW_NOT_IN_FAVORITES);
        }

        ecoNews.setFollowers(ecoNews.getFollowers()
            .stream()
            .filter(user -> !user.getId().equals(currentUser.getId()))
            .collect(Collectors.toSet()));
        ecoNewsRepo.save(ecoNews);
    }

    @Override
    public PageableAdvancedDto<EcoNewsDto> getFilteredDataForManagementByPage(String query,
        Pageable pageable,
        EcoNewsViewDto ecoNewsViewDto,
        Locale locale) {
        Page<EcoNews> byQuery = null;
        boolean isQueryPresent = query != null && !query.isEmpty();
        if (isQueryPresent) {
            byQuery = ecoNewsRepo.searchEcoNewsBy(pageable, query);
        }
        Page<EcoNews> filteredByFields = null;
        boolean isFilterByFieldsPresent =
            (ecoNewsViewDto != null && !ecoNewsViewDto.isEmpty()) || pageable.getSort().isSorted();
        if (isFilterByFieldsPresent) {
            filteredByFields = ecoNewsRepo.findAll(getSpecification(ecoNewsViewDto), pageable);
        }
        if (isQueryPresent && isFilterByFieldsPresent) {
            return buildPageableAdvancedDto(getCommonEcoNews(filteredByFields, byQuery));
        } else if (isQueryPresent && byQuery != null) {
            return buildPageableAdvancedDto(byQuery);
        } else if (isFilterByFieldsPresent) {
            return buildPageableAdvancedDto(filteredByFields);
        } else {
            return buildPageableAdvancedDto(ecoNewsRepo.findAllByOrderByCreationDateDesc(pageable));
        }
    }

    private Page<EcoNews> getCommonEcoNews(Page<EcoNews> sortedPage, Page<EcoNews> page) {
        Iterator<EcoNews> iteratorByField = sortedPage.iterator();
        while (iteratorByField.hasNext()) {
            EcoNews currentEcoNews = iteratorByField.next();
            boolean isPresentByQuery = false;
            for (EcoNews ecoNewsByQuery : page) {
                if (currentEcoNews.getId().equals(ecoNewsByQuery.getId())) {
                    isPresentByQuery = true;
                    break;
                }
            }
            if (!isPresentByQuery) {
                iteratorByField.remove();
            }
        }
        return sortedPage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(UserVO userVO, Long id) {
        EcoNewsVO ecoNewsVO = findById(id);

        ecoNewsVO.getUsersDislikedNews().removeIf(u -> u.getId().equals(userVO.getId()));

        boolean isLiked = ecoNewsVO.getUsersLikedNews().stream()
            .anyMatch(u -> u.getId().equals(userVO.getId()));

        boolean isAuthor = ecoNewsVO.getAuthor().getId().equals(userVO.getId());

        if (isLiked) {
            achievementCalculation.calculateAchievement(userVO,
                AchievementCategoryType.LIKE_NEWS, AchievementAction.DELETE);
            ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("UNDO_LIKE_NEWS"),
                userVO);
            ecoNewsVO.getUsersLikedNews().removeIf(u -> u.getId().equals(userVO.getId()));
        } else {
            if (isAuthor) {
                throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
            }
            achievementCalculation.calculateAchievement(userVO,
                AchievementCategoryType.LIKE_NEWS, AchievementAction.ASSIGN);
            ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("LIKE_NEWS"), userVO);
            ecoNewsVO.getUsersLikedNews().add(userVO);
        }

        sendNotification(ecoNewsVO, userVO, !isLiked);
        ecoNewsRepo.save(modelMapper.map(ecoNewsVO, EcoNews.class));
    }

    private void sendNotification(EcoNewsVO ecoNewsVO, UserVO actionUser, boolean isLike) {
        UserVO targetUser = userService.findById(ecoNewsVO.getAuthor().getId());
        userNotificationService.createOrUpdateLikeNotification(
            targetUser, actionUser, ecoNewsVO.getId(), formatNewsTitle(ecoNewsVO.getTitle()),
            NotificationType.ECONEWS_LIKE, isLike);
    }

    private String formatNewsTitle(String newsTitle) {
        int maxLength = 20;
        if (newsTitle.length() > maxLength) {
            return newsTitle.substring(0, maxLength) + "...";
        }
        return newsTitle;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public Integer countLikesForEcoNews(Long id) {
        EcoNewsVO ecoNewsVO = findById(id);
        return ecoNewsVO.getUsersLikedNews().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer countDislikesForEcoNews(Long id) {
        EcoNewsVO ecoNewsV0 = findById(id);
        return ecoNewsV0.getUsersDislikedNews().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean checkNewsIsLikedByUser(Long id, Long userId) {
        EcoNewsVO ecoNewsVO = findById(id);
        return ecoNewsVO.getUsersLikedNews().stream().anyMatch(u -> u.getId().equals(userId));
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
        setValueIfNotEmpty(criteriaList, EcoNews_.ID, ecoNewsViewDto.getId());
        setValueIfNotEmpty(criteriaList, EcoNews_.TITLE, ecoNewsViewDto.getTitle());
        setValueIfNotEmpty(criteriaList, EcoNews_.AUTHOR, ecoNewsViewDto.getAuthor());
        setValueIfNotEmpty(criteriaList, EcoNews_.TEXT, ecoNewsViewDto.getText());
        setValueIfNotEmpty(criteriaList, EcoNews_.TAGS, ecoNewsViewDto.getTags());
        setValueIfNotEmpty(criteriaList, EcoNews_.HIDDEN, ecoNewsViewDto.getHidden());

        if ((ecoNewsViewDto.getStartDate() != null && !ecoNewsViewDto.getStartDate().isEmpty())
            && (ecoNewsViewDto.getEndDate() != null && !ecoNewsViewDto.getEndDate().isEmpty())) {
            SearchCriteria searchCriteria = SearchCriteria.builder()
                .key(EcoNews_.CREATION_DATE)
                .type("dateRange")
                .value(new String[] {ecoNewsViewDto.getStartDate(), ecoNewsViewDto.getEndDate()})
                .build();
            criteriaList.add(searchCriteria);
        } else {
            setValueIfNotEmpty(criteriaList, EcoNews_.CREATION_DATE, ecoNewsViewDto.getStartDate());
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

    private List<EcoNewsDto> mapEcoNewsListToEcoNewsDtoList(List<EcoNews> ecoNewsList) {
        return ecoNewsList.stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .toList();
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
        EcoNewsAuthorDto ecoNewsAuthorDto = new EcoNewsAuthorDto(author.getId(), author.getName());
        int countOfComments = commentService.countCommentsForEcoNews(ecoNews.getId());
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
            .hidden(ecoNews.isHidden())
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EcoNewContentSourceDto getContentAndSourceForEcoNewsById(Long id) {
        EcoNews ecoNews = ecoNewsRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + id));
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
        if (image != null) {
            toSave.setImagePath(fileService.upload(image));
        }

        Set<String> tagsSet = new HashSet<>(addEcoNewsDtoRequest.getTags());

        if (tagsSet.size() < addEcoNewsDtoRequest.getTags().size()) {
            throw new NotSavedException(ErrorMessage.DUPLICATE_TAGS);
        }

        List<TagVO> tagVOS = tagService.findTagsByNamesAndType(
            addEcoNewsDtoRequest.getTags(), TagType.ECO_NEWS);

        toSave.setTags(modelMapper.map(tagVOS,
            new TypeToken<List<Tag>>() {
            }.getType()));
        try {
            ecoNewsRepo.save(toSave);
        } catch (Exception e) {
            fileService.delete(toSave.getImagePath());
            throw new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED);
        }
        return toSave;
    }

    @Override
    public Set<UserVO> findUsersWhoLikedPost(Long id) {
        EcoNews ecoNews = ecoNewsRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + id));
        Set<User> usersLikedNews = ecoNews.getUsersLikedNews();
        return usersLikedNews.stream().map(u -> modelMapper.map(u, UserVO.class)).collect(Collectors.toSet());
    }

    @Override
    public Set<UserVO> findUsersWhoDislikedPost(Long id) {
        EcoNews ecoNews = ecoNewsRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + id));
        Set<User> usersDislikedNews = ecoNews.getUsersDislikedNews();
        return usersDislikedNews.stream().map(u -> modelMapper.map(u, UserVO.class)).collect(Collectors.toSet());
    }

    Predicate getPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder, List<String> tags, String title,
        Long authorId) {
        List<Predicate> predicates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tags)) {
            predicates.add(predicateForTags(criteriaBuilder, root, tags));
        }
        if (StringUtils.isNotEmpty(title)) {
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get(ECO_NEWS_TITLE)), '%' + title.toLowerCase() + '%'));
        }
        if (authorId != null) {
            Join<EcoNews, User> users = root.join("author");
            predicates.add(criteriaBuilder.equal(users.get(ECO_NEWS_AUTHOR_ID), authorId));
        }

        Predicate result;
        if (predicates.size() == 1) {
            result = predicates.getFirst();
        } else {
            result = predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }
        return result;
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

    /**
     * {@inheritDoc}
     *
     * @author Viktoriia Herchanivska.
     */
    @CacheEvict(value = CacheConstants.NEWEST_ECO_NEWS_CACHE_NAME, allEntries = true)
    @Override
    public void setHiddenValue(Long id, UserVO user, boolean value) {
        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        EcoNews ecoNews = ecoNewsRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + id));
        ecoNews.setHidden(value);

        ecoNewsRepo.save(ecoNews);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcoNewsDto> getThreeInterestingEcoNews() {
        return mapEcoNewsListToEcoNewsDtoList(ecoNewsRepo.findThreeInterestingEcoNews());
    }
}