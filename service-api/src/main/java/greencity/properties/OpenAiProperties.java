package greencity.properties;

import greencity.constant.ErrorMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiProperties {
    private final Environment environment;

    @PostConstruct
    public void validateProperties() {
        getOpenAiKey();
        getRelevance();
        log.info("All OpneAi properties validated successfully.");
    }

    public String getOpenAiKey() {
        String openAiKey = environment.getProperty("openai.api.key");
        if (!StringUtils.hasText(openAiKey)) {
            log.error(ErrorMessage.OPENAI_TOKEN_KEY_NOT_SET);
            throw new IllegalStateException(ErrorMessage.OPENAI_TOKEN_KEY_NOT_SET);
        }
        return openAiKey;
    }

    public String getRelevance() {
        String relevance = environment.getProperty("greencity.relevance.enabled");
        if (!StringUtils.hasText(relevance)) {
            log.error(ErrorMessage.RELEVANCE_NOT_SET);
            throw new IllegalStateException(ErrorMessage.RELEVANCE_NOT_SET);
        }
        return relevance;
    }
}
