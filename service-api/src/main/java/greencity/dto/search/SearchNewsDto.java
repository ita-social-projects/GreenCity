package greencity.dto.search;

import java.util.List;
import greencity.annotations.Sortable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Sortable(fields = {"id", "title"})
@NoArgsConstructor
@AllArgsConstructor
public class SearchNewsDto {
    private Long id;
    private String title;
    private List<String> tags;
}
