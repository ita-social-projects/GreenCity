package greencity.service;

import java.io.IOException;

public interface GrammarCheckerService {
    String checkGrammar(String text) throws IOException;
}
