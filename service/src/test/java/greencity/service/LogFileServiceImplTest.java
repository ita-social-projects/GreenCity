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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

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
    void getLogFilesListShouldReturnLogFilesWhenTheyExistTest() {
        String secretKey = "secret";
        File logFile1 = new File("test1.log");
        File logFile2 = new File("test2.log");
        File[] mockFiles = {logFile1, logFile2};

        LogFileServiceImpl spyService = spy(logFileService);
        doReturn(mockFiles).when(spyService).listLogFilesFromFolder();
        PageableDto<LogFileMetadataDto> result = spyService.getLogFilesList(PAGEABLE, null, secretKey);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("test1.log", result.getPage().get(0).filename());
        assertEquals("test2.log", result.getPage().get(1).filename());
    }

    @Test
    void getLogFilesListShouldReturnFilteredLogFilesTest() {
        String secretKey = "secret";
        LogFileFilterDto filterDto = ModelUtils.getLogFileFilterDto();
        File logFile1 = new File("test1.log");
        File logFile2 = new File("test2.log");
        File logFile3 = new File("smth.log");
        File[] mockFiles = {logFile1, logFile2, logFile3};

        LogFileServiceImpl spyService = spy(logFileService);
        doReturn(mockFiles).when(spyService).listLogFilesFromFolder();
        doReturn("someFileContent").when(spyService).getLogFileContent(any(), any());
        PageableDto<LogFileMetadataDto> result = spyService.getLogFilesList(PAGEABLE, filterDto, secretKey);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("test1.log", result.getPage().get(0).filename());
        assertEquals("test2.log", result.getPage().get(1).filename());
    }

    @Test
    void getLogFilesListShouldThrowNotFoundExceptionWhenNoLogFilesExistTest() {
        String secretKey = "secret";
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> logFileService.getLogFilesList(PAGEABLE, null, secretKey));

        assertEquals(ErrorMessage.LOG_FILES_NOT_FOUND, exception.getMessage());
    }

    @Test
    void getLogFileContentShouldReturnFileContentWhenFileExistsTest() {
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

            String result = spyService.getLogFileContent(filename, secretKey);

            assertEquals(expectedContent, result);
        }
    }

    @Test
    void getLogFileContentShouldThrowNotFoundExceptionWhenFileDoesNotExistTest() {
        String filename = "nonexistent.log";
        String secretKey = "secret";

        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(false);

        LogFileServiceImpl spyService = spy(logFileService);
        doReturn(mockFile).when(spyService).getLogFile(filename);

        assertThrows(NotFoundException.class, () -> spyService.getLogFileContent(filename, secretKey));
    }

    @Test
    void getLogFileContentShouldThrowFileReadExceptionWhenIOExceptionOccursTest() {
        String filename = "test.log";
        String secretKey = "secret";

        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.toPath()).thenReturn(new File(filename).toPath());

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readString(any())).thenThrow(new IOException("Read error"));

            LogFileServiceImpl spyService = spy(logFileService);
            doReturn(mockFile).when(spyService).getLogFile(filename);

            assertThrows(FileReadException.class, () -> spyService.getLogFileContent(filename, secretKey));
        }
    }

    @Test
    void getDownloadLogFileUrlShouldReturnLogFileUrlWhenFileExistsTest() {
        String filename = "testFile.log";
        String secretKey = "secret";
        File mockFile = mock(File.class);
        FileSystemResource expectedResource = new FileSystemResource(mockFile);

        LogFileServiceImpl spyService = spy(logFileService);
        doReturn(mockFile).when(spyService).getLogFile(filename);

        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isFile()).thenReturn(true);

        Resource result = spyService.getDownloadLogFileUrl(filename, secretKey);

        assertNotNull(result);
        assertEquals(expectedResource.getFilename(), result.getFilename());
    }
}