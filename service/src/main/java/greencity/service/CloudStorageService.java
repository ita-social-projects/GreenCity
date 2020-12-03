package greencity.service;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.NotSavedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.PropertyResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.UUID;

@Service
public class CloudStorageService implements FileService {
    private final String staticUrl;
    private final String bucketName;
    private final Storage storage;
    private final ModelMapper modelMapper;

    /**
     * Constructor with parameters.
     */
    public CloudStorageService(@Autowired PropertyResolver propertyResolver,
        ModelMapper modelMapper) {
        this.bucketName = propertyResolver.getProperty("bucketName");
        this.staticUrl = propertyResolver.getProperty("staticUrl");
        this.storage = StorageOptions.newBuilder().build().getService();
        this.modelMapper = modelMapper;
    }

    /**
     * {@inheritDoc}
     */
    public URL upload(final MultipartFile multipartFile) {
        try {
            final String contentType = multipartFile.getContentType();
            final String blob = UUID.randomUUID().toString();
            final BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blob).setContentType(contentType).build();

            return write(blobInfo, multipartFile);
        } catch (IOException ex) {
            throw new NotSavedException(ErrorMessage.FILE_NOT_SAVED);
        }
    }

    private URL write(final BlobInfo blobInfo, final MultipartFile multipartFile) throws IOException {
        try (WriteChannel writer = storage.writer(blobInfo)) {
            final byte[] buffer = new byte[1024];
            InputStream input = multipartFile.getInputStream();
            int limit;
            while ((limit = input.read(buffer)) >= 0) {
                writer.write(ByteBuffer.wrap(buffer, 0, limit));
            }
        }
        return getURL(blobInfo);
    }

    private URL getURL(BlobInfo blobInfo) throws MalformedURLException {
        return new URL(staticUrl + blobInfo.getBucket() + "/" + blobInfo.getName());
    }

    /**
     * Convert string to MultipartFile.
     *
     * @return MultipartFile.
     **/
    public MultipartFile convertToMultipartImage(String image) {
        return modelMapper.map(image, MultipartFile.class);
    }
}
