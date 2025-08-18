package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.user.UserVO;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface EcoNewsRelevanceService {
    /**
     * Finds relevant eco news by user.
     *
     * @param page   page settings
     * @param tags   tags to filter
     * @param title  title to filter
     * @param author author to filter
     * @param user   currently authorized user
     * @return page of relevant eco news
     */
    PageableAdvancedDto<EcoNewsGenericDto> findRelevantEcoNews(Pageable page,
        List<String> tags,
        String title,
        String author,
        UserVO user);

    /**
     * Marks relevance of given eco news as outdated. This method is used after some
     * changes in eco news.
     *
     * @param ecoNews eco news to mark relevance as outdated
     */
    void markRelevanceAsOutdated(EcoNewsVO ecoNews);
}
