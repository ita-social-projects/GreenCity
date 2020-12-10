package greencity.service;

import greencity.constant.ValidationConstants;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.tag.TagPostDto;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.enums.TagType;
import greencity.exception.exceptions.*;
import greencity.repository.TagTranslationRepo;
import greencity.repository.TagsRepo;
import greencity.constant.ErrorMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagsServiceImpl implements TagsService {
    private final TagsRepo tagRepo;
    private final TagTranslationRepo tagTranslationRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     * */
    @Override
    public PageableAdvancedDto<TagVO> findAll(Pageable pageable) {
        Page<Tag> tags = tagRepo.findAll(pageable);

        return buildPageableAdvanceDtoFromPage(tags);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public TagVO save(TagPostDto tag) {
        Tag toSave = modelMapper.map(tag, Tag.class);
        toSave.getTagTranslations().forEach(t -> t.setTag(toSave));
        Tag saved = tagRepo.save(toSave);

        return modelMapper.map(saved, TagVO.class);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public TagVO findById(Long id) {
        Tag foundTag = tagRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TAG_NOT_FOUND + id));

        return modelMapper.map(foundTag, TagVO.class);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public Long deleteById(Long id) {
        try {
            tagRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotDeletedException(ErrorMessage.TAG_NOT_DELETED + id);
        }

        return id;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    @Transactional
    public List<Long> bulkDelete(List<Long> ids) {
        tagTranslationRepo.bulkDeleteByTagId(ids);
        tagRepo.bulkDelete(ids);
        return ids;
    }

    @Override
    @Transactional
    public TagVO update(TagPostDto tagPostDto, Long id) {
        Tag toUpdate = tagRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TAG_NOT_FOUND + id));
        enhanceTagWithNewData(toUpdate, tagPostDto);

        return modelMapper.map(toUpdate, TagVO.class);
    }

    private void enhanceTagWithNewData(Tag toUpdate, TagPostDto tagPostDto) {
        toUpdate.setType(tagPostDto.getType());
        toUpdate.getTagTranslations()
            .forEach(tagTranslation -> tagTranslation.setName(
                tagPostDto.getTagTranslations().stream().filter(newTranslation ->
                    newTranslation.getLanguage().getId()
                        .equals(tagTranslation.getLanguage().getId()))
                    .findFirst()
                    .get().getName()
            ));
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
