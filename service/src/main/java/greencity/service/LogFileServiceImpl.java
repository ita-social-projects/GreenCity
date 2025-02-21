package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.logs.filter.ByteSizeRange;
import greencity.dto.logs.filter.DateRange;
import greencity.dto.logs.filter.LogFileFilterDto;
import greencity.dto.logs.LogFileMetadataDto;
import greencity.exception.exceptions.FileReadException;
import greencity.exception.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class LogFileServiceImpl implements LogFileService {
    private static final String LOGS_DIRECTORY =
        System.getProperty("user.dir") + File.separator + "logs" + File.separator;

    private final DotenvService dotEnvService;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<LogFileMetadataDto> listLogFiles(Pageable pageable, LogFileFilterDto filterDto,
        String secretKey) {
        dotEnvService.validateSecretKey(secretKey);
        File[] logFiles = listLogFilesFromFolder();

        if (logFiles == null || logFiles.length == 0) {
            throw new NotFoundException(ErrorMessage.LOG_FILES_NOT_FOUND);
        }

        List<LogFileMetadataDto> dtos = Arrays.stream(logFiles)
            .map(file -> new LogFileMetadataDto(
                file.getName(),
                file.length(),
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(file.lastModified()),
                    ZoneId.systemDefault())))
            .filter(fileDto -> filterFileDto(fileDto, filterDto))
            .toList();

        return applyPagination(dtos, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String viewLogFileContent(String filename, String secretKey) {
        dotEnvService.validateSecretKey(secretKey);
        File file = getLogFile(filename);

        if (!file.exists()) {
            throw new NotFoundException(String.format(ErrorMessage.LOG_FILE_NOT_FOUND, filename));
        }

        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new FileReadException(String.format(ErrorMessage.CANNOT_READ_LOG_FILE, filename), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource generateDownloadLogFileUrl(String filename, String secretKey) {
        dotEnvService.validateSecretKey(secretKey);
        File file = getLogFile(filename);

        if (!file.exists() || !file.isFile()) {
            throw new NotFoundException(String.format(ErrorMessage.LOG_FILE_NOT_FOUND, filename));
        }

        return new FileSystemResource(file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    File[] listLogFilesFromFolder() {
        File folder = getLogFile();
        return folder.listFiles((dir, name) -> name.endsWith(".log"));
    }

    File getLogFile() {
        return new File(LOGS_DIRECTORY);
    }

    File getLogFile(String filename) {
        return new File(LOGS_DIRECTORY + filename);
    }

    /**
     * Applies pagination to a list of LogFileMetadataDto objects based on the given
     * Pageable parameters.
     *
     * @param dtos     The list of dto's to paginate.
     * @param pageable The pagination details
     * @return A PageableDto containing the paginated list, total elements, current
     *         page, and total pages.
     * @author Hrenevych Ivan
     */
    private PageableDto<LogFileMetadataDto> applyPagination(List<LogFileMetadataDto> dtos, Pageable pageable) {
        long totalElements = dtos.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());

        List<LogFileMetadataDto> paginatedList = dtos.subList(start, end);

        return new PageableDto<>(paginatedList, totalElements, pageable.getPageNumber(), totalPages);
    }

    /**
     * Filters a log file metadata object based on the given filter criteria.
     *
     * @param fileDto   The log file metadata to be filtered.
     * @param filterDto The filter criteria that will be applied.
     * @return true if the log file metadata matches all filter criteria, false
     *         otherwise.
     * @author Hrenevych Ivan
     */
    private boolean filterFileDto(LogFileMetadataDto fileDto, LogFileFilterDto filterDto) {
        if (filterDto == null) {
            return true;
        }

        try {
            Supplier<Stream<String>> linesStreamSupplier = () -> {
                try {
                    return Files.lines(getLogFile(fileDto.filename()).toPath());
                } catch (IOException e) {
                    throw new FileReadException("Error reading file: " + fileDto.filename(), e);
                }
            };

            boolean matchesFileContentQuery;
            try (Stream<String> linesStream = linesStreamSupplier.get()) {
                matchesFileContentQuery = matchesFileContentQuery(linesStream, filterDto.fileContentQuery());
            }

            boolean matchesLogLevel;
            try (Stream<String> linesStream = linesStreamSupplier.get()) {
                matchesLogLevel = matchesLogLevel(linesStream, filterDto.logLevel());
            }

            return matchesFileNameQuery(fileDto.filename(), filterDto.fileNameQuery())
                && matchesFileContentQuery
                && matchesByteSize(fileDto.byteSize(), filterDto.byteSizeRange())
                && matchesDateRange(fileDto.lastModified(), filterDto.dateRange())
                && matchesLogLevel;
        } catch (FileReadException e) {
            return false;
        }
    }

    /**
     * Checks if the log file's filename matches the given name filter.
     *
     * @param filename       The name of the log file.
     * @param fileNameFilter The filter to match against the filename. Can be null.
     * @return true if the filename matches the filter, false otherwise.
     * @author Hrenevych Ivan
     */
    private boolean matchesFileNameQuery(String filename, String fileNameFilter) {
        return fileNameFilter == null
            || filename.toLowerCase().contains(fileNameFilter.toLowerCase());
    }

    /**
     * Checks if the log file's content contains the given query filter.
     *
     * @param fileContentStream The name of the log file.
     * @param nameFilter        The filter to match against the file content. Can be
     *                          null.
     * @return true if the file content contains the given text, false otherwise.
     * @author Hrenevych Ivan
     */
    private boolean matchesFileContentQuery(Stream<String> fileContentStream, String fileContentFilter) {
        return fileContentFilter == null
            || fileContentStream.map(String::toLowerCase)
                .anyMatch(line -> line.contains(fileContentFilter.toLowerCase()));
    }

    /**
     * Checks if the log file's byte size is within the specified range.
     *
     * @param fileSize      The size of the log file in bytes.
     * @param byteSizeRange The byte size range filter. Can be null.
     * @return true if the file size is within the range, false otherwise.
     * @author Hrenevych Ivan
     */
    private boolean matchesByteSize(long fileSize, ByteSizeRange byteSizeRange) {
        return byteSizeRange == null
            || (fileSize >= byteSizeRange.from() && fileSize <= byteSizeRange.to());
    }

    /**
     * Checks if the log file's last modified date is within the specified date
     * range.
     *
     * @param fileDate  The last modified date of the log file.
     * @param dateRange The date range filter. Can be null.
     * @return true if the file's last modified date is within the range, false
     *         otherwise.
     * @author Hrenevych Ivan
     */
    private boolean matchesDateRange(LocalDateTime fileDate, DateRange dateRange) {
        return dateRange == null
            || (!fileDate.isBefore(dateRange.from()) && !fileDate.isAfter(dateRange.to()));
    }

    /**
     * Checks if the log file contains the specified log level.
     *
     * @param fileDto  The log file metadata object.
     * @param logLevel The log level filter. Can be null.
     * @return true if the log file contains the specified log level, false
     *         otherwise.
     * @author Hrenevych Ivan
     */
    private boolean matchesLogLevel(Stream<String> fileContentStream, LogLevel logLevel) {
        return logLevel == null
            || fileContentStream.anyMatch(line -> line.contains(logLevel.toString()));
    }
}
