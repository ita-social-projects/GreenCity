package greencity.service;

import greencity.constant.ValidationConstants;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.exception.exceptions.DuplicatedTagException;
import greencity.exception.exceptions.InvalidNumOfTagsException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TagsRepo;
import greencity.service.constant.ErrorMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagsServiceImpl implements TagsService {
    private final TagsRepo tagRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TagVO> findEcoNewsTagsByNames(List<String> ecoNewsTagNames) {
        List<String> lowerCaseTagNames = ecoNewsTagNames.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        List<Tag> tags = tagRepo.findEcoNewsTagsByNames(lowerCaseTagNames);
        if (tags.isEmpty()) {
            throw new TagNotFoundException(ErrorMessage.TAGS_NOT_FOUND);
        }
        return modelMapper.map(tagRepo.findEcoNewsTagsByNames(lowerCaseTagNames),
            new TypeToken<List<TagVO>>() {
            }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TagVO> findTipsAndTricksTagsByNames(List<String> tipsAndTricksTagNames) {
        List<String> lowerCaseTagNames = tipsAndTricksTagNames.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        List<Tag> tags = tagRepo.findTipsAndTricksTagsByNames(lowerCaseTagNames);
        if (tags.isEmpty()) {
            throw new TagNotFoundException(ErrorMessage.TAGS_NOT_FOUND);
        }
        return modelMapper.map(tags, new TypeToken<List<TagVO>>() {
        }.getType());
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
    public boolean isAllTipsAndTricksValid(List<String> tipsAndTricksTagNames) {
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
    public boolean isValidNumOfUniqueTags(List<String> tagNames) {
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
