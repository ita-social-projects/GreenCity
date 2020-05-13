package greencity.dto.search;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SearchRequestDto {
    @NotEmpty
    private String query;

    private SortingType sortingType;
}
