package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.exception.exceptions.BadIdException;
import greencity.repository.UserRepo;
import greencity.service.LanguageService;
import greencity.service.TagService;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link AddEcoNewsDtoRequest} into
 * {@link EcoNews}.
 */
@Component
public class AddEcoNewsDtoRequestMapper extends AbstractConverter<AddEcoNewsDtoRequest, EcoNews> {
    private final LanguageService languageService;
    private final TagService tagService;
    private UserRepo userRepo;

    /**
     * All args constructor.
     *
     * @param languageService service for getting language.
     * @param tagService      service for getting tags.
     * @param userRepo        repository for getting author.
     */
    @Autowired
    public AddEcoNewsDtoRequestMapper(LanguageService languageService,
                                      TagService tagService, UserRepo userRepo) {
        this.languageService = languageService;
        this.tagService = tagService;
        this.userRepo = userRepo;
    }

    /**
     * Method for converting {@link AddEcoNewsDtoRequest} into {@link EcoNews}.
     *
     * @param addEcoNewsDtoRequest object to convert.
     * @return converted object.
     */
    @Override
    protected EcoNews convert(AddEcoNewsDtoRequest addEcoNewsDtoRequest) {
        EcoNews ecoNews = EcoNews.builder()
            .creationDate(ZonedDateTime.now())
            .author(userRepo.findById(addEcoNewsDtoRequest.getAuthor().getId()).orElseThrow(
                () -> new BadIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + addEcoNewsDtoRequest.getAuthor().getId())
            ))
            .imagePath(addEcoNewsDtoRequest.getImagePath())
            .build();

        ecoNews.setTags(addEcoNewsDtoRequest.getTags()
            .stream()
            .map(tag -> tagService.findByName(tag))
            .collect(Collectors.toList())
        );

        ecoNews.setTranslations(addEcoNewsDtoRequest.getTranslations()
            .stream()
            .map(translation ->
                new EcoNewsTranslation(null,
                    languageService.findByCode(translation.getLanguage().getCode()),
                    translation.getTitle(), translation.getText(), ecoNews))
            .collect(Collectors.toList()));

        return ecoNews;
    }
}
