package greencity.service;

import greencity.dto.grammar.GrammarCheckResult;
import java.io.IOException;

public interface GrammarCheckerService {
    GrammarCheckResult checkGrammar(String text) throws IOException;

    void clearCache(String text);
}
