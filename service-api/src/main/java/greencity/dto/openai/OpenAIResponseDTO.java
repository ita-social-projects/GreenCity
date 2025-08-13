package greencity.dto.openai;

import greencity.enums.OpenAIResponseFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenAIResponseDTO {
    private String id;
    private String content;
    private Integer usedInputTokens;
    private Integer usedOutputTokens;
    private OpenAIResponseFormat responseFormat;
    private LocalDateTime responseDateTime;
}
