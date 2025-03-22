package greencity.config;

import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractor;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.tika.language.detect.LanguageDetector;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.Ukrainian;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LanguageToolConfig {
    @Bean
    public JLanguageTool englishTool() {
        return new JLanguageTool(new AmericanEnglish());
    }

    @Bean
    public JLanguageTool ukrainianTool() {
        return new JLanguageTool(new Ukrainian());
    }

    @Bean
    public LanguageDetector languageDetector() throws IOException {
        List<String> languages = Arrays.asList("en", "uk");
        List<LanguageProfile> profiles = new LanguageProfileReader().read(languages);
        return (LanguageDetector) LanguageDetectorBuilder.create((NgramExtractor) profiles).build();
    }
}
