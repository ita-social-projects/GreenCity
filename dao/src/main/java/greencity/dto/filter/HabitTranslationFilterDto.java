package greencity.dto.filter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HabitTranslationFilterDto {
    @NotEmpty
    private String languageCode;
    private List<String> tags;
    private List<Integer> complexities;
    private boolean isCustom;
    @NotNull
    private Long userId;
    private List<Long> requestedIds;
}
