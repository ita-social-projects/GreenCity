package greencity.service;

import greencity.ModelUtils;
import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.PropertyResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AzureCloudStorageServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PropertyResolver propertyResolver;

    @Test
    void convertToMultipartImageThrowsBadRequestException() {
        MultipartFile expected = ModelUtils.getFile();
        MultipartFile multipartFile = new MockMultipartFile("Image", "sada".getBytes(StandardCharsets.UTF_8));
        AzureCloudStorageService azureCloudStorageService = new AzureCloudStorageService(propertyResolver, modelMapper);
        when(modelMapper.map("Image", MultipartFile.class)).thenReturn((multipartFile));
        assertEquals(expected.isEmpty(), azureCloudStorageService.convertToMultipartImage("Image").isEmpty());
        assertThrows(BadRequestException.class, () -> azureCloudStorageService.convertToMultipartImage(any()));
    }
}