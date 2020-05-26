package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.tipsandtricks.AddTipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.AddTipsAndTricksDtoResponse;
import greencity.dto.tipsandtricks.TipsAndTricksDto;
import greencity.entity.TipsAndTricks;
import greencity.service.TipsAndTricksService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TipsAndTricksServiceImpl implements TipsAndTricksService {
    @Override
    public AddTipsAndTricksDtoResponse save(AddTipsAndTricksDtoRequest addTipsAndTricksDtoRequest, MultipartFile image,
                                            String email) {
        return null;
    }

    @Override
    public PageableDto<TipsAndTricksDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public PageableDto<TipsAndTricksDto> find(Pageable page, List<String> tags) {
        return null;
    }

    @Override
    public TipsAndTricks findById(Long id) {
        return null;
    }

    @Override
    public TipsAndTricksDto findDtoById(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {
    }
}
