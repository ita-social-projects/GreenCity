package greencity.service;

import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.PropertyResolver;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

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
        AzureCloudStorageService azureCloudStorageService = new AzureCloudStorageService(propertyResolver, modelMapper);
        when(modelMapper.map("Image", MultipartFile.class)).thenReturn((null));
        assertThrows(BadRequestException.class, () -> azureCloudStorageService.convertToMultipartImage(any()));
    }
}