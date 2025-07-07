package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

public interface EcoNewsRelevanceService {
    /**
     * Finds relevant eco news by user.
     *
     * @param user     user for which we search relevant eco news
     * @param pageable page settings
     * @return list of relevant eco news
     */
    PageableDto<EcoNewsDto> findRelevantEcoNews(UserVO user, Pageable pageable);
}
