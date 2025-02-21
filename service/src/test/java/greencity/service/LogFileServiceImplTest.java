package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.logs.LogFileMetadataDto;
import greencity.dto.logs.filter.LogFileFilterDto;
import greencity.exception.exceptions.FileReadException;
import greencity.exception.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogFileServiceImplTest {

    public static final PageRequest PAGEABLE = PageRequest.of(0, 10);
    @InjectMocks
    private LogFileServiceImpl logFileService;

    @Mock
    private DotenvService dotEnvService;

    @BeforeEach
    void ignoreSecretKeyValidation() {
        doNothing().when(dotEnvService).validateSecretKey(anyString());
    }

    @Test
    void listLogFilesListShouldReturnLogFilesWhenTheyExistTest() {
        String secretKey = "secret";
        File logFile1 = new File("test1.log");
        File logFile2 = new File("test2.log");
        File[] mockFiles = {logFile1, logFile2};

        LogFileServiceImpl spyService = spy(logFileService);
        doReturn(mockFiles).when(spyService).listLogFilesFromFolder();
        PageableDto<LogFileMetadataDto> result = spyService.listLogFiles(PAGEABLE, null, secretKey);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("test1.log", result.getPage().get(0).filename());
        assertEquals("test2.log", result.getPage().get(1).filename());
    }

    @Test
    void listLogFilesListShouldReturnFilteredLogFilesTest() {
        String secretKey = "secret";
        LogFileFilterDto filterDto = ModelUtils.getLogFileFilterDto();
        File logFile1 = new File("test1.log");
        File logFile2 = new File("test2.log");
        File logFile3 = new File("smth.log");
        File[] mockFiles = {logFile1, logFile2, logFile3};

        LogFileServiceImpl spyService = spy(logFileService);
        doReturn(mockFiles).when(spyService).listLogFilesFromFolder();
        doReturn("someFileContent").when(spyService).viewLogFileContent(any(), any());
        PageableDto<LogFileMetadataDto> result = spyService.listLogFiles(PAGEABLE, filterDto, secretKey);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("test1.log", result.getPage().get(0).filename());
        assertEquals("test2.log", result.getPage().get(1).filename());
    }

    @Test
    void listLogFilesListShouldThrowNotFoundExceptionWhenNoLogFilesExistTest() {
        String secretKey = "secret";
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> logFileService.listLogFiles(PAGEABLE, null, secretKey));

        assertEquals(ErrorMessage.LOG_FILES_NOT_FOUND, exception.getMessage());
    }

    @Test
    void viewLogFileContentShouldReturnFileContentWhenFileExistsTest() {
        String filename = "test.log";
        String secretKey = "secret";
        String expectedContent = "testContent";

        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.toPath()).thenReturn(new File(filename).toPath());

        LogFileServiceImpl spyService = spy(logFileService);
        doReturn(mockFile).when(spyService).getLogFile(filename);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readString(any())).thenReturn(expectedContent);

            String result = spyService.viewLogFileContent(filename, secretKey);

            assertEquals(expectedContent, result);
        }
    }

    @Test
    void viewLogFileContentShouldThrowNotFoundExceptionWhenFileDoesNotExistTest() {
        String filename = "nonexistent.log";
        String secretKey = "secret";

        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(false);

        LogFileServiceImpl spyService = spy(logFileService);
        doReturn(mockFile).when(spyService).getLogFile(filename);

        assertThrows(NotFoundException.class, () -> spyService.viewLogFileContent(filename, secretKey));
    }

    @Test
    void viewLogFileContentShouldThrowFileReadExceptionWhenIOExceptionOccursTest() {
        String filename = "test.log";
        String secretKey = "secret";

        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.toPath()).thenReturn(new File(filename).toPath());

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readString(any())).thenThrow(new IOException("Read error"));

            LogFileServiceImpl spyService = spy(logFileService);
            doReturn(mockFile).when(spyService).getLogFile(filename);

            assertThrows(FileReadException.class, () -> spyService.viewLogFileContent(filename, secretKey));
        }
    }

    @Test
    void generateDownloadLogFileUrlShouldReturnLogFileUrlWhenFileExistsTest() {
        String filename = "testFile.log";
        String secretKey = "secret";
        File mockFile = mock(File.class);
        FileSystemResource expectedResource = new FileSystemResource(mockFile);

        LogFileServiceImpl spyService = spy(logFileService);
        doReturn(mockFile).when(spyService).getLogFile(filename);

        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isFile()).thenReturn(true);

        Resource result = spyService.generateDownloadLogFileUrl(filename, secretKey);

        assertNotNull(result);
        assertEquals(expectedResource.getFilename(), result.getFilename());
    }
}