package greencity.mapping.testdata.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto {
    private Integer id;
    private String title;
    private List<Integer> visitorIds = new ArrayList<>();
}
