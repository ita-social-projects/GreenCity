package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.GetEcoNewsDto;
import greencity.entity.EcoNews;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface EcoNewsService {
    /**
     * Method for creating {@link EcoNews} instance.
     *
     * @param addEcoNewsDtoRequest - dto with {@link EcoNews} title, text, image path.
     * @return {@link AddEcoNewsDtoResponse} instance.
     */
    AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest);

    /**
     * Method for getting last three eco news.
     *
     * @param languageCode needed language code.
     * @return list of {@link EcoNewsDto} instances.
     */
    List<EcoNewsDto> getThreeLastEcoNews(String languageCode);

    /**
     * Method for getting all eco news by page.
     *
     * @param page         parameters of to search.
     * @param languageCode needed language code.
     * @return PageableDto of {@link EcoNewsDto} instances.
     */
    PageableDto<EcoNewsDto> findAll(Pageable page, String languageCode);

    /**
     * Method for getting all eco news by params.
     *
     * @param getEcoNewsDto needed params to search.
     * @param page          parameters of to search.
     * @return PageableDto with {@link EcoNewsDto} instance.
     */
    PageableDto<EcoNewsDto> find(Pageable page, GetEcoNewsDto getEcoNewsDto);

    /**
     * Method for getting the {@link EcoNews} instance by its id.
     *
     * @param id {@link EcoNews} instance id.
     * @return {@link EcoNews} instance.
     */
    EcoNews findById(Long id);

    /**
     * Method for deleting the {@link EcoNews} instance by its id.
     *
     * @param id - {@link EcoNews} instance id which will be deleted.
     */
    void delete(Long id);
}
