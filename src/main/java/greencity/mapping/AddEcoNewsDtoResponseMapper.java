package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.LanguageService;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddEcoNewsDtoResponseMapper extends AbstractConverter<EcoNews, AddEcoNewsDtoResponse> {
    private LanguageService languageService;
    private EcoNewsTranslationRepo ecoNewsTranslationRepo;

    /**
     * Constructor.
     * @param languageService lol
     * @param ecoNewsTranslationRepo kek
     */
    @Autowired
    public AddEcoNewsDtoResponseMapper(LanguageService languageService, EcoNewsTranslationRepo ecoNewsTranslationRepo) {
        this.languageService = languageService;
        this.ecoNewsTranslationRepo = ecoNewsTranslationRepo;
    }

    /**
     * Method.
     * @param ecoNews aga
     * @return da
     */
    @Override
    protected AddEcoNewsDtoResponse convert(EcoNews ecoNews) {
        EcoNewsTranslation translation = ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(ecoNews,
            languageService.extractLanguageCodeFromRequest());

        return new AddEcoNewsDtoResponse(translation.getTitle(), ecoNews.getText(), ecoNews.getCreationDate(),
            ecoNews.getImagePath());
    }
}
