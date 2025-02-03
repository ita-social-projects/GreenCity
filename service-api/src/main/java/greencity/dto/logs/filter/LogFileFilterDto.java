package greencity.dto.logs.filter;

import jakarta.validation.Valid;
import org.springframework.boot.logging.LogLevel;

public record LogFileFilterDto(
        String name,
        @Valid ByteSizeRange byteSizeRange,
        @Valid DateRange dateRange,
        LogLevel logLevel
        ) {}