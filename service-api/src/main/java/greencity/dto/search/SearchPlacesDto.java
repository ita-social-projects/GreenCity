package greencity.dto.search;

import greencity.annotations.Sortable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Sortable
@NoArgsConstructor
@AllArgsConstructor
public class SearchPlacesDto {
    private Long id;
    private String name;
    private String category;
}
