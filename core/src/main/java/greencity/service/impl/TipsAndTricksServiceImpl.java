package greencity.service.impl;

import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.TipsAndTricks;
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
    @CacheEvict(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    public TipsAndTricksDtoResponse save(TipsAndTricksDtoRequest tipsAndTricksDtoRequest, MultipartFile image,
                                         String email) {
        TipsAndTricks toSave = modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class);
        toSave.setAuthor(userService.findByEmail(email));
        if (tipsAndTricksDtoRequest.getImage() != null) {
            image = modelMapper.map(tipsAndTricksDtoRequest.getImage(), MultipartFile.class);
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
            pages.getPageable().getPageNumber()
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
    @CacheEvict(value = CacheConstants.TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    public void delete(Long id) {
        tipsAndTricksRepo.deleteById(findById(id).getId());
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
    public PageableDto<SearchTipsAndTricksDto> search(String searchQuery) {
        Page<TipsAndTricks> page = tipsAndTricksRepo.searchTipsAndTricks(PageRequest.of(0, 3), searchQuery);

        List<SearchTipsAndTricksDto> tipsAndTricksDtos = page.stream()
            .map(tipsAndTricks -> modelMapper.map(tipsAndTricks, SearchTipsAndTricksDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            tipsAndTricksDtos,
            page.getTotalElements(),
            page.getPageable().getPageNumber()
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
}
