package greencity.dto.logs;

import greencity.dto.logs.filter.LogFileFilterDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record LogFileRequestDto(
    @NotNull String secretKey,
    @Valid LogFileFilterDto filterDto) {
    public static final String defaultJson = """
        {
            "secretKey": "string",
            "filterDto": {
              "fileNameQuery": "string",
              "fileContentQuery": "string",
              "byteSizeRange": {
                "from": 0,
                "to": 0
              },
              "dateRange": {
                "from": "2025-01-01T00:00:00",
                "to": "2025-01-01T00:00:00"
              },
              "logLevel": "INFO"
            }
        }
        """;
}
