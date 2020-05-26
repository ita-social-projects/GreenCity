package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.tipsandtricks.AddTipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.AddTipsAndTricksDtoResponse;
import greencity.dto.tipsandtricks.TipsAndTricksDto;
import greencity.entity.TipsAndTricks;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface TipsAndTricksService {
    /**
     * Method for creating {@link TipsAndTricks} instance.
     *
     * @param addTipsAndTricksDtoRequest - dto with {@link TipsAndTricks} title and text.
     * @return {@link AddTipsAndTricksDtoResponse} instance.
     */
    AddTipsAndTricksDtoResponse save(AddTipsAndTricksDtoRequest addTipsAndTricksDtoRequest, MultipartFile image,
                                     String email);

    /**
     * Method for getting all tips & tricks by page.
     *
     * @param page to search for.
     * @return PageableDto of {@link TipsAndTricksDto} instances.
     */
    PageableDto<TipsAndTricksDto> findAll(Pageable page);

    /**
     * Method for getting tips & tricks by params.
     *
     * @param page to search for.
     * @param tags used to search for.
     * @return PageableDto with {@link TipsAndTricksDto} instance.
     */
    PageableDto<TipsAndTricksDto> find(Pageable page, List<String> tags);

    /**
     * Method for getting the {@link TipsAndTricks} instance by its id.
     *
     * @param id {@link TipsAndTricks} instance id.
     * @return {@link TipsAndTricks} instance.
     */
    TipsAndTricks findById(Long id);

    /**
     * Method for getting the {@link TipsAndTricksDto} instance by its id.
     *
     * @param id {@link TipsAndTricksDto} instance id.
     * @return {@link TipsAndTricksDto} instance.
     */
    TipsAndTricksDto findDtoById(Long id);

    /**
     * Method for deleting the {@link TipsAndTricks} instance by its id.
     *
     * @param id - {@link TipsAndTricks} instance id which will be deleted.
     */
    void delete(Long id);
}
