package greencity.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageableDto<T> {

    private List<T> page;

    private int pageSize;
}
