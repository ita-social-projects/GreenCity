package greencity.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LogFileServiceImplSanitizeFilenameTest {

//    @InjectMocks
//    private LogFileServiceImpl logFileService;
//
//    @Mock
//    private DotenvService dotenvService;
//
//    private static Stream<Arguments> filenameProvider() {
//        return Stream.of(
//            Arguments.of("valid_filename123.txt", "valid_filename123.txt"),
//            Arguments.of("invalid@filename#.txt", "invalid_filename_.txt"),
//            Arguments.of("", ""),
//            Arguments.of("@#$.txt", "___.txt"),
//            Arguments.of("file name with spaces.txt", "file_name_with_spaces.txt"));
//    }
//
//    @ParameterizedTest
//    @MethodSource("filenameProvider")
//    void sanitizeFilenameShouldReturnSanitizedFilenameTest(String input, String expected) {
//        String result = logFileService.sanitizeFilename(input);
//        assertEquals(expected, result);
//    }
}
