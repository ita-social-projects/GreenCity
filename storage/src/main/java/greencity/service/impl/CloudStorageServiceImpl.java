package greencity.service.impl;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import greencity.service.FileService;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class CloudStorageServiceImpl implements FileService {
    private final String bucketName;
    private final Storage storage;
    private final String staticUrl;


    /**
     * Constructor with parameters.
     */
    public CloudStorageServiceImpl(@Value("${bucketName}") final String bucketName,
                                   @Value("${staticUrl}") final String staticUrl) {
        this.bucketName = bucketName;
        this.staticUrl = staticUrl;

        this.storage = StorageOptions.newBuilder().build().getService();
    }

    @SneakyThrows
    @Override
    public URL upload(MultipartFile multipartfile) {
        final String contentType = multipartfile.getContentType();
        final String blob = UUID.randomUUID().toString();
        final BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blob).setContentType(contentType).build();
        storage.create(blobInfo, multipartfile.getBytes());
        log.info("==file uploaded");
        return getURL(blobInfo);
    }

    private URL getURL(BlobInfo blobInfo) throws MalformedURLException {
        log.info("==file uploaded" + staticUrl + blobInfo.getBucket() + "/" + blobInfo.getName());
        return new URL(staticUrl + blobInfo.getBucket() + "/" + blobInfo.getName());
    }
}