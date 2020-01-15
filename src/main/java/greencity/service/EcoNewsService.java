package greencity.service;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EcoNewsService {
    /**
     * Method for creating {@link EcoNews} instance.
     *
     * @param addEcoNewsDtoRequest - dto with {@link EcoNews} title, text.
     * @param languageCode         - code of the needed language.
     * @param multipartFile - image to save.
     * @return {@link AddEcoNewsDtoResponse} instance.
     */
    AddEcoNewsDtoResponse save(MultipartFile multipartFile, AddEcoNewsDtoRequest addEcoNewsDtoRequest);

    /**
     * Method for getting last three eco news.
     *
     * @param languageCode needed language code.
     * @return list of {@link EcoNewsDto} instances.
     */
    List<EcoNewsDto> getThreeLastEcoNews(String languageCode);

    /**
     * Method for getting all eco news.
     *
     * @param languageCode needed language code.
     * @return list of {@link EcoNewsDto} instances.
     */
    List<EcoNewsDto> findAll(String languageCode);

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
