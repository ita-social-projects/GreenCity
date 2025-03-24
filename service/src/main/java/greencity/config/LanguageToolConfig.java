package greencity.config;

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
}