package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.logs.filter.ByteSizeRange;
import greencity.dto.logs.filter.DateRange;
import greencity.dto.logs.filter.LogFileFilterDto;
import greencity.dto.logs.LogFileMetadataDto;
import greencity.exception.exceptions.NotFoundException;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class LogFileServiceImpl implements LogFileService {

    private static final String LOGS_DIRECTORY = System.getProperty("user.dir") + File.separator + "logs" + File.separator;

    //TODO: dotenv dependency for .env file
    //Dotenv dotenv = Dotenv.load();

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<LogFileMetadataDto> getLogFilesList(Pageable pageable, LogFileFilterDto filterDto) {
        File folder = new File(LOGS_DIRECTORY);
        File[] logFiles = folder.listFiles((dir, name) -> name.endsWith(".log"));

        if (logFiles == null || logFiles.length == 0) {
            throw new NotFoundException("No files found");
        }

        List<LogFileMetadataDto> dtos = Arrays.stream(logFiles)
                .map(file -> new LogFileMetadataDto(file.getName(), file.length(), new Date(file.lastModified())))
                .filter(fileDto -> filterFileDto(fileDto, filterDto))
                .toList();

        return applyPagination(dtos, pageable);
    }

    /**
     * Applies pagination to a list of LogFileMetadataDto objects based on the given Pageable parameters.
     *
     * @param dtos     The list of dto's to paginate.
     * @param pageable The pagination details
     * @return A PageableDto containing the paginated list, total elements, current page, and total pages.
     * @author Hrenevych Ivan
     */
    private PageableDto<LogFileMetadataDto> applyPagination(List<LogFileMetadataDto> dtos, Pageable pageable) {
        long totalElements = dtos.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());

        List<LogFileMetadataDto> paginatedList = dtos.subList(start, end);

        return new PageableDto<>(paginatedList, totalElements, pageable.getPageNumber(), totalPages);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getLogFileContent(String filename) {
        File file = new File(LOGS_DIRECTORY + filename);

        if (!file.exists()) {
            throw new NotFoundException("No file found with name: " + filename);
        }

        try {
            String content = Files.readString(file.toPath());
            return content;
        } catch (IOException e) {
            throw new RuntimeException("Error reading log file: " + filename, e); //TODO: change to something else
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getDownloadLogFileUrl(String filename) {
        File file = new File(LOGS_DIRECTORY + filename);

        if (!file.exists() || !file.isFile()) {
            throw new NotFoundException("No file found with name: " + filename);
        }

        return new FileSystemResource(file);
    }

    /**
     * Filters a log file metadata object based on the given filter criteria.
     *
     * @param fileDto  The log file metadata to be filtered.
     * @param filterDto The filter criteria that will be applied.
     * @return true if the log file metadata matches all filter criteria, false otherwise.
     * @author Hrenevych Ivan
     */
    private boolean filterFileDto(LogFileMetadataDto fileDto, LogFileFilterDto filterDto) {
        return matchesName(fileDto.getFilename(), filterDto.name()) &&
                matchesByteSize(fileDto.getByteSize(), filterDto.byteSizeRange()) &&
                matchesDateRange(fileDto.getLastModified(), filterDto.dateRange()) &&
                matchesLogLevel(fileDto, filterDto.logLevel());
    }

    /**
     * Checks if the log file's filename matches the given name filter.
     *
     * @param filename    The name of the log file.
     * @param nameFilter  The filter to match against the filename. Can be null.
     * @return true if the filename matches the filter, false otherwise.
     * @author Hrenevych Ivan
     */
    private boolean matchesName(String filename, String nameFilter) {
        return nameFilter == null ||
                filename.toLowerCase().contains(nameFilter.toLowerCase());
    }

    /**
     * Checks if the log file's byte size is within the specified range.
     *
     * @param fileSize        The size of the log file in bytes.
     * @param byteSizeRange   The byte size range filter. Can be null.
     * @return true if the file size is within the range, false otherwise.
     * @author Hrenevych Ivan
     */
    private boolean matchesByteSize(long fileSize, ByteSizeRange byteSizeRange) {
        return byteSizeRange == null ||
                (fileSize >= byteSizeRange.from() && fileSize <= byteSizeRange.to());
    }

    /**
     * Checks if the log file's last modified date is within the specified date range.
     *
     * @param fileDate        The last modified date of the log file.
     * @param dateRange       The date range filter. Can be null.
     * @return true if the file's last modified date is within the range, false otherwise.
     * @author Hrenevych Ivan
     */
    private boolean matchesDateRange(Date fileDate, DateRange dateRange) {
        return dateRange == null ||
                (fileDate.compareTo(dateRange.from()) >= 0 && fileDate.compareTo(dateRange.to()) <= 0);
    }

    /**
     * Checks if the log file contains the specified log level.
     *
     * @param fileDto     The log file metadata object.
     * @param logLevel    The log level filter. Can be null.
     * @return true if the log file contains the specified log level, false otherwise.
     * @author Hrenevych Ivan
     */
    private boolean matchesLogLevel(LogFileMetadataDto fileDto, LogLevel logLevel) {
        return logLevel == null ||
                getLogFileContent(fileDto.getFilename()).contains(logLevel.toString());
    }
}
