package greencity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LogFileServiceImplSanitizeFilenameTest {

    @InjectMocks
    private LogFileServiceImpl logFileService;

    @Mock
    private DotenvService dotenvService;

    @Test
    public void sanitizeFilenameListShouldReturnUnchangedFilenameWhenFilenameContainsOnlyValidCharactersTest() {
        String filename = "valid_filename123.txt";
        String result = logFileService.sanitizeFilename(filename);
        assertEquals("valid_filename123.txt", result);
    }

    @Test
    public void sanitizeFilenameListShouldReturnSanitizedFilenameWhenFilenameContainsForbiddenCharactersTest() {
        String filename = "invalid@filename#.txt";
        String result = logFileService.sanitizeFilename(filename);
        assertEquals("invalid_filename_.txt", result);
    }

    @Test
    public void sanitizeFilenameListShouldReturnEmptyStringWhenFilenameIsEmptyTest() {
        String filename = "";
        String result = logFileService.sanitizeFilename(filename);
        assertEquals("", result);
    }

    @Test
    public void sanitizeFilenameListShouldReturnSanitizedFilenameWhenFilenameContainsOnlyForbiddenCharactersTest() {
        String filename = "@#$.txt";
        String result = logFileService.sanitizeFilename(filename);
        assertEquals("___.txt", result);
    }

    @Test
    public void sanitizeFilenameListShouldReturnSanitizedFilenameWhenFilenameContainsSpacesTest() {
        String filename = "file name with spaces.txt";
        String result = logFileService.sanitizeFilename(filename);
        assertEquals("file_name_with_spaces.txt", result);
    }
}
