package greencity.service.impl;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import greencity.service.FileService;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudStorageServiceImpl implements FileService {
    final String projectId;
    final StorageOptions.Builder optionsBuilder;
    private final String bucketName;
    private final Storage storage;

    /**
     * Constructor with parameters.
     */
    public CloudStorageServiceImpl(@Value("${PROJECT_ID}") final String projectId,
                                   @Value("${BUCKET_NAME}") final String bucketName) {
        this.projectId = projectId;
        this.bucketName = bucketName;
        this.optionsBuilder = StorageOptions.newBuilder();
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalArgumentException("Bucket name is null");
        }
        if (projectId != null && !projectId.isEmpty()) {
            optionsBuilder.setProjectId(projectId);
        }
        this.storage = optionsBuilder.build().getService();
    }


    @Override
    public URL upload(final MultipartFile multipartFile) throws IOException {
        try {
            final String contentType = multipartFile.getContentType();
            final String blob = UUID.randomUUID().toString();
            final BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blob).setContentType(contentType).build();

            return write(storage, blobInfo, multipartFile);
        } catch (IOException ex) {
            throw ex;
        }
    }

    private static URL write(final Storage storage,
                             final BlobInfo blobInfo, final MultipartFile multipartFile) throws IOException {
        try (WriteChannel writer = storage.writer(blobInfo)) {
            final byte[] buffer = new byte[1024];
            try (InputStream input = multipartFile.getInputStream()) {
                int limit;
                while ((limit = input.read(buffer)) >= 0) {
                    try {
                        writer.write(ByteBuffer.wrap(buffer, 0, limit));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        return new URL("https://storage.cloud.google.com/" + blobInfo.getBucket()
            + "/" + blobInfo.getName() + "?authuser=0");
    }
}
