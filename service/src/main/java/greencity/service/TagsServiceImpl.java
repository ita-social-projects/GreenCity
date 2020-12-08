package greencity.service;

import greencity.constant.ValidationConstants;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.enums.TagType;
import greencity.exception.exceptions.DuplicatedTagException;
import greencity.exception.exceptions.InvalidNumOfTagsException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TagsRepo;
import greencity.constant.ErrorMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagsServiceImpl implements TagsService {
    private final TagsRepo tagRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     * */
    @Override
    public PageableAdvancedDto<TagVO> findAll(Pageable pageable) {
        Page<Tag> tags = tagRepo.findAll(pageable);

        return buildPageableAdvanceDtoFromPage(tags);
    }

    private PageableAdvancedDto<TagVO> buildPageableAdvanceDtoFromPage(Page<Tag> pageTags) {
        List<TagVO> tagVOs = pageTags.getContent().stream()
            .map(t -> modelMapper.map(t, TagVO.class))
            .collect(Collectors.toList());

        return new PageableAdvancedDto<>(
            tagVOs,
            pageTags.getTotalElements(), pageTags.getPageable().getPageNumber(),
            pageTags.getTotalPages(), pageTags.getNumber(),
            pageTags.hasPrevious(), pageTags.hasNext(),
            pageTags.isFirst(), pageTags.isLast());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TagVO> findTagsByNamesAndType(List<String> tagNames, TagType tagType) {
        List<String> lowerCaseTagNames = tagNames.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        List<Tag> tags = tagRepo.findTagsByNamesAndType(lowerCaseTagNames, tagType);
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
    public List<String> findAllEcoNewsTags(String languageCode) {
        return tagRepo.findAllEcoNewsTags(languageCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findAllTipsAndTricksTags(String languageCode) {
        return tagRepo.findAllTipsAndTricksTags(languageCode);
    }

    @Override
    public List<String> findAllHabitsTags(String languageCode) {
        return tagRepo.findAllHabitsTags(languageCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllTipsAndTricksValid(List<String> tipsAndTricksTagNames, TagType type) {
        try {
            findTagsByNamesAndType(tipsAndTricksTagNames, type);
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
