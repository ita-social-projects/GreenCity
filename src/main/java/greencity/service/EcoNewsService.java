package greencity.service;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import java.util.List;

public interface EcoNewsService {
    /**
     * DAsdas.
     */
    AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest);

    /**
     * dasdas.
     */
    List<EcoNewsDto> getThreeLastEcoNews();

    /**
     * dsad.
     */
    List<EcoNewsDto> findAll();

    /**
     * dsafa.
     */
    EcoNews findById(Long id);

    /**
     * dsads.
     */
    Long delete(Long id);
}
