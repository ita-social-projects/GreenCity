package greencity.dto.exportsettings;

import java.util.List;
import java.util.Map;

public record TablesMetadataDto(Map<String, List<String>> tables) {
}
