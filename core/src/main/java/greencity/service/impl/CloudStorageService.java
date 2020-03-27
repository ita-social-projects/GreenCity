package greencity.service.impl;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.NotSavedException;
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
public class CloudStorageService implements FileService {
    private final String staticUrl;
    private final String bucketName;
    private final Storage storage;


    /**
     * Constructor with parameters.
     */
    public CloudStorageService(@Value("${BUCKET_NAME}") final String bucketName,
                               @Value("${STATIC_URL}") final String staticUrl) {
        this.bucketName = bucketName;
        this.staticUrl = staticUrl;
        this.storage = StorageOptions.newBuilder().build().getService();
    }


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
        return new URL(staticUrl + blobInfo.getBucket() + "/" + blobInfo.getName());
    }
}