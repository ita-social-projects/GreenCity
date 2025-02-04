package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.logs.filter.LogFileFilterDto;
import greencity.dto.logs.LogFileMetadataDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;

public interface LogFileService {
    /**
     * Retrieves a list of available log files with metadata such as filename, size,
     * and last modified date.
     *
     * @param page {@link Pageable}
     * @return a list of maps where each map contains details about a log file.
     * @author Hrenevych Ivan
     */
    PageableDto<LogFileMetadataDto> getLogFilesList(Pageable page, LogFileFilterDto filterDto, String secretKey);

    /**
     * Reads and returns the content of a log file as a string for viewing in the
     * browser.
     *
     * @param filename the name of the log file to be retrieved.
     * @return the content of the log file as a string.
     * @author Hrenevych Ivan
     */
    String getLogFileContent(String filename, String secretKey);

    /**
     * Provides a downloadable resource for a given log file.
     *
     * @param filename the name of the log file to be downloaded.
     * @return a {@link Resource} representing the log file.
     * @author Hrenevych Ivan
     */
    Resource getDownloadLogFileUrl(String filename, String secretKey);
}