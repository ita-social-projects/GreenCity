package greencity.service;

import greencity.annotations.RatingCalculation;
import greencity.annotations.RatingCalculationEnum;
import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.tipsandtricks.*;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.filters.SearchCriteria;
import greencity.filters.TipsAndTricksSpecification;
import greencity.repository.TipsAndTricksRepo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@EnableCaching
@RequiredArgsConstructor
public class TipsAndTricksServiceImpl implements TipsAndTricksService {
    private final TipsAndTricksRepo tipsAndTricksRepo;

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final TagsService tagService;

    private final FileService fileService;

    private final BeanFactory beanFactory;

    private final LanguageService languageService;

    private final TipsAndTricksTranslationService tipsAndTricksTranslationService;

    /**
     * {@inheritDoc}
     */
    @RatingCalculation(rating = RatingCalculationEnum.ADD_TIPS_AND_TRICKS)
    @CacheEvict(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    @Transactional
    public TipsAndTricksDtoResponse save(TipsAndTricksDtoRequest tipsAndTricksDtoRequest, MultipartFile image,
        String email) {
        TipsAndTricks toSave = modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class);
        toSave.setAuthor(modelMapper.map(userService.findByEmail(email), User.class));
        if (tipsAndTricksDtoRequest.getImage() != null) {
            image = fileService.convertToMultipartImage(tipsAndTricksDtoRequest.getImage());
        }
        if (image != null) {
            toSave.setImagePath(fileService.upload(image).toString());
        }
        toSave.setTags(modelMapper.map(tagService.findTipsAndTricksTagsByNames(tipsAndTricksDtoRequest.getTags()),
            new TypeToken<List<Tag>>() {
            }.getType()));
        toSave.getTitleTranslations().forEach(el -> el.setTipsAndTricks(toSave));
        toSave.getTextTranslations().forEach(el -> el.setTipsAndTricks(toSave));
        try {
            tipsAndTricksRepo.save(toSave);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.TIPS_AND_TRICKS_NOT_SAVED);
        }
        tipsAndTricksTranslationService.saveTitleTranslations(modelMapper.map(toSave.getTitleTranslations(),
            new TypeToken<List<TitleTranslationVO>>() {
            }.getType()));
        tipsAndTricksTranslationService.saveTextTranslations(modelMapper.map(toSave.getTextTranslations(),
            new TypeToken<List<TextTranslationVO>>() {
            }.getType()));

