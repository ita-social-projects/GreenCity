package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.tipsandtricks.*;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO;
import greencity.dto.user.UserVO;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface TipsAndTricksService {
    /**
     * Method for creating {@link TipsAndTricksDtoResponse} instance.
     *
     * @param tipsAndTricksDtoRequest {@link TipsAndTricksDtoRequest}
     * @return {@link TipsAndTricksDtoResponse} instance.
     */
    TipsAndTricksDtoResponse save(TipsAndTricksDtoRequest tipsAndTricksDtoRequest, MultipartFile image,
                                  String email);

    /**
     * Method saves new {@link TipsAndTricksDtoManagement}, {@link TitleTranslationVO}  and {@link TextTranslationVO}.
     *
     * @param tipsAndTricksDtoManagement {@link TipsAndTricksDtoManagement}
     * @return instance of {@link TipsAndTricksDtoManagement}
     */
    TipsAndTricksDtoManagement saveTipsAndTricksWithTranslations(TipsAndTricksDtoManagement tipsAndTricksDtoManagement,
                                                                 MultipartFile image,
                                                                 String name);

    /**
     * Method for updating {@link TipsAndTricksVO} instance.
     *
     * @param tricksDtoManagement - instance of {@link TipsAndTricksDtoManagement}.
     */
    void update(TipsAndTricksDtoManagement tricksDtoManagement, MultipartFile multipartFile);

    /**
     * Method for getting all tips & tricks by page.
     *
     * @param page to search for.
     * @return PageableDto of {@link TipsAndTricksDtoManagement} instances.
     */
    PageableDto<TipsAndTricksDtoManagement> findAllManagementDtos(Pageable page);

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
     * Method for getting the {@link TipsAndTricksVO} instance by its id.
     *
     * @param id {@link TipsAndTricksVO} instance id.
     * @return {@link TipsAndTricksVO} instance.
     */
    TipsAndTricksVO findById(Long id);

    /**
     * Method for getting the {@link TipsAndTricksDtoManagement} instance by its id.
     *
     * @param id {@link TipsAndTricksDtoManagement} instance id.
     * @return {@link TipsAndTricksDtoManagement} instance.
     */
    TipsAndTricksDtoManagement findManagementDtoById(Long id);

    /**
     * Method for deleting the {@link TipsAndTricksVO} instance by its id.
     *
     * @param id - {@link TipsAndTricksVO} instance id which will be deleted.
     */
    void delete(Long id);

    /**
     * Method for deleting all {@link TipsAndTricksVO} instance by list of IDs.
     *
     * @param listId list of id {@link TipsAndTricksVO}
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
     * Method for getting all Tips & Tricks by searchQuery.
     *
     * @param pageable    {@link Pageable}.
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchTipsAndTricksDto} instances.
     */
    PageableDto<SearchTipsAndTricksDto> search(Pageable pageable, String searchQuery);

    /**
     * Method for getting Tips & Tricks by searchQuery.
     *
     * @param pageable    {@link Pageable}.
     * @param searchQuery query to search,
     * @return PageableDto of {@link TipsAndTricksDtoResponse} instances.
     */
    PageableDto<TipsAndTricksDtoResponse> searchBy(Pageable pageable, String searchQuery);

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
     * @param user    {@link UserVO}.
     * @param comment {@link TipsAndTricksCommentVO}
     * @author Dovganyuk Taras
     */
    void likeComment(UserVO user, TipsAndTricksCommentVO comment);

    /**
     * Method to mark comment as unliked by User.
     *
     * @param user    {@link UserVO}.
     * @param comment {@link TipsAndTricksCommentVO}
     * @author Dovganyuk Taras
     */
    void unlikeComment(UserVO user, TipsAndTricksCommentVO comment);

    /**
     * Method for finding {@link TipsAndTricksDtoManagement} by specification.
     *
     * @return a dto of {@link PageableDto}.
     */
    PageableDto<TipsAndTricksDtoManagement> getFilteredDataForManagementByPage(
        Pageable pageable, TipsAndTricksViewDto tipsAndTricksViewDto);
}
