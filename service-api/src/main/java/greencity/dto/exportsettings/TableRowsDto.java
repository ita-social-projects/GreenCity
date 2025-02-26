package greencity.dto.exportsettings;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class TableRowsDto {
    private List<Map<String, String>> tableData;
}
