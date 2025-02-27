package greencity.dto.logs;

import java.time.LocalDateTime;

public record LogFileMetadataDto(
    String filename,
    long byteSize,
    LocalDateTime lastModified) {
    public static final String defaultJson =
        """
                {
                    "page": [
                        {
                          "filename": "string",
                          "size": 0,
                          "lastModified": "2025-01-01T00:00:00.000+00:00"
                        }
                    ],
                    "totalElements": 0,
                    "currentPage": 0,
                    "totalPages": 0
                }
            """;
}
