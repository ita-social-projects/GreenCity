package greencity.dto.logs;

import greencity.dto.logs.filter.LogFileFilterDto;
import jakarta.validation.Valid;

public record LogFileRequestDto (
        String secretKey,
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
                        "from": "2025-01-01T00:00:00.000Z",
                        "to": "2025-01-01T00:00:00.000Z"
                      },
                      "logLevel": "INFO"
                    }
                }
                """;
}
