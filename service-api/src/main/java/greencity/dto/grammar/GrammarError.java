package greencity.dto.grammar;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GrammarError {
    private String original;
    private String correction;
    private String description;
    private int position;
}
