package greencity.dto.grammar;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GrammarCheckResult {
    private List<GrammarError> errors;
    private Long originalTextLength;
    private String correctedText;
}
