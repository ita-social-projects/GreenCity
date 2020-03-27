package greencity.service.impl;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import greencity.service.FileService;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudStorageServiceImpl implements FileService {
    final String staticUrl;
    final String projectId;
    final StorageOptions.Builder optionsBuilder;
    private final String bucketName;
    private final Storage storage;


    /**
     * Constructor with parameters.
     */
    public CloudStorageServiceImpl(@Value("${PROJECT_ID}") final String projectId,
                                   @Value("${BUCKET_NAME}") final String bucketName,
                                   @Value("${STATIC_URL") String staticUrl) {
        this.projectId = projectId;
        this.bucketName = bucketName;
        this.staticUrl = staticUrl;
        this.optionsBuilder = StorageOptions.newBuilder();
        this.storage = optionsBuilder.build().getService();
    }


    @Override
    public URL upload(final MultipartFile multipartFile) throws IOException {
        final String contentType = multipartFile.getContentType();
        final String destinationName = UUID.randomUUID().toString();
        final BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, destinationName).setContentType(contentType).build();

        return write(blobInfo, multipartFile);
    }

    private URL write(final BlobInfo blobInfo, final MultipartFile multipartFile) throws IOException {
        WriteChannel writer = storage.writer(blobInfo);
        final byte[] buffer = new byte[1024];
        InputStream input = multipartFile.getInputStream();
        int limit;
        while ((limit = input.read(buffer)) >= 0) {
            writer.write(ByteBuffer.wrap(buffer, 0, limit));
        }
        return getURL(blobInfo);
    }

    private URL getURL(BlobInfo blobInfo) throws MalformedURLException {
        StringBuilder newURL = new StringBuilder(staticUrl);
        newURL.replace(34, 34, blobInfo.getName());
        newURL.replace(33, 33, blobInfo.getBucket());
        return new URL(newURL.toString());
    }
}
