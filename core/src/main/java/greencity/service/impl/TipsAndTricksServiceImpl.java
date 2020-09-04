package greencity.service.impl;

import greencity.annotations.RatingCalculation;
import greencity.annotations.RatingCalculationEnum;
import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoManagement;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.TipsAndTricks;
import greencity.entity.TipsAndTricksComment;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.TipsAndTricksRepo;
import greencity.service.FileService;
import greencity.service.TagsService;
import greencity.service.TipsAndTricksService;
import greencity.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
public class TipsAndTricksServiceImpl implements TipsAndTricksService {
    private final TipsAndTricksRepo tipsAndTricksRepo;

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final TagsService tagService;

    private final FileService fileService;

    /**
     * {@inheritDoc}
     */
    @RatingCalculation(rating = RatingCalculationEnum.ADD_TIPS_AND_TRICKS)
    @CacheEvict(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    public TipsAndTricksDtoResponse save(TipsAndTricksDtoRequest tipsAndTricksDtoRequest, MultipartFile image,
                                         String email) {
        TipsAndTricks toSave = modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class);
        toSave.setAuthor(userService.findByEmail(email));
        if (tipsAndTricksDtoRequest.getImage() != null) {
            image = fileService.convertToMultipartImage(tipsAndTricksDtoRequest.getImage());
        }
        if (image != null) {
            toSave.setImagePath(fileService.upload(image).toString());
        }
        toSave.setTags(
            tagService.findTipsAndTricksTagsByNames(tipsAndTricksDtoRequest.getTags()));
        try {
            tipsAndTricksRepo.save(toSave);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.TIPS_AND_TRICKS_NOT_SAVED);
        }

        return modelMapper.map(toSave, TipsAndTricksDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    public void update(TipsAndTricksDtoManagement tipsAndTricksDtoManagement,
                       MultipartFile image) {
        TipsAndTricks toUpdate = findById(tipsAndTricksDtoManagement.getId());
        toUpdate.setTitle(tipsAndTricksDtoManagement.getTitle());
        toUpdate.setText(tipsAndTricksDtoManagement.getText());
        toUpdate.setTags(tagService.findTipsAndTricksTagsByNames(tipsAndTricksDtoManagement.getTags()));
        toUpdate.setAuthor(userService.findByEmail(tipsAndTricksDtoManagement.getEmailAuthor()));
        if (image != null) {
            toUpdate.setImagePath(fileService.upload(image).toString());
        }
        tipsAndTricksRepo.save(toUpdate);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME)
    @Override
    public PageableDto<TipsAndTricksDtoResponse> findAll(Pageable page) {
        Page<TipsAndTricks> pages = tipsAndTricksRepo.findAllByOrderByCreationDateDesc(page);

        return getPagesWithTipsAndTricksResponseDto(pages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<TipsAndTricksDtoResponse> find(Pageable page, List<String> tags) {
        Page<TipsAndTricks> pages;
        if (tags == null || tags.isEmpty()) {
            pages = tipsAndTricksRepo.findAllByOrderByCreationDateDesc(page);
        } else {
            List<String> lowerCaseTags = tags.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
            pages = tipsAndTricksRepo.find(page, lowerCaseTags);
        }
        return getPagesWithTipsAndTricksResponseDto(pages);
    }

    private PageableDto<TipsAndTricksDtoResponse> getPagesWithTipsAndTricksResponseDto(Page<TipsAndTricks> pages) {
        List<TipsAndTricksDtoResponse> tipsAndTricksDtos = pages
            .stream()
            .map(tipsAndTricks -> modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            tipsAndTricksDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipsAndTricksDtoResponse findDtoById(Long id) {
        TipsAndTricks tipsAndTricks = findById(id);
        return modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @RatingCalculation(rating = RatingCalculationEnum.DELETE_TIPS_AND_TRICKS)
    @CacheEvict(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    public void delete(Long id) {
        tipsAndTricksRepo.deleteById(findById(id).getId());
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    public void deleteAll(List<Long> listId) {
        listId.forEach(id -> tipsAndTricksRepo.deleteById(findById(id).getId()));
    }

    /**
     * {@inheritDoc}
     */
    public TipsAndTricks findById(Long id) {
        return tipsAndTricksRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TIPS_AND_TRICKS_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipsAndTricksDtoManagement findManagementDtoById(Long id) {
        TipsAndTricks tipsAndTricks = findById(id);
        return modelMapper.map(tipsAndTricks, TipsAndTricksDtoManagement.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchTipsAndTricksDto> search(String searchQuery) {
        Page<TipsAndTricks> page = tipsAndTricksRepo.searchTipsAndTricks(PageRequest.of(0, 3), searchQuery);

        List<SearchTipsAndTricksDto> tipsAndTricksDtos = page.stream()
            .map(tipsAndTricks -> modelMapper.map(tipsAndTricks, SearchTipsAndTricksDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            tipsAndTricksDtos,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages()
        );
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
    public void likeComment(User user, TipsAndTricksComment comment) {
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
    public void unlikeComment(User user, TipsAndTricksComment comment) {
        comment.getUsersLiked().remove(user);
    }
}
