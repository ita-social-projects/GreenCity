package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.search.SearchNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EcoNewsService {
    /**
     * Method for creating {@link EcoNews} instance.
     *
     * @param addEcoNewsDtoRequest - dto with {@link EcoNews} title, text, image path.
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
    PageableDto<EcoNewsDto> findAll(Pageable page);

    /**
     * Method for getting eco news by params.
     *
     * @param page parameters of to search.
     * @param tags tags to search.
     * @return PageableDto with {@link EcoNewsDto} instance.
     */
    PageableDto<EcoNewsDto> find(Pageable page, List<String> tags);

    /**
     * Method for getting the {@link EcoNews} instance by its id.
     *
     * @param id {@link EcoNews} instance id.
     * @return {@link EcoNews} instance.
     */
    EcoNews findById(Long id);

    /**
     * Method for getting the {@link EcoNewsDto} instance by its id.
     *
     * @param id {@link EcoNewsDto} instance id.
     * @return {@link EcoNewsDto} instance.
     */
    EcoNewsDto findDtoById(Long id);

    /**
     * Method for deleting the {@link EcoNews} instance by its id.
     *
     * @param id - {@link EcoNews} instance id which will be deleted.
     */
    void delete(Long id);

    /**
     * Method for getting EcoNews by searchQuery.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchNewsDto}
     * @author Kovaliv Taras
     */
    PageableDto<SearchNewsDto> search(String searchQuery);

    /**
     * Method for getting all EcoNews by searchQuery.
     *
     * @param pageable    {@link Pageable}.
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchNewsDto} instances.
     * @author Yurii Savchenko
     */
    PageableDto<SearchNewsDto> search(Pageable pageable, String searchQuery);

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
     * @param user    {@link User}.
     * @param comment {@link EcoNewsComment}
     * @author Dovganyuk Taras
     */
    void likeComment(User user, EcoNewsComment comment);

    /**
     * Method to mark comment as unliked by User.
     *
     * @param user    {@link User}.
     * @param comment {@link EcoNewsComment}
     * @author Dovganyuk Taras
     */
    void unlikeComment(User user, EcoNewsComment comment);
}
