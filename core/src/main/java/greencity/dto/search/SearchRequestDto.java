package greencity.dto.search;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchRequestDto {
    @NotEmpty
    private String query;

    private SortingType sortingType;
}
