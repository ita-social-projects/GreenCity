package greencity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SliceDto<T> {
    private List<T> page;

    private boolean last;

    private int currentPage;
}
