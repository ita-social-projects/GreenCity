package greencity.service;

import com.deepl.api.DeepLClient;
import com.deepl.api.Formality;
import com.deepl.api.TextResult;
import com.deepl.api.DeepLException;
import com.deepl.api.SentenceSplittingMode;
import com.deepl.api.TextTranslationOptions;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.TranslationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepLTranslationService implements TranslationService {
    private final DeepLClient deeplClient;

    @Override
    public String translateText(String text, String sourceLanguage, String targetLanguage) {
        try {
            TextTranslationOptions textTranslationOptions = new TextTranslationOptions();
            textTranslationOptions.setFormality(Formality.PreferMore);
            textTranslationOptions.setSentenceSplittingMode(SentenceSplittingMode.All);
            TextResult textResult =
                deeplClient.translateText(text, sourceLanguage, targetLanguage, textTranslationOptions);
            return textResult.getText();
        } catch (DeepLException | InterruptedException exception) {
            log.error("An error occurred during translation, reason: {}", exception.getMessage());
            throw new TranslationException(
                String.format(ErrorMessage.TRANSLATION_PROCESSING_ERROR, sourceLanguage, targetLanguage));
        }
    }
}
