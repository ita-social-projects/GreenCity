package greencity.service;

import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.PropertyResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AzureCloudStorageServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PropertyResolver propertyResolver;

    @InjectMocks
    private AzureCloudStorageService azureCloudStorageService;

    @Test
    void convertToMultipartImageThrowsBadRequestException() {
        when(modelMapper.map("Image", MultipartFile.class)).thenThrow(new BadRequestException("S"));
        assertThrows(BadRequestException.class, () -> azureCloudStorageService.convertToMultipartImage("Image"));
    }

    @Test
    void convertToMultipartImage() {
        MultipartFile multipartFile = new MockMultipartFile("Image", "Image".getBytes(StandardCharsets.UTF_8));
        when(modelMapper.map("Image", MultipartFile.class)).thenReturn(multipartFile);
        assertEquals(multipartFile, azureCloudStorageService.convertToMultipartImage("Image"));
    }
}