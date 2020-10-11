package greencity.service;

import greencity.entity.TextTranslation;
import greencity.entity.TitleTranslation;

import java.util.List;

public interface TipsAndTricksTranslationService {
    /**
     * Method saves all new {@link TitleTranslation}.
     *
     * @param  titleTranslations {@link TitleTranslation}
     * @return list of {@link TitleTranslation}
     */
    List<TitleTranslation> saveTitleTranslations(List<TitleTranslation> titleTranslations);

    /**
     * Method saves all new {@link TextTranslation}.
     *
     * @param textTranslations {@link TextTranslation}
     * @return list of {@link TextTranslation}
     */
    List<TextTranslation> saveTextTranslations(List<TextTranslation> textTranslations);
}
