package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.ValidationConstants;
import greencity.entity.Tag;
import greencity.exception.exceptions.DuplicatedTagException;
import greencity.exception.exceptions.InvalidNumOfTagsException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TagsRepo;
import greencity.service.TagsService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagsServiceImpl implements TagsService {
    private final TagsRepo tagRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> findEcoNewsTagsByNames(List<String> ecoNewsTagNames) {
        ecoNewsTagNames.replaceAll(String::toLowerCase);
        List<Tag> tags = tagRepo.findEcoNewsTagsByNames(ecoNewsTagNames);
        if (tags.isEmpty()) {
            throw new TagNotFoundException(ErrorMessage.TAGS_NOT_FOUND);
        }
        return tags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> findTipsAndTricksTagsByNames(List<String> tipsAndTricksTagNames) {
        tipsAndTricksTagNames.replaceAll(String::toLowerCase);
        List<Tag> tags = tagRepo.findTipsAndTricksTagsByNames(tipsAndTricksTagNames);
        if (tags.isEmpty()) {
            throw new TagNotFoundException(ErrorMessage.TAGS_NOT_FOUND);
        }
        return tags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findAllEcoNewsTags() {
        return tagRepo.findAllEcoNewsTags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findAllTipsAndTricksTags() {
        return tagRepo.findAllTipsAndTricksTags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isAllTipsAndTricksValid(List<String> tipsAndTricksTagNames) {
        try {
            findTipsAndTricksTagsByNames(tipsAndTricksTagNames);
        } catch (TagNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isValidNumOfUniqueTags(List<String> tagNames) {
        Set<String> tagsSet = new HashSet<>(tagNames);
        if (tagsSet.size() < tagNames.size()) {
            throw new DuplicatedTagException(ErrorMessage.DUPLICATED_TAG);
        }
        if (tagsSet.size() > ValidationConstants.MAX_AMOUNT_OF_TAGS) {
            throw new InvalidNumOfTagsException(ErrorMessage.INVALID_NUM_OF_TAGS);
        }
        return true;
    }
}