package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.Tag;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TagRepo;
import greencity.service.TagService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepo tagRepo;

    /**
     * All args constructor.
     *
     * @param tagRepo Repo to get {@link Tag}.
     */
    @Autowired
    public TagServiceImpl(TagRepo tagRepo) {
        this.tagRepo = tagRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tag findByName(String name) {
        return tagRepo.findByName(name).orElseThrow(() ->
            new TagNotFoundException(ErrorMessage.TAG_NOT_FOUND + name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isAllValid(List<String> tags) {
        for (String tag : tags) {
            try {
                findByName(tag);
            } catch (TagNotFoundException e) {
                return false;
            }
        }
        return true;
    }
}
