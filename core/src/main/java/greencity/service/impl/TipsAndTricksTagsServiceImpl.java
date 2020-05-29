package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.TipsAndTricksTag;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TipsAndTricksTagsRepo;
import greencity.service.TipsAndTricksTagsService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TipsAndTricksTagsServiceImpl implements TipsAndTricksTagsService {
    private final TipsAndTricksTagsRepo tipsAndTricksTagsRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findAll() {
        return tipsAndTricksTagsRepo.findAll().stream()
            .filter(tag -> !tag.getTipsAndTricks().isEmpty())
            .map(TipsAndTricksTag::getName)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipsAndTricksTag findByName(String name) {
        return tipsAndTricksTagsRepo.findByName(name).orElseThrow(() ->
            new TagNotFoundException(ErrorMessage.TAG_NOT_FOUND + name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TipsAndTricksTag> findAllByNames(List<String> tipsAndTricksTagsNames) {
        List<TipsAndTricksTag> tags = tipsAndTricksTagsRepo.findAllByNames(tipsAndTricksTagsNames);
        if (tags.isEmpty()) {
            throw new TagNotFoundException(ErrorMessage.TAGS_NOT_FOUND);
        }
        return tags;
    }
}
