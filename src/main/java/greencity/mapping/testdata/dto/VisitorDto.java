package greencity.mapping.testdata.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorDto {
    private Integer id;
    private String name;
    private List<Integer> placeIds = new ArrayList<>();
}
