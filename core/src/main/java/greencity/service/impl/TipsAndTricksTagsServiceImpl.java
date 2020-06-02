package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.ValidationConstants;
import greencity.entity.TipsAndTricksTag;
import greencity.exception.exceptions.DuplicatedTagException;
import greencity.exception.exceptions.InvalidNumOfTagsException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TipsAndTricksTagsRepo;
import greencity.service.TipsAndTricksTagsService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isAllValid(List<String> tags) {
        try {
            findAllByNames(tags);
        } catch (TagNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isValidNumOfUniqueTags(List<String> tipsAndTricksTagsNames) {
        Set<String> tagsSet = new HashSet<>(tipsAndTricksTagsNames);
        if (tagsSet.size() < tipsAndTricksTagsNames.size()) {
            throw new DuplicatedTagException(ErrorMessage.DUPLICATED_TAG);
        }
        if (tagsSet.size() > ValidationConstants.MAX_AMOUNT_OF_TAGS) {
            throw new InvalidNumOfTagsException(ErrorMessage.INVALID_NUM_OF_TAGS);
        }
        return true;
    }
}