        return modelMapper.map(toSave, TipsAndTricksDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TipsAndTricksDtoManagement saveTipsAndTricksWithTranslations(
        TipsAndTricksDtoManagement tipsAndTricksDtoManagement,
        MultipartFile image,
        String email) {
        TipsAndTricks tipsAndTricks = TipsAndTricks.builder()
            .source(tipsAndTricksDtoManagement.getSource())
            .creationDate(ZonedDateTime.now())
            .titleTranslations(
                tipsAndTricksDtoManagement.getTitleTranslations().stream()
                    .map(el -> TitleTranslation.builder()
                        .content(el.getContent())
                        .language(modelMapper.map(languageService.findByCode(el.getLanguageCode()),
                            Language.class))
                        .build())
                    .collect(Collectors.toList()))
            .textTranslations(
                tipsAndTricksDtoManagement.getTextTranslations().stream()
                    .map(el -> TextTranslation.builder()
                        .content(el.getContent())
                        .language(modelMapper.map(languageService.findByCode(el.getLanguageCode()),
                            Language.class))
                        .build())
                    .collect(Collectors.toList()))
            .build();
        tipsAndTricks.setAuthor(modelMapper.map(userService.findByEmail(email), User.class));
        tipsAndTricks.getTitleTranslations().forEach(el -> el.setTipsAndTricks(tipsAndTricks));
        tipsAndTricks.getTextTranslations().forEach(el -> el.setTipsAndTricks(tipsAndTricks));
        if (tipsAndTricksDtoManagement.getImagePath() != null) {
            image = fileService.convertToMultipartImage(tipsAndTricksDtoManagement.getImagePath());
        }
        if (image != null) {
            tipsAndTricks.setImagePath(fileService.upload(image).toString());
        }
        tipsAndTricks
            .setTags(modelMapper.map(tagService.findTipsAndTricksTagsByNames(tipsAndTricksDtoManagement.getTags()),
                new TypeToken<List<Tag>>() {
                }.getType()));

        tipsAndTricksRepo.save(tipsAndTricks);
        tipsAndTricksTranslationService.saveTitleTranslations(modelMapper.map(tipsAndTricks.getTitleTranslations(),
            new TypeToken<List<TitleTranslationVO>>() {
            }.getType()));
        tipsAndTricksTranslationService.saveTextTranslations(modelMapper.map(tipsAndTricks.getTextTranslations(),
            new TypeToken<List<TextTranslationVO>>() {
            }.getType()));

        return tipsAndTricksDtoManagement;
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    public void update(TipsAndTricksDtoManagement tipsAndTricksDtoManagement,
        MultipartFile image) {
        TipsAndTricks toUpdate = findTipsAndTricksById(tipsAndTricksDtoManagement.getId());
        toUpdate.setSource(tipsAndTricksDtoManagement.getSource());
        toUpdate.setTags(modelMapper.map(tagService.findTipsAndTricksTagsByNames(tipsAndTricksDtoManagement.getTags()),
            new TypeToken<List<Tag>>() {
            }.getType()));
        if (image != null) {
            toUpdate.setImagePath(fileService.upload(image).toString());
        }
        toUpdate.getTitleTranslations()
            .forEach(titleTranslation -> titleTranslation
                .setContent(tipsAndTricksDtoManagement.getTitleTranslations().stream()
                    .filter(elem -> elem.getLanguageCode().equals(titleTranslation.getLanguage().getCode()))
                    .findFirst().orElseThrow(RuntimeException::new).getContent()));

        toUpdate.getTextTranslations()
            .forEach(
                textTranslation -> textTranslation.setContent(tipsAndTricksDtoManagement.getTextTranslations().stream()
                    .filter(elem -> elem.getLanguageCode().equals(textTranslation.getLanguage().getCode()))
                    .findFirst().orElseThrow(RuntimeException::new).getContent()));

        tipsAndTricksRepo.save(toUpdate);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME)
    @Override
    public PageableDto<TipsAndTricksDtoManagement> findAllManagementDtos(Pageable page) {
        Page<TipsAndTricks> pages = tipsAndTricksRepo.findAllByOrderByCreationDateDesc(page);

        return getPagesWithTipsAndTricksDtoManagement(pages);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME)
    @Override
    public PageableDto<TipsAndTricksDtoResponse> findAll(Pageable page) {
        Page<TipsAndTricks> pages = tipsAndTricksRepo
            .findByTitleTranslationsLanguageCodeOrderByCreationDateDesc(
                languageService.extractLanguageCodeFromRequest(), page);

        return getPagesWithTipsAndTricksDtoResponse(pages);
    }

    private PageableDto<TipsAndTricksDtoResponse> getPagesWithTipsAndTricksDtoResponse(Page<TipsAndTricks> pages) {
        List<TipsAndTricksDtoResponse> tipsAndTricksDtos = pages
            .stream()
            .map(tipsAndTricks -> modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class))
            .collect(Collectors.toList());
        return new PageableDto<>(
            tipsAndTricksDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<TipsAndTricksDtoResponse> find(Pageable page, List<String> tags) {
        Page<TipsAndTricks> pages;
        String languageCode = languageService.extractLanguageCodeFromRequest();
        if (tags == null || tags.isEmpty()) {
            pages = tipsAndTricksRepo
                .findByTitleTranslationsLanguageCodeOrderByCreationDateDesc(
                    languageCode,
                    page);
        } else {
            List<String> lowerCaseTags = tags.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
            pages = tipsAndTricksRepo.find(languageCode, page, lowerCaseTags);
        }
        return getPagesWithTipsAndTricksDtoResponse(pages);
    }

    private PageableDto<TipsAndTricksDtoManagement> getPagesWithTipsAndTricksDtoManagement(Page<TipsAndTricks> pages) {
        List<TipsAndTricksDtoManagement> tipsAndTricksDtos = pages
            .stream()
            .map(tipsAndTricks -> modelMapper.map(tipsAndTricks, TipsAndTricksDtoManagement.class))
            .collect(Collectors.toList());
        return new PageableDto<>(
            tipsAndTricksDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipsAndTricksDtoResponse findDtoById(Long id) {
        TipsAndTricks tipsAndTricks = findTipsAndTricksByIdAndLanguageCode(
            languageService.extractLanguageCodeFromRequest(), id);
        return modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @RatingCalculation(rating = RatingCalculationEnum.DELETE_TIPS_AND_TRICKS)
    @CacheEvict(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    public void delete(Long id) {
        tipsAndTricksRepo.deleteById(findTipsAndTricksById(id).getId());
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    public void deleteAll(List<Long> listId) {
        listId.forEach(id -> tipsAndTricksRepo.deleteById(findTipsAndTricksById(id).getId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipsAndTricksVO findById(Long id) {
        return tipsAndTricksRepo
            .findById(id).map(tipsAndTricks -> modelMapper.map(tipsAndTricks, TipsAndTricksVO.class))
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TIPS_AND_TRICKS_NOT_FOUND_BY_ID + id));
    }

    private TipsAndTricks findTipsAndTricksById(Long id) {
        return tipsAndTricksRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TIPS_AND_TRICKS_NOT_FOUND_BY_ID + id));
    }

    private TipsAndTricks findTipsAndTricksByIdAndLanguageCode(String languageCode, Long id) {
        return tipsAndTricksRepo
            .findByIdAndTitleTranslationsLanguageCode(id, languageCode)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TIPS_AND_TRICKS_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipsAndTricksDtoManagement findManagementDtoById(Long id) {
        TipsAndTricks tipsAndTricks = findTipsAndTricksById(id);
        return modelMapper.map(tipsAndTricks, TipsAndTricksDtoManagement.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchTipsAndTricksDto> search(String searchQuery) {
        Page<TipsAndTricks> page = tipsAndTricksRepo.searchTipsAndTricks(PageRequest.of(0, 3), searchQuery,
            languageService.extractLanguageCodeFromRequest());

        return getSearchTipsAndTricksDtoPageableDto(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchTipsAndTricksDto> search(Pageable pageable, String searchQuery) {
        Page<TipsAndTricks> page = tipsAndTricksRepo.searchTipsAndTricks(pageable, searchQuery,
            languageService.extractLanguageCodeFromRequest());

        return getSearchTipsAndTricksDtoPageableDto(page);
    }

    private PageableDto<SearchTipsAndTricksDto> getSearchTipsAndTricksDtoPageableDto(Page<TipsAndTricks> page) {
        List<SearchTipsAndTricksDto> tipsAndTricksDtos = page.stream()
            .map(tipsAndTricks -> modelMapper.map(tipsAndTricks, SearchTipsAndTricksDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            tipsAndTricksDtos,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<TipsAndTricksDtoResponse> searchBy(Pageable pageable, String searchQuery) {
        Page<TipsAndTricks> page = tipsAndTricksRepo
            .searchBy(pageable, searchQuery, languageService.extractLanguageCodeFromRequest());
        return getPagesWithTipsAndTricksDtoResponse(page);
    }

    /**
     * Method for getting amount of written tips and trick by user id.
     *
     * @param id {@link Long} user id.
     * @return amount of written tips and trick by user id.
     * @author Marian Datsko
     */
    @Override
    public Long getAmountOfWrittenTipsAndTrickByUserId(Long id) {
        return tipsAndTricksRepo.getAmountOfWrittenTipsAndTrickByUserId(id);
    }

    /**
     * Method to mark comment as liked by User.
     *
     * @param user    {@link User}.
     * @param comment {@link TipsAndTricksComment}
     * @author Dovganyuk Taras
     */
    @RatingCalculation(rating = RatingCalculationEnum.LIKE_COMMENT)
    public void likeComment(UserVO user, TipsAndTricksCommentVO comment) {
        comment.getUsersLiked().add(user);
    }

    /**
     * Method to mark comment as unliked by User.
     *
     * @param user    {@link User}.
     * @param comment {@link TipsAndTricksComment}
     * @author Dovganyuk Taras
     */
    @RatingCalculation(rating = RatingCalculationEnum.UNLIKE_COMMENT)
    public void unlikeComment(UserVO user, TipsAndTricksCommentVO comment) {
        comment.getUsersLiked().removeIf(u -> u.getId().equals(user.getId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<TipsAndTricksDtoManagement> getFilteredDataForManagementByPage(
        Pageable pageable,
        TipsAndTricksViewDto tipsAndTricksViewDto) {
        Page<TipsAndTricks> pages = tipsAndTricksRepo.findAll(getSpecification(tipsAndTricksViewDto), pageable);
        return getPagesWithTipsAndTricksDtoManagement(pages);
    }

    /**
     * * This method used for build {@link SearchCriteria} depends on
     * {@link TipsAndTricksViewDto}.
     *
     * @param tipsAndTricksViewDto used for receive parameters for filters from UI.
     * @return {@link SearchCriteria}.
     */
    private List<SearchCriteria> buildSearchCriteria(TipsAndTricksViewDto tipsAndTricksViewDto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        SearchCriteria searchCriteria;
        if (!tipsAndTricksViewDto.getId().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(tipsAndTricksViewDto.getId())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!tipsAndTricksViewDto.getTitle().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("title")
                .type("title")
                .value(tipsAndTricksViewDto.getTitle())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!tipsAndTricksViewDto.getAuthor().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("author")
                .type("author")
                .value(tipsAndTricksViewDto.getAuthor())
                .build();
            criteriaList.add(searchCriteria);
        }
        if (!tipsAndTricksViewDto.getStartDate().isEmpty() && !tipsAndTricksViewDto.getEndDate().isEmpty()) {
            searchCriteria = SearchCriteria.builder()
                .key("creationDate")
                .type("dateRange")
                .value(new String[] {tipsAndTricksViewDto.getStartDate(), tipsAndTricksViewDto.getEndDate()})
                .build();
            criteriaList.add(searchCriteria);
        }
        return criteriaList;
    }

    /**
     * Returns {@link TipsAndTricksSpecification} for entered filter parameters.
     *
     * @param tipsAndTricksViewDto contains data from filters
     */
    private TipsAndTricksSpecification getSpecification(TipsAndTricksViewDto tipsAndTricksViewDto) {
        List<SearchCriteria> searchCriteria = buildSearchCriteria(tipsAndTricksViewDto);
        return new TipsAndTricksSpecification(searchCriteria);
    }
}
