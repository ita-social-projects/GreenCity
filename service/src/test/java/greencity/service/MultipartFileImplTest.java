package greencity.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@ExtendWith(MockitoExtension.class)
class MultipartFileImplTest {
    @InjectMocks
    MultipartFileImpl multipartFile;

    @Test
    public void testMultipartFileMethods() throws IOException {
        String name = "file";
        String originalFilename = "test.txt";
        String contentType = "text/plain";
        byte[] content = "Greencity test!".getBytes();

        multipartFile = new MultipartFileImpl(name, originalFilename, contentType, content);

        assertAll(
            () -> assertEquals(name, multipartFile.getName()),
            () -> assertEquals(originalFilename, multipartFile.getOriginalFilename()),
            () -> assertEquals(contentType, multipartFile.getContentType()),
            () -> assertFalse(multipartFile.isEmpty()),
            () -> assertEquals(content.length, multipartFile.getSize()),
            () -> assertArrayEquals(content, multipartFile.getBytes()));

        File tempFile = File.createTempFile("test1", ".txt");
        multipartFile.transferTo(tempFile);

        byte[] fileContent = Files.readAllBytes(tempFile.toPath());
        assertArrayEquals(content, fileContent);

        InputStream byteInputStream = multipartFile.getInputStream();
        assertArrayEquals(content, byteInputStream.readAllBytes());
    }
}