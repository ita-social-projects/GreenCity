package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.econews.*;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EcoNewsService {
    /**
     * Method for creating {@link EcoNewsVO} instance.
     *
     * @param addEcoNewsDtoRequest - dto with {@link EcoNewsVO} title, text, image
     *                             path.
     * @return {@link AddEcoNewsDtoResponse} instance.
     */
    AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest, MultipartFile image, String email);

    /**
     * Method for getting last three eco news.
     *
     * @return list of {@link EcoNewsDto} instances.
     */

    List<EcoNewsDto> getThreeLastEcoNews();

    /**
     * Method for getting three recommended eco news.
     *
     * @param openedEcoNewsId don't include into the list
     * @return list of three {@link EcoNewsDto} instances.
     */
    List<EcoNewsDto> getThreeRecommendedEcoNews(Long openedEcoNewsId);

    /**
     * Method for getting all eco news by page.
     *
     * @param page parameters of to search.
     * @return PageableDto of {@link EcoNewsDto} instances.
     */
    PageableAdvancedDto<EcoNewsDto> findAll(Pageable page);

    /**
     * Method for getting eco news by params.
     *
     * @param page parameters of to search.
     * @param tags tags to search.
     * @return PageableDto with {@link EcoNewsDto} instance.
     */
    PageableAdvancedDto<EcoNewsDto> find(Pageable page, List<String> tags);

    /**
     * Method for getting the {@link EcoNewsVO} instance by its id.
     *
     * @param id {@link EcoNewsVO} instance id.
     * @return {@link EcoNewsVO} instance.
     */
    EcoNewsVO findById(Long id);

    /**
     * Method for getting the {@link EcoNewsDto} instance by its id.
     *
     * @param id {@link EcoNewsDto} instance id.
     * @return {@link EcoNewsDto} instance.
     */
    EcoNewsDto findDtoById(Long id);

    /**
     * Method for deleting the {@link EcoNewsVO} instance by its id.
     *
     * @param id   - {@link EcoNewsVO} instance id which will be deleted.
     * @param user current {@link UserVO} that wants to delete.
     */
    void delete(Long id, UserVO user);

    /**
     * Method deletes all {@link EcoNewsVO} by list of ids.
     *
     * @param listId list of id {@link EcoNewsVO}
     */
    void deleteAll(List<Long> listId);

    /**
     * Method for getting EcoNews by searchQuery.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchNewsDto}
     * @author Kovaliv Taras
     */
    PageableDto<SearchNewsDto> search(String searchQuery, String languageCode);

    /**
     * Method for getting all EcoNews by searchQuery.
     *
     * @param pageable    {@link Pageable}.
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchNewsDto} instances.
     * @author Yurii Savchenko
     */
    PageableDto<SearchNewsDto> search(Pageable pageable, String searchQuery, String languageCode);

    /**
     * Method for getting amount of published news by user id.
     *
     * @param id {@link Long} user id.
     * @return amount of published news by user id.
     */
    Long getAmountOfPublishedNewsByUserId(Long id);

    /**
     * Method to mark comment as liked by User.
     *
     * @param user    {@link UserVO}.
     * @param comment {@link EcoNewsCommentVO}
     * @author Dovganyuk Taras
     */
    void likeComment(UserVO user, EcoNewsCommentVO comment);

    /**
     * Method to mark comment as unliked by User.
     *
     * @param user    {@link UserVO}.
     * @param comment {@link EcoNewsCommentVO}
     * @author Dovganyuk Taras
     */
    void unlikeComment(UserVO user, EcoNewsCommentVO comment);

    /**
     * Method returns {@link EcoNewsDto} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link EcoNewsDto}.
     */
    PageableAdvancedDto<EcoNewsDto> searchEcoNewsBy(Pageable paging, String query);

    /**
     * Method for updating {@link EcoNewsVO} instance.
     *
     * @param ecoNewsDtoManagement - instance of {@link EcoNewsDtoManagement}.
     */
    void update(EcoNewsDtoManagement ecoNewsDtoManagement, MultipartFile multipartFile);

    /**
     * Method for updating {@link EcoNewsVO} instance.
     *
     * @param updateEcoNewsDto - instance of {@link UpdateEcoNewsDto}.
     * @return instance of {@link EcoNewsDto};=.
     */
    EcoNewsDto update(UpdateEcoNewsDto updateEcoNewsDto, MultipartFile multipartFile, UserVO user);

    /**
     * Find {@link EcoNewsVO} for management.
     *
     * @return a dto of {@link PageableDto}.
     * @author Dovganyuk Taras
     */
    PageableAdvancedDto<EcoNewsDto> getFilteredDataForManagementByPage(
        Pageable pageable, EcoNewsViewDto ecoNewsViewDto);
}
