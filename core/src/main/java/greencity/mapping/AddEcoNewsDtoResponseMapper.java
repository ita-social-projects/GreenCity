package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.tag.TagDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.LanguageService;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link EcoNews} into
 * {@link AddEcoNewsDtoResponse}.
 */
@Component
public class AddEcoNewsDtoResponseMapper extends AbstractConverter<EcoNews, AddEcoNewsDtoResponse> {
    private LanguageService languageService;
    private EcoNewsTranslationRepo ecoNewsTranslationRepo;
    private EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper;
    private TagDtoMapper tagDtoMapper;

    /**
     * All args constructor.
     *
     * @param languageService        service to extract language from request.
     * @param ecoNewsTranslationRepo repository for getting {@link EcoNewsTranslation}.
     * @param tagDtoMapper           mapper to map Tag to TagDto
     */
    @Autowired
    public AddEcoNewsDtoResponseMapper(LanguageService languageService, EcoNewsTranslationRepo ecoNewsTranslationRepo,
                                       EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper, TagDtoMapper tagDtoMapper) {
        this.languageService = languageService;
        this.ecoNewsTranslationRepo = ecoNewsTranslationRepo;
        this.ecoNewsAuthorDtoMapper = ecoNewsAuthorDtoMapper;
        this.tagDtoMapper = tagDtoMapper;
    }

    /**
     * Method for converting {@link EcoNews} into {@link AddEcoNewsDtoResponse}.
     *
     * @param ecoNews object to convert.
     * @return converted object.
     */
    @Override
    protected AddEcoNewsDtoResponse convert(EcoNews ecoNews) {
        EcoNewsTranslation translation = ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(ecoNews,
            languageService.extractLanguageCodeFromRequest());
        EcoNewsAuthorDto ecoNewsAuthorDto = ecoNewsAuthorDtoMapper.convert(ecoNews.getAuthor());
        List<TagDto> tags = ecoNews.getTags()
            .stream()
            .map(tag -> tagDtoMapper.convert(tag))
            .collect(Collectors.toList());

        return new AddEcoNewsDtoResponse(ecoNews.getId(), translation.getTitle(), translation.getText(),
            ecoNewsAuthorDto, ecoNews.getCreationDate(), ecoNews.getImagePath(), tags);
    }
}
