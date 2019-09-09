package greencity.dto.place;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlacePageableDto {
    private List<AdminPlaceDto> page;

    @NotNull
    private long totalElements;

    @NotNull
    private int currentPage;
}
