package greencity.dto.exportsettings;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class TableRowsDto {
    List<Map<String, String>> tableData;
}
