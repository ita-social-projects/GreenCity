package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.TipsAndTricks;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface TipsAndTricksService {
    /**
     * Method for creating {@link TipsAndTricks} instance.
     *
     * @param tipsAndTricksDtoRequest - dto with {@link TipsAndTricks} title and text.
     * @return {@link TipsAndTricksDtoResponse} instance.
     */
    TipsAndTricksDtoResponse save(TipsAndTricksDtoRequest tipsAndTricksDtoRequest, MultipartFile image,
                                  String email);

    /**
     * Method for getting all tips & tricks by page.
     *
     * @param page to search for.
     * @return PageableDto of {@link TipsAndTricksDtoResponse} instances.
     */
    PageableDto<TipsAndTricksDtoResponse> findAll(Pageable page);

    /**
     * Method for getting tips & tricks by params.
     *
     * @param page to search for.
     * @param tags used to search for.
     * @return PageableDto with {@link TipsAndTricksDtoResponse} instance.
     */
    PageableDto<TipsAndTricksDtoResponse> find(Pageable page, List<String> tags);

    /**
     * Method for getting the {@link TipsAndTricksDtoResponse} instance by its id.
     *
     * @param id {@link TipsAndTricksDtoResponse} instance id.
     * @return {@link TipsAndTricksDtoResponse} instance.
     */
    TipsAndTricksDtoResponse findDtoById(Long id);

    /**
     * Method for deleting the {@link TipsAndTricks} instance by its id.
     *
     * @param id - {@link TipsAndTricks} instance id which will be deleted.
     */
    void delete(Long id);

    /**
     * Method for getting Tips & Tricks by searchQuery.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchTipsAndTricksDto}
     */
    PageableDto<SearchTipsAndTricksDto> search(String searchQuery);
}
