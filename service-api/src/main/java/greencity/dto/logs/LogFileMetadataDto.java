package greencity.dto.logs;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class LogFileMetadataDto {
    private String filename;
    private long byteSize;
    private Date lastModified;

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
