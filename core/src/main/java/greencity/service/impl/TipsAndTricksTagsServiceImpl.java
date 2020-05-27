package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.TipsAndTricksTag;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TipsAndTricksTagsRepo;
import greencity.service.TipsAndTricksTagsService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipsAndTricksTagsServiceImpl implements TipsAndTricksTagsService {
    private final TipsAndTricksTagsRepo tipsAndTricksTagsRepo;

    /**
     * All args constructor.
     *
     * @param tipsAndTricksTagsRepo Repo to get {@link TipsAndTricksTag}.
     */
    @Autowired
    public TipsAndTricksTagsServiceImpl(TipsAndTricksTagsRepo tipsAndTricksTagsRepo) {
        this.tipsAndTricksTagsRepo = tipsAndTricksTagsRepo;
    }

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
}
