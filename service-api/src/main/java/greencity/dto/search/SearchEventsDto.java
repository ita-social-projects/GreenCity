package greencity.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SearchEventsDto {
    @Min(0)
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private LocalDate creationDate;
    @NotEmpty
    private List<String> tags;
}
