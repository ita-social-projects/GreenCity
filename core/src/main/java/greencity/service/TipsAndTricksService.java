package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoManagement;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.TipsAndTricks;
import greencity.entity.TipsAndTricksComment;
import greencity.entity.User;
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
     * Method for updating {@link TipsAndTricks} instance.
     *
     * @param tricksDtoManagement - instance of {@link TipsAndTricksDtoManagement}.
     */
    void update(TipsAndTricksDtoManagement tricksDtoManagement, MultipartFile multipartFile);

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
     * Method for getting the {@link TipsAndTricks} instance by its id.
     *
     * @param id {@link TipsAndTricks} instance id.
     * @return {@link TipsAndTricks} instance.
     */
    TipsAndTricks findById(Long id);

    /**
     * Method for getting the {@link TipsAndTricksDtoManagement} instance by its id.
     *
     * @param id {@link TipsAndTricksDtoManagement} instance id.
     * @return {@link TipsAndTricksDtoManagement} instance.
     */
    TipsAndTricksDtoManagement findManagementDtoById(Long id);

    /**
     * Method for deleting the {@link TipsAndTricks} instance by its id.
     *
     * @param id - {@link TipsAndTricks} instance id which will be deleted.
     */
    void delete(Long id);

    /**
     * Method for deleting all {@link TipsAndTricks} instance by list of IDs.
     *
     * @param listId list of id {@link TipsAndTricks}
     */
    void deleteAll(List<Long> listId);

    /**
     * Method for getting Tips & Tricks by searchQuery.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchTipsAndTricksDto}
     */
    PageableDto<SearchTipsAndTricksDto> search(String searchQuery);

    /**
     * Method for getting Tips & Tricks by searchQuery.
     *
     * @param searchQuery query to search
     * @return list of {@link TipsAndTricksDtoResponse}
     */
    PageableDto<TipsAndTricksDtoResponse> searchBy(String searchQuery);

    /**
     * Method for getting amount of written tips and trick by user id.
     *
     * @param id {@link Long} user id.
     * @return amount of written tips and trick by user id.
     */
    Long getAmountOfWrittenTipsAndTrickByUserId(Long id);

    /**
     * Method to mark comment as liked by User.
     *
     * @param user    {@link User}.
     * @param comment {@link TipsAndTricksComment}
     * @author Dovganyuk Taras
     */
    void likeComment(User user, TipsAndTricksComment comment);

    /**
     * Method to mark comment as unliked by User.
     *
     * @param user    {@link User}.
     * @param comment {@link TipsAndTricksComment}
     * @author Dovganyuk Taras
     */
    void unlikeComment(User user, TipsAndTricksComment comment);
}
