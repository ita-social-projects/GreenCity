package greencity.service;

import com.github.benmanes.caffeine.cache.Cache;
import greencity.cache.CachedUserRelevance;
import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.UserVO;
import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.TagsCoherenceRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class EcoNewsRelevanceServiceImpl implements EcoNewsRelevanceService {
    private final EcoNewsRelevanceRepo ecoNewsRelevanceRepo;
    private final TagsCoherenceRepo tagsCoherenceRepo;
    private final Cache<Long, CachedUserRelevance> userRelevanceNewsCache;
    private final ModelMapper modelMapper;

    @Override
    public PageableDto<EcoNewsDto> findRelevantEcoNews(UserVO user, Pageable pageable) {
        return null;
    }
}
