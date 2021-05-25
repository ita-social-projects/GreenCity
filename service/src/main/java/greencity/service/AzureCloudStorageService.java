package greencity.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotSavedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.PropertyResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class AzureCloudStorageService implements FileService {
    private final String connectionString;
    private final String containerName;
    private final ModelMapper modelMapper;

    /**
     * Constructor with parameters.
     */
    public AzureCloudStorageService(@Autowired PropertyResolver propertyResolver,
        ModelMapper modelMapper) {
        this.connectionString = propertyResolver.getProperty("azure.connection.string");
        this.containerName = propertyResolver.getProperty("azure.container.name");
        this.modelMapper = modelMapper;
    }

    /**
     * {@inheritDoc}
     */
    public String upload(MultipartFile multipartFile) {
        final String blob = UUID.randomUUID().toString();
        BlobClient client = containerClient()
            .getBlobClient(blob + multipartFile.getOriginalFilename());
        try {
            client.upload(multipartFile.getInputStream(), multipartFile.getSize());
        } catch (IOException e) {
            throw new NotSavedException(ErrorMessage.FILE_NOT_SAVED);
        }
        return client.getBlobUrl();
    }

    private BlobContainerClient containerClient() {
        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
            .connectionString(connectionString).buildClient();
        return serviceClient.getBlobContainerClient(containerName);
    }

    /**
     * {@inheritDoc}
     */
    public MultipartFile convertToMultipartImage(String image) {
        try {
            return modelMapper.map(image, MultipartFile.class);
        } catch (Exception e) {
            throw new BadRequestException(ErrorMessage.MULTIPART_FILE_BAD_REQUEST + image);
        }
    }
}